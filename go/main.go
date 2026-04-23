package main

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"log"
	"math/big"
	"net/http"
	"strings"
	"time"

	"github.com/ethereum/go-ethereum"
	"github.com/ethereum/go-ethereum/accounts/abi"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/core/types"
	"github.com/ethereum/go-ethereum/crypto"
	"github.com/ethereum/go-ethereum/ethclient"
)

const abiJSON = `[
    {
      "anonymous": false,
      "inputs": [
        {
          "indexed": true,
          "internalType": "address",
          "name": "from",
          "type": "address"
        },
        {
          "indexed": false,
          "internalType": "uint256",
          "name": "amount",
          "type": "uint256"
        }
      ],
      "name": "Deposit",
      "type": "event"
    },
    {
      "anonymous": false,
      "inputs": [
        {
          "indexed": true,
          "internalType": "address",
          "name": "from",
          "type": "address"
        },
        {
          "indexed": false,
          "internalType": "uint256",
          "name": "amount",
          "type": "uint256"
        }
      ],
      "name": "Withdraw",
      "type": "event"
    },
    {
      "inputs": [
        {
          "internalType": "address",
          "name": "",
          "type": "address"
        }
      ],
      "name": "balances",
      "outputs": [
        {
          "internalType": "uint256",
          "name": "",
          "type": "uint256"
        }
      ],
      "stateMutability": "view",
      "type": "function"
    },
    {
      "inputs": [],
      "name": "deposit",
      "outputs": [],
      "stateMutability": "payable",
      "type": "function"
    },
    {
      "inputs": [],
      "name": "getBalance",
      "outputs": [
        {
          "internalType": "uint256",
          "name": "",
          "type": "uint256"
        }
      ],
      "stateMutability": "view",
      "type": "function"
    },
    {
      "inputs": [
        {
          "internalType": "uint256",
          "name": "amount",
          "type": "uint256"
        }
      ],
      "name": "withdraw",
      "outputs": [],
      "stateMutability": "payable",
      "type": "function"
    }
  ]`

type TransactionRequest struct {
	UserAddress string  `json:"userAddress"`
	TxType      string  `json:"txType"`
	Amount      float64 `json:"amount"`
}

type EthBalanceResponse struct {
	Address    string  `json:"address"`
	Balance    float64 `json:"balance"`
	UsdtValue  float64 `json:"usdtValue"`
}

var ethClient *ethclient.Client

func main() {
	// connect to hardhat node
	rpcUrl := "ws://127.0.0.1:8545"
	client, err := ethclient.Dial(rpcUrl)
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println("Connected to Hardhat Node successfully")

	// 设置全局客户端用于HTTP接口
	httpRpcUrl := "http://127.0.0.1:8545"
	ethClient, err = ethclient.Dial(httpRpcUrl)
	if err != nil {
		log.Fatal("Failed to connect to Ethereum client for HTTP API:", err)
	}

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// contract address
	contractAddress := common.HexToAddress("0x5FbDB2315678afecb367f032d93F642f64180aa3")

	// analysis ABI
	parsedABI, err := abi.JSON(strings.NewReader(abiJSON))
	if err != nil {
		log.Fatal("abi analysis error", err)
	}

	// listen event
	query := ethereum.FilterQuery{
		Addresses: []common.Address{contractAddress},
	}
	logs := make(chan types.Log)
	sub, err := client.SubscribeFilterLogs(context.Background(), query, logs)
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println("Listening for events... started ")

	// 启动HTTP服务器
	go startHttpServer()

	// handle event
	for {
		select {
		case err := <-sub.Err():
			log.Printf("Subscription error: %v, attempting to reconnect...\n", err)
			// Reconnect
			time.Sleep(5 * time.Second)
			sub, err = client.SubscribeFilterLogs(ctx, query, logs)
			if err != nil {
				log.Printf("Reconnection failed: %v\n", err)
				continue
			}
			fmt.Println("reconnected successfully")
		case logData := <-logs:
			userAddress := common.HexToAddress(string(logData.Topics[1].Hex()))
			// match Deposit event
			if logData.Topics[0] == crypto.Keccak256Hash([]byte("Deposit(address,uint256)")) {
				var event struct {
					User   common.Address
					Amount *big.Int
				}
				parsedABI.UnpackIntoInterface(&event, "Deposit", logData.Data)
				fmt.Println("Deposit User:", userAddress, "Amount:", weiToEth(event.Amount))
				sendToJavaRiskService(userAddress.Hex(), "DEPOSIT", weiToEth(event.Amount))
			}
			// match Withdraw event
			if logData.Topics[0] == crypto.Keccak256Hash([]byte("Withdraw(address,uint256)")) {
				var event struct {
					User   common.Address
					Amount *big.Int
				}
				parsedABI.UnpackIntoInterface(&event, "Withdraw", logData.Data)
				fmt.Println("Withdraw User:", userAddress, "Amount:", weiToEth(event.Amount))
				sendToJavaRiskService(userAddress.Hex(), "WITHDRAW", weiToEth(event.Amount))
			}
		case <-ctx.Done():
			fmt.Println("Stopped listening for events")
			return
		}
	}
}

