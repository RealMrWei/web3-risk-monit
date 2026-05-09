# Web3 Risk Monitor System

A high-performance on-chain risk monitoring system that detects and analyzes blockchain transactions in real-time. This project demonstrates a complete Web3 monitoring pipeline from smart contract deployment to risk assessment, enhanced with AI-powered intelligent analysis.

## 📋 Project Overview

This system monitors Ethereum blockchain events from a Vault smart contract, captures deposit/withdrawal transactions, and performs risk analysis based on transaction amounts. The architecture follows a microservices pattern with four main components:

- **Smart Contract Layer**: Solidity-based Vault contract with event emission
- **Data Collection Layer**: Go-based event listener using WebSocket subscription
- **Risk Analysis Layer**: Java Spring Boot service for transaction risk assessment
- **AI Agent Layer**: Spring AI-powered intelligent agent for natural language risk queries with function calling capabilities

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
                                                                                              │ Feign Client
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
                                                                                 ┌────────┬─────────┬────────┐
                                                                                 │        │         │        │
                                                                                 ▼        ▼         ▼        ▼
                                                                          Account-Svc  Tx-Svc  Web3Go-Svc  LLM API
                                                                             :8084      :8084     :8085    SiliconFlow
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
- **ETH Balance Query API**: Provides REST endpoint for querying Ethereum address balances

**Technical Highlights:**
- Uses `ws://` protocol for WebSocket connection to Hardhat node
- Implements proper error handling and graceful shutdown via context cancellation
- Converts Wei to Ether for human-readable amount reporting
- Follows Solidity event parsing best practices (indexed vs non-indexed parameters)
- Exposes HTTP API at port 8085 for balance queries

**Key File:**
- `go/main.go` - Complete event listener implementation (~300 lines)

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
- **Function Calling**: AI automatically invokes tools based on user intent
- **Multi-Tool Integration**: 
  - Account Balance Query (via Feign → account-service:8084)
  - Transaction History Query (via Feign → transaction-service:8084)
  - ETH Balance Query (via Feign → web3go-service:8085)
- **Spring AI Integration**: Uses SiliconFlow (Qwen/Qwen2.5-7B-Instruct) as the LLM backend
- **Streaming Response**: Real-time token streaming for better user experience
- **Microservice Architecture**: Independent service with OpenFeign integration

**Core Capabilities:**
- Natural language transaction risk assessment
- Automatic parameter extraction from user messages
- Intelligent tool selection and invocation
- Context-aware multi-turn conversations
- Account and balance information queries

**Technical Highlights:**
- Spring AI 1.0+ with ChatClient builder pattern
- `@Tool` annotation for function registration (replaces deprecated `@AiFunction`)
- InputType specification for proper JSON schema generation
- SiliconFlow OpenAI-compatible API integration
- Constructor-based dependency injection with Lombok
- Stream-based response handling for real-time output

**Key Files:**
- `java/ai-agent-service/src/main/java/com/web3/ai/controller/AiAgentController.java` - REST endpoints
- `java/ai-agent-service/src/main/java/com/web3/ai/service/impl/OpenAiAgentServiceImpl.java` - AI service implementation
- `java/ai-agent-service/src/main/java/com/web3/ai/tools/AccountBalanceTool.java` - Account balance tool
- `java/ai-agent-service/src/main/java/com/web3/ai/tools/TransactionHistoryTool.java` - Transaction history tool
- `java/ai-agent-service/src/main/java/com/web3/ai/tools/EthBalanceTool.java` - ETH balance tool
- `java/ai-agent-service/src/main/resources/application.yml` - Configuration file

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Smart Contract | Solidity | ^0.8.20 |
| Development Framework | Hardhat | Latest |
| Testing | Mocha + Chai | - |
| Event Listener | Go | 1.25.4 |
| Ethereum Library | go-ethereum | v1.17.2 |
| Backend Service | Java | 17 |
| Web Framework | Spring Boot | 3.4.3 |
| Cloud Framework | Spring Cloud | 2024.0.0 |
| AI Framework | Spring AI | 1.0.0-M6 |
| LLM Provider | SiliconFlow (Qwen2.5-7B-Instruct) | - |
| Service Communication | OpenFeign | - |
| Build Tool | Maven | - |

## 📦 Installation & Setup

### Prerequisites
- Node.js & npm
- Go 1.25+
- Java 17+
- Maven 3.6+

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

The listener will:
- Connect to the Hardhat node via WebSocket (port 8545)
- Start monitoring contract events
- Expose HTTP API at port 8085 for balance queries

### 4. Start Java Risk Service

```bash
cd java/risk-service
mvn spring-boot:run
```

The service will start at `http://localhost:8080`.

### 5. Start AI Agent Service

**Important**: Ensure all dependent services are running first:
- account-service (port 8084)
- web3go-service (Go service, port 8085)
- Hardhat node (port 8545)

```bash
cd java/ai-agent-service
mvn clean install
mvn spring-boot:run
```

The service will start at `http://localhost:8081`.

**Configuration Required:**

Edit `java/ai-agent-service/src/main/resources/application.yml`:

```yaml
spring:
  ai:
    openai:
      api-key: your-siliconflow-api-key-here  # Get from https://siliconflow.cn
      base-url: https://api.siliconflow.cn/v1  # Note: must include /v1
      chat:
        options:
          model: Qwen/Qwen2.5-7B-Instruct
          stream: true  # Must be enabled
          temperature: 0.1  # Lower for tool calling
          max-tokens: 1024

feign:
  account-service:
    url: http://127.0.0.1:8084
  transaction-service:
    url: http://127.0.0.1:8084
  web3go-service:
    url: http://127.0.0.1:8085
```

