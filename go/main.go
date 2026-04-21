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

func main() {
	// connect to hardhat node
	rpcUrl := "ws://127.0.0.1:8545"
	client, err := ethclient.Dial(rpcUrl)
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println("Connected to Hardhat Node successfully")

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