// 启动HTTP服务器
func startHttpServer() {
	http.HandleFunc("/eth/balance/", handleGetEthBalance)
	fmt.Println("HTTP server started on :8085")
	if err := http.ListenAndServe(":8085", nil); err != nil {
		log.Fatal("HTTP server error:", err)
	}
}

// 处理获取ETH余额请求
func handleGetEthBalance(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Received request for ETH balance:", r.URL.Path)
	// 从URL路径中提取地址
	path := r.URL.Path
	address := strings.TrimPrefix(path, "/eth/balance/")

	if address == "" {
		http.Error(w, "Address is required", http.StatusBadRequest)
		return
	}
	fmt.Println("Extracted address:", address)
	// 验证地址格式
	if !common.IsHexAddress(address) {
		http.Error(w, "Invalid Ethereum address format", http.StatusBadRequest)
		return
	}

	// 获取余额
	balance, err := getEthBalance(address)
	if err != nil {
		http.Error(w, fmt.Sprintf("Failed to get balance: %v", err), http.StatusInternalServerError)
		return
	}
	fmt.Println("Balance:", balance)
	// 返回JSON响应
	response := EthBalanceResponse{
		Address:   address,
		Balance:   balance,
		UsdtValue: balance, // 暂时使用 ETH 余额作为 USDT 价值（实际项目中应该查询汇率）
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}

// 获取ETH余额
func getEthBalance(address string) (float64, error) {
	account := common.HexToAddress(address)
	balance, err := ethClient.BalanceAt(context.Background(), account, nil)
	if err != nil {
		return 0, err
	}

	// 将wei转换为ETH
	f := new(big.Float).SetInt(balance)
	ethValue := new(big.Float).Quo(f, big.NewFloat(1e18))
	res, _ := ethValue.Float64()
	return res, nil
}

// send transaction data to Java SpringBoot risk service
func sendToJavaRiskService(userAddr, txType string, amount float64) {
	url := "http://localhost:8080/api/risk/check"

	reqBody := TransactionRequest{
		UserAddress: userAddr,
		TxType:      txType,
		Amount:      amount,
	}

	jsonData, _ := json.Marshal(reqBody)
	resp, err := http.Post(url, "application/json", bytes.NewBuffer(jsonData))
	if err != nil {
		fmt.Println("Failed to call Java service:", err)
		return
	}
	defer resp.Body.Close()
}

// tools : wei to ether
func weiToEth(wei *big.Int) float64 {
	f := new(big.Float).SetInt(wei)
	ethValue := new(big.Float).Quo(f, big.NewFloat(1e18))
	res, _ := ethValue.Float64()
	return res
}