**Get Your API Key:**
1. Visit [SiliconFlow Platform](https://siliconflow.cn)
2. Register and obtain your API key
3. Replace `your-siliconflow-api-key-here` in the configuration

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

### AI Agent Service Tests

#### Test 1: Account Balance Query
```bash
curl -X POST http://localhost:8081/ai/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"查询用户1的账户余额"}'
```

Expected: AI calls AccountBalanceTool and returns balance information

#### Test 2: Transaction History Query
```bash
curl -X POST http://localhost:8081/ai/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"用户1最近的交易记录是什么？"}'
```

Expected: AI calls TransactionHistoryTool and returns transaction list

#### Test 3: ETH Balance Query
```bash
curl -X POST http://localhost:8081/ai/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"查询地址 0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266 的ETH余额"}'
```

Expected: AI calls EthBalanceTool and returns ETH balance

#### Test 4: Risk Assessment
```bash
curl -X POST http://localhost:8081/ai/agent/chattorisk \
  -H "Content-Type: application/json" \
  -d '{"message":"检测这笔交易是否有风险"}'
```

Expected: AI performs risk analysis with tool invocation

#### Test 5: General Chat (No Tools)
```bash
curl -X POST http://localhost:8081/ai/agent/chatwithnotool \
  -H "Content-Type: application/json" \
  -d '{"message":"你好，请介绍一下你自己"}'
```

Expected: Conversational response without tool invocation

### Go Service API Test

Test the ETH balance query endpoint:

```bash
curl http://localhost:8085/eth/balance/0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266
```

Expected response:
```json
{
  "address": "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266",
  "balance": 10000.0,
  "usdtValue": 10000.0
}
```

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
- Feign clients configured with timeout protection (connect: 3s, read: 10s)

### AI Function Calling
- Tools are registered using `@Tool` annotation with descriptive names
- Input types explicitly specified for proper JSON schema generation
- Temperature set low (0.1) for deterministic tool selection
- Streaming enabled for real-time response delivery

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

### Go Web3 Service (Port 8085)

**GET** `/eth/balance/{address}`
Queries ETH balance for an Ethereum address.

Path Parameter:
- `address`: Ethereum address (e.g., `0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266`)

Response:
```json
{
  "address": "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266",
  "balance": 10000.0,
  "usdtValue": 10000.0
}
```

### AI Agent Service (Port 8081)

**POST** `/ai/agent/chat`
General chat endpoint with function calling support.

Request Body:
```json
{
  "message": "查询用户1的账户余额"
}
```

Response: Server-Sent Events (streaming)
```
data: {"answer":"用户1的账户余额为"}
data: {"answer":" 15800.50 ETH"}
data: {"answer":"，状态正常。"}
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
  "answer": "经过分析，该交易的风险等级为低..."
}
```

**POST** `/ai/agent/chatwithnotool`
Chat endpoint without tool invocation.

Request Body:
```json
{
  "message": "你好，请介绍自己"
}
```

Response:
```json
{
  "answer": "你好！我是Web3风控助手..."
}
```

## 📊 Project Completion Status

| Module | Weight | Status | Description |
|--------|--------|--------|-------------|
| Smart Contract | 10% | ✅ Complete | Vault contract with tests |
| Event Collection | 40% | ✅ Complete | Go WebSocket listener + HTTP API |
| Business Logic | 20% | ✅ Complete | Java risk assessment |
| AI Agent | 20% | ✅ Complete | Spring AI-powered agent with function calling |
| Documentation | 10% | ✅ Complete | Comprehensive README |

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
- **Account balance and transaction history queries**
- **Cross-chain asset monitoring**

## 📝 Notes

- The contract address is hardcoded in `go/main.go` - update if redeploying
- Risk threshold (2 ETH) can be adjusted in `RiskService.java`
- WebSocket connection requires Hardhat node to support subscriptions
- For production use, consider adding authentication, rate limiting, and persistent storage
- **AI Agent requires SiliconFlow API key configured in `application.yml`**
- **Base URL must include `/v1` suffix**: `https://api.siliconflow.cn/v1`
- **Function calling works best with temperature ≤ 0.1**
- **All three dependent services must be running for full AI agent functionality**:
  - account-service (8084)
  - web3go-service (8085)
  - Hardhat node (8545)
- **Maven compiler must use `-parameters` flag** for proper parameter name resolution
- **YAML configuration must not have duplicate keys** (merge all `spring:` sections)

## 🐛 Troubleshooting

### Common Issues

**1. AI Agent fails to start with "DuplicateKeyException"**
- **Cause**: Duplicate `spring:` keys in `application.yml`
- **Solution**: Merge all spring configurations into one section

**2. "IllegalArgumentException: Name for argument not specified"**
- **Cause**: Missing `-parameters` compiler flag
- **Solution**: Add `<compilerArgs><arg>-parameters</arg></compilerArgs>` to pom.xml

**3. Feign client timeout**
- **Cause**: Dependent service not responding
- **Solution**: Check if account-service (8084) and web3go-service (8085) are running

**4. JSON deserialization error**
- **Cause**: Mismatch between Go response fields and Java DTO
- **Solution**: Ensure Go's `EthBalanceResponse` includes all fields expected by Java's `EthDTO`

**5. AI doesn't invoke tools**
- **Cause**: Temperature too high or stream disabled
- **Solution**: Set `temperature: 0.1` and `stream: true` in configuration

## 🤝 Contributing

Feel free to submit issues and enhancement requests!

## 📄 License

This project is open source and available under the MIT License.

---

**Last Updated**: April 23, 2026  
**Maintained by**: Web3 Risk Monitor Team
