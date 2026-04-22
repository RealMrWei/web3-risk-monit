# Web3 Risk Monitor System

A high-performance on-chain risk monitoring system that detects and analyzes blockchain transactions in real-time. This project demonstrates a complete Web3 monitoring pipeline from smart contract deployment to risk assessment, enhanced with AI-powered intelligent analysis.

## 📋 Project Overview

This system monitors Ethereum blockchain events from a Vault smart contract, captures deposit/withdrawal transactions, and performs risk analysis based on transaction amounts. The architecture follows a microservices pattern with four main components:

- **Smart Contract Layer**: Solidity-based Vault contract with event emission
- **Data Collection Layer**: Go-based event listener using WebSocket subscription
- **Risk Analysis Layer**: Java Spring Boot service for transaction risk assessment
- **AI Agent Layer**: Spring AI-powered intelligent agent for natural language risk queries

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
                                                                                              ▲
                                                                                              │
                                                                                              │ HTTP
                                                                                              ▼
                                                                                    ┌────────────────────┐
                                                                                    │                    │
                                                                                    │   AI Agent Service │
                                                                                    │   (Spring AI)      │
                                                                                    │                    │
                                                                                    └────────────────────┘
                                                                                         Port: 8081
                                                                                  Natural Language Interface
                                                                                        Function Calling
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
- `java/risk-service/src/main/java/com/web3/risk_service/RiskController.java` - REST controller
- `java/risk-service/src/main/java/com/web3/risk_service/RiskService.java` - Business logic
- `java/risk-service/src/main/java/com/web3/risk_service/TransactionRequest.java` - Data model
- `java/risk-service/pom.xml` - Maven dependencies (Spring Boot 4.0.5, Java 17)

### 4. AI Agent Service (Java/Spring AI) - 20%
- **Natural Language Interface**: Accepts risk queries in natural language
- **Function Calling**: AI automatically invokes risk detection tools based on user intent
- **Spring AI Integration**: Uses SiliconFlow (Qwen2.5-7B) as the LLM backend
- **Tool Registration**: Configurable function callbacks for custom business logic
- **Microservice Architecture**: Independent service with OpenFeign integration

**Core Capabilities:**
- Transaction risk assessment via natural language
- Automatic parameter extraction from user messages
- Intelligent tool selection and invocation
- Context-aware risk analysis responses

**Technical Highlights:**
- Spring AI 1.0+ with ChatClient builder pattern
- FunctionCallback configuration with inputType specification
- SiliconFlow OpenAI-compatible API integration
- Constructor-based dependency injection with Lombok

**Key Files:**
- `java/ai-agent-service/src/main/java/com/web3/ai/controller/AiAgentController.java` - REST endpoints
- `java/ai-agent-service/src/main/java/com/web3/ai/service/impl/OpenAiAgentServiceImpl.java` - AI service implementation
- `java/ai-agent-service/src/main/java/com/web3/ai/config/FunctionConfig.java` - Function calling configuration
- `java/ai-agent-service/src/main/resources/application.yml` - SiliconFlow API configuration

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
| AI Framework | Spring AI | 1.0.0-M6 |
| LLM Provider | SiliconFlow (Qwen2.5-7B) | - |
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

### 4. Start AI Agent Service

```bash
cd java/ai-agent-service
mvn spring-boot:run
```

The service will start at `http://localhost:8081`.

**Environment Variables Required:**
```bash
# Set your SiliconFlow API key
export SILICONFLOW_API_KEY="your-api-key-here"
```

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

1. Start all services (Hardhat node, Go listener, Java risk service, AI agent)
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

### AI Agent Test

Test the AI-powered risk assessment:

```bash
# Test 1: Low risk transaction
curl -X POST http://localhost:8081/ai/agent/chattorisk \
  -H "Content-Type: application/json" \
  -d '{"message":"检测地址 0x111 向 0x222 转账 100 ETH 的风险"}'

# Test 2: High risk transaction
curl -X POST http://localhost:8081/ai/agent/chattorisk \
  -H "Content-Type: application/json" \
  -d '{"message":"检测地址 0xAlice 向 0xBob 转账 5000 ETH 的风险"}'

# Test 3: Regular chat (no tool invocation)
curl -X POST http://localhost:8081/ai/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"你好，请介绍一下这个系统"}'
```

Expected behavior:
- Transactions > 1000 ETH → AI detects high risk
- Transactions ≤ 1000 ETH → AI detects low risk
- Non-risk queries → AI responds conversationally

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

## 📚 API Documentation

### Risk Assessment Service (Port 8080)

**POST** `/api/risk/check`
Checks transaction risk level.

Request Body:
```json
{
  "from": "0x123...",
  "to": "0x456...",
  "amount": 1.5
}
```

**GET** `/api/risk/health`
Returns service health status.

### AI Agent Service (Port 8081)

**POST** `/ai/agent/chat`
General chat endpoint for natural language interaction.

Request Body:
```json
{
  "message": "你好，请介绍自己"
}
```

**POST** `/ai/agent/chattorisk`
Risk assessment endpoint using AI agent with function calling.

Request Body:
```json
{
  "message": "检测地址 0x111 向 0x222 转账 100 ETH 的风险"
}
```

Response:
```json
{
  "answer": "交易的风险等级为低，发送地址为0x111，接收地址为0x222，交易金额为100。链上交易风险检测已完成。"
}
```

## 📊 Project Completion Status

| Module | Weight | Status | Description |
|--------|--------|--------|-------------|
| Smart Contract | 10% | ✅ Complete | Vault contract with tests |
| Event Collection | 40% | ✅ Complete | Go WebSocket listener |
| Business Logic | 20% | ✅ Complete | Java risk assessment |
| AI Agent | 20% | ✅ Complete | Spring AI-powered agent |
| Documentation | 10% | ✅ Complete | This README |

**Total Completion: 100%** ✅

## 🎯 Use Cases

This system can be extended for:
- Real-time whale movement tracking
- AML (Anti-Money Laundering) compliance
- DeFi protocol monitoring
- Automated alert systems for suspicious activities
- Portfolio risk management
- **Natural language risk queries via AI agent**
- **Intelligent transaction analysis with function calling**
- **Multi-turn conversational risk assessment**

## 📝 Notes

- The contract address is hardcoded in `go/main.go` - update if redeploying
- Risk threshold (2 ETH) can be adjusted in `RiskService.java`
- WebSocket connection requires Hardhat node to support subscriptions
- For production use, consider adding authentication, rate limiting, and persistent storage
- **AI Agent requires SiliconFlow API key configured in `application.yml`**
- **Function calling threshold (1000 ETH) can be adjusted in `FunctionConfig.java`**
- **AI model automatically decides when to invoke risk detection tools based on user intent**

## 🤝 Contributing

Feel free to submit issues and enhancement requests!

## 📄 License

This project is open source and available under the MIT License.
