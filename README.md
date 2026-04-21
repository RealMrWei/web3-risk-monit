# Web3 Risk Monitor System

A high-performance on-chain risk monitoring system that detects and analyzes blockchain transactions in real-time. This project demonstrates a complete Web3 monitoring pipeline from smart contract deployment to risk assessment.

## 📋 Project Overview

This system monitors Ethereum blockchain events from a Vault smart contract, captures deposit/withdrawal transactions, and performs risk analysis based on transaction amounts. The architecture follows a microservices pattern with three main components:

- **Smart Contract Layer**: Solidity-based Vault contract with event emission
- **Data Collection Layer**: Go-based event listener using WebSocket subscription
- **Risk Analysis Layer**: Java Spring Boot service for transaction risk assessment

## 🏗️ Architecture

```
┌─────────────────┐       WebSocket        ┌──────────────────┐       HTTP POST       ┌────────────────────┐
│                 │ ◄────────────────────► │                  │ ◄───────────────────► │                    │
│  Hardhat Node   │    Event Streaming     │  Go Listener     │   Transaction Data    │  Java Risk Service │
│  (Local RPC)    │                        │  (go-ethereum)   │                       │  (Spring Boot)     │
│                 │                        │                  │                       │                    │
└─────────────────┘                        └──────────────────┘                       └────────────────────┘
     Port: 8545                                    Real-time                              Port: 8080
                                                  Monitoring                          Risk Assessment Logic
```

## 🚀 Features

### 1. Smart Contract (Solidity/Hardhat) - 10%
- **Vault Contract**: Implements basic deposit and withdrawal functionality
- **Event Emission**: Emits `Deposit` and `Withdraw` events with indexed addresses
- **Security Best Practices**: 
  - Gas-limited transfers (`gas: 2300`) to prevent reentrancy attacks
  - Balance validation before withdrawals
  - Proper access control via `msg.sender`
- **Comprehensive Testing**: Mocha/Chai test suite covering deployment, deposits, and withdrawals

**Key Files:**
- `solidity/contracts/Vault.sol` - Main contract implementation
- `solidity/test/Vault.test.js` - Test cases (3 test scenarios)

### 2. Event Collection Service (Go) - 40%
- **WebSocket Subscription**: Real-time event listening using `eth_subscribe`
- **ABI Parsing**: Decodes contract events using go-ethereum ABI parser
- **Auto-Reconnection**: Handles subscription errors with automatic reconnection logic
- **Indexed Parameter Handling**: Correctly extracts indexed address parameters from `Topics[1]`
- **HTTP Integration**: Forwards transaction data to Java risk service

**Technical Highlights:**
- Uses `ws://` protocol for WebSocket connection to Hardhat node
- Implements proper error handling and graceful shutdown via context cancellation
- Converts Wei to Ether for human-readable amount reporting
- Follows Solidity event parsing best practices (indexed vs non-indexed parameters)

**Key File:**
- `go/main.go` - Complete event listener implementation (~220 lines)

### 3. Risk Assessment Service (Java/Spring Boot) - 30%
- **RESTful API**: Exposes `/api/risk/check` endpoint for transaction analysis
- **Business Logic**: Detects large transactions (>2 ETH threshold)
- **Health Check**: Provides `/api/risk/health` endpoint for service monitoring
- **Clean Architecture**: Separation of concerns with Controller-Service pattern

**Risk Rules:**
- Transactions > 2 ETH → Flagged as "large transaction detected"
- Transactions ≤ 2 ETH → Marked as "transaction passed"

**Key Files:**
- `java/src/main/java/com/web3/risk_service/RiskController.java` - REST controller
- `java/src/main/java/com/web3/risk_service/RiskService.java` - Business logic
- `java/src/main/java/com/web3/risk_service/TransactionRequest.java` - Data model
- `java/pom.xml` - Maven dependencies (Spring Boot 4.0.5, Java 17)

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Smart Contract | Solidity | ^0.8.20 |
| Development Framework | Hardhat | Latest |
| Testing | Mocha + Chai | - |
| Event Listener | Go | 1.25.4 |
| Ethereum Library | go-ethereum | v1.17.2 |
| Backend Service | Java | 17 |
| Web Framework | Spring Boot | 4.0.5 |
| Build Tool | Maven | - |

## 📦 Installation & Setup

### Prerequisites
- Node.js & npm
- Go 1.25+
- Java 17+
- Maven

### 1. Smart Contract Setup

```bash
cd solidity
npm install
npx hardhat compile
npx hardhat test
npx hardhat node
```

The Hardhat node will start at `http://127.0.0.1:8545` with WebSocket support at `ws://127.0.0.1:8545`.

### 2. Deploy Contract

In a new terminal:
```bash
cd solidity
npx hardhat run scripts/deploy.js --network localhost
```

Note the deployed contract address (default: `0x5FbDB2315678afecb367f032d93F642f64180aa3`).

### 3. Start Go Event Listener

```bash
cd go
go mod download
go run main.go
```

The listener will connect to the Hardhat node via WebSocket and start monitoring events.

### 4. Start Java Risk Service

```bash
cd java
mvn spring-boot:run
```

The service will start at `http://localhost:8080`.

## 🧪 Testing

### Smart Contract Tests
```bash
cd solidity
npx hardhat test
```

Expected output:
- ✅ Deploy success
- ✅ Deposit event emission
- ✅ Withdraw event emission

### Integration Test

1. Start all three services (Hardhat node, Go listener, Java service)
2. Interact with the contract:
```bash
cd solidity
npx hardhat run scripts/testTx.js --network localhost
```

This will execute two test transactions:
- **Test 1**: Deposit 1 ETH (normal transaction)
- **Test 2**: Deposit 3 ETH (large transaction triggering risk alert)

3. Observe:
   - Go console logs showing detected events
   - Java service console showing risk assessment results
   - Large deposits (>2 ETH) trigger risk warnings

## 🔍 Key Implementation Details

### Event Parsing Strategy
The Go listener correctly handles Solidity event parameters:
- **Indexed parameters** (address): Extracted from `logData.Topics[1]`
- **Non-indexed parameters** (uint256): Parsed from `logData.Data` using ABI unpacking

### Security Considerations
- Vault contract uses `gas: 2300` limit for ETH transfers to prevent reentrancy
- Input validation ensures positive amounts for deposits/withdrawals
- Balance checks prevent overdrafts

### Error Handling
- Go service implements auto-reconnection for WebSocket failures
- Context-based cancellation for graceful shutdown
- Java service provides health check endpoint for monitoring

## 📊 Project Completion Status

| Module | Weight | Status | Description |
|--------|--------|--------|-------------|
| Smart Contract | 10% | ✅ Complete | Vault contract with tests |
| Event Collection | 40% | ✅ Complete | Go WebSocket listener |
| Business Logic | 30% | ✅ Complete | Java risk assessment |
| Documentation | 20% | ✅ Complete | This README |

**Total Completion: 100%** ✅

## 🎯 Use Cases

This system can be extended for:
- Real-time whale movement tracking
- AML (Anti-Money Laundering) compliance
- DeFi protocol monitoring
- Automated alert systems for suspicious activities
- Portfolio risk management

## 📝 Notes

- The contract address is hardcoded in `go/main.go` - update if redeploying
- Risk threshold (2 ETH) can be adjusted in `RiskService.java`
- WebSocket connection requires Hardhat node to support subscriptions
- For production use, consider adding authentication, rate limiting, and persistent storage

## 🤝 Contributing

Feel free to submit issues and enhancement requests!

## 📄 License

This project is open source and available under the MIT License.
