# Web3 风险监控系统

一个高性能的链上风险监控系统，能够实时检测和分析区块链交易。本项目展示了从智能合约部署到风险评估的完整 Web3 监控流程，并通过 AI 驱动的智能分析进行增强。

## 📋 项目概述

本系统监控来自 Vault 智能合约的以太坊区块链事件，捕获存款/取款交易，并根据交易金额执行风险分析。架构采用微服务模式，包含四个主要组件：

- **智能合约层**：基于 Solidity 的 Vault 合约，支持事件发射
- **数据采集层**：基于 Go 的事件监听器，使用 WebSocket 订阅
- **风险分析层**：Java Spring Boot 服务，进行交易风险评估
- **AI Agent 层**：基于 Spring AI 的智能代理，支持自然语言风险查询和函数调用

## 🏗️ 系统架构

```
┌─────────────────┐       WebSocket        ┌──────────────────┐       HTTP POST       ┌────────────────────┐
│                 │ ◄────────────────────► │                  │ ◄───────────────────► │                    │
│  Hardhat 节点   │      事件流式传输       │  Go 监听器       │      交易数据         │ Java 风险服务      │
│  (本地 RPC)     │                        │  (go-ethereum)   │                       │  (Spring Boot)     │
│                 │                        │                  │                       │                    │
└─────────────────┘                        └──────────────────┘                       └────────────────────┘
     端口: 8545                                   实时监控                               端口: 8080
                                                                                     风险评估逻辑
                                                                                              ▲
                                                                                              │
                                                                                              │ Feign 客户端
                                                                                              ▼
                                                                                    ┌────────────────────┐
                                                                                    │                    │
                                                                                    │   AI Agent 服务    │
                                                                                    │   (Spring AI)      │
                                                                                    │                    │
                                                                                    └────────────────────┘
                                                                                         端口: 8081
                                                                                  自然语言交互界面
                                                                                        函数调用
                                                                                 ┌────────┬─────────┬────────┐
                                                                                 │        │         │        │
                                                                                 ▼        ▼         ▼        ▼
                                                                          账户服务  交易服务  Web3Go服务  LLM API
                                                                             :8084      :8084     :8085    硅基流动
```

## 🚀 功能特性

### 1. 智能合约（Solidity/Hardhat）- 10%
- **Vault 合约**：实现基本的存款和取款功能
- **事件发射**：发射带有索引地址的 `Deposit` 和 `Withdraw` 事件
- **安全最佳实践**：
  - 限制 Gas 的转账（`gas: 2300`）以防止重入攻击
  - 取款前的余额验证
  - 通过 `msg.sender` 实现适当的访问控制
- **全面测试**：Mocha/Chai 测试套件，覆盖部署、存款和取款场景

**关键文件：**
- `solidity/contracts/Vault.sol` - 主合约实现
- `solidity/test/Vault.test.js` - 测试用例（3 个测试场景）

### 2. 事件采集服务（Go）- 40%
- **WebSocket 订阅**：使用 `eth_subscribe` 进行实时事件监听
- **ABI 解析**：使用 go-ethereum ABI 解析器解码合约事件
- **自动重连**：处理订阅错误，具备自动重连逻辑
- **索引参数处理**：正确从 `Topics[1]` 中提取索引地址参数
- **HTTP 集成**：将交易数据转发到 Java 风险服务
- **ETH 余额查询 API**：提供 REST 端点用于查询以太坊地址余额

**技术亮点：**
- 使用 `ws://` 协议连接到 Hardhat 节点
- 通过上下文取消实现适当的错误处理和优雅关闭
- 将 Wei 转换为 Ether，提供人类可读的金额报告
- 遵循 Solidity 事件解析最佳实践（索引 vs 非索引参数）
- 在端口 8085 暴露 HTTP API 用于余额查询

**关键文件：**
- `go/main.go` - 完整的事件监听器实现（约 300 行）

### 3. 风险评估服务（Java/Spring Boot）- 30%
- **RESTful API**：暴露 `/api/risk/check` 端点用于交易分析
- **业务逻辑**：检测大额交易（>2 ETH 阈值）
- **健康检查**：提供 `/api/risk/health` 端点用于服务监控
- **清晰架构**：采用 Controller-Service 模式实现关注点分离

**风险规则：**
- 交易 > 2 ETH → 标记为"检测到大额交易"
- 交易 ≤ 2 ETH → 标记为"交易通过"

**关键文件：**
- `java/risk-service/src/main/java/com/web3/risk_service/RiskController.java` - REST 控制器
- `java/risk-service/src/main/java/com/web3/risk_service/RiskService.java` - 业务逻辑
- `java/risk-service/src/main/java/com/web3/risk_service/TransactionRequest.java` - 数据模型
- `java/risk-service/pom.xml` - Maven 依赖（Spring Boot 4.0.5, Java 17）

### 4. AI Agent 服务（Java/Spring AI）- 20%
- **自然语言接口**：接受自然语言风险查询
- **函数调用**：AI 根据用户意图自动调用工具
- **多工具集成**：
  - 账户余额查询（通过 Feign → account-service:8084）
  - 交易历史查询（通过 Feign → transaction-service:8084）
  - ETH 余额查询（通过 Feign → web3go-service:8085）
- **Spring AI 集成**：使用硅基流动（Qwen/Qwen2.5-7B-Instruct）作为 LLM 后端
- **流式响应**：实时 token 流式传输，提供更好的用户体验
- **微服务架构**：独立服务，集成 OpenFeign

**核心能力：**
- 自然语言交易风险评估
- 从用户消息中自动提取参数
- 智能工具选择和调用
- 上下文感知的多轮对话
- 账户和余额信息查询

**技术亮点：**
- Spring AI 1.0+ 使用 ChatClient 构建器模式
- 使用 `@Tool` 注解进行函数注册（替代已弃用的 `@AiFunction`）
- 明确指定 InputType 以生成正确的 JSON Schema
- 硅基流动 OpenAI 兼容 API 集成
- 使用 Lombok 的构造函数依赖注入
- 基于流的响应处理，实现实时输出

**关键文件：**
- `java/ai-agent-service/src/main/java/com/web3/ai/controller/AiAgentController.java` - REST 端点
- `java/ai-agent-service/src/main/java/com/web3/ai/service/impl/OpenAiAgentServiceImpl.java` - AI 服务实现
- `java/ai-agent-service/src/main/java/com/web3/ai/tools/AccountBalanceTool.java` - 账户余额工具
- `java/ai-agent-service/src/main/java/com/web3/ai/tools/TransactionHistoryTool.java` - 交易历史工具
- `java/ai-agent-service/src/main/java/com/web3/ai/tools/EthBalanceTool.java` - ETH 余额工具
- `java/ai-agent-service/src/main/resources/application.yml` - 配置文件

## 🛠️ 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| 智能合约 | Solidity | ^0.8.20 |
| 开发框架 | Hardhat | 最新 |
| 测试框架 | Mocha + Chai | - |
| 事件监听器 | Go | 1.25.4 |
| 以太坊库 | go-ethereum | v1.17.2 |
| 后端服务 | Java | 17 |
| Web 框架 | Spring Boot | 3.4.3 |
| 云框架 | Spring Cloud | 2024.0.0 |
| AI 框架 | Spring AI | 1.0.0-M6 |
| LLM 提供商 | 硅基流动（Qwen2.5-7B-Instruct） | - |
| 服务通信 | OpenFeign | - |
| 构建工具 | Maven | - |

## 📦 安装与设置

### 前置要求
- Node.js 和 npm
- Go 1.25+
- Java 17+
- Maven 3.6+

### 1. 智能合约设置

```bash
cd solidity
npm install
npx hardhat compile
npx hardhat test
npx hardhat node
```

Hardhat 节点将在 `http://127.0.0.1:8545` 启动，WebSocket 支持在 `ws://127.0.0.1:8545`。

### 2. 部署合约

在新终端中：
```bash
cd solidity
npx hardhat run scripts/deploy.js --network localhost
```

记录部署的合约地址（默认：`0x5FbDB2315678afecb367f032d93F642f64180aa3`）。

### 3. 启动 Go 事件监听器

```bash
cd go
go mod download
go run main.go
```

监听器将：
- 通过 WebSocket（端口 8545）连接到 Hardhat 节点
- 开始监控合约事件
- 在端口 8085 暴露 HTTP API 用于余额查询

### 4. 启动 Java 风险服务

```bash
cd java/risk-service
mvn spring-boot:run
```

服务将在 `http://localhost:8080` 启动。

### 5. 启动 AI Agent 服务

**重要**：确保所有依赖服务已先启动：
- account-service（端口 8084）
- web3go-service（Go 服务，端口 8085）
- Hardhat 节点（端口 8545）

```bash
cd java/ai-agent-service
mvn clean install
mvn spring-boot:run
```

服务将在 `http://localhost:8081` 启动。

**所需配置：**

编辑 `java/ai-agent-service/src/main/resources/application.yml`：

```yaml
spring:
  ai:
    openai:
      api-key: your-siliconflow-api-key-here  # 从 https://siliconflow.cn 获取
      base-url: https://api.siliconflow.cn/v1  # 注意：必须包含 /v1
      chat:
        options:
          model: Qwen/Qwen2.5-7B-Instruct
          stream: true  # 必须启用
          temperature: 0.1  # 工具调用时使用较低温度
          max-tokens: 1024

feign:
  account-service:
    url: http://127.0.0.1:8084
  transaction-service:
    url: http://127.0.0.1:8084
  web3go-service:
    url: http://127.0.0.1:8085
```

**获取 API Key：**
1. 访问 [硅基流动平台](https://siliconflow.cn)
2. 注册并获取您的 API Key
3. 替换配置文件中的 `your-siliconflow-api-key-here`

## 🧪 测试

### 智能合约测试
```bash
cd solidity
npx hardhat test
```

预期输出：
- ✅ 部署成功
- ✅ Deposit 事件发射
- ✅ Withdraw 事件发射

### 集成测试

1. 启动所有服务（Hardhat 节点、Go 监听器、Java 风险服务、AI Agent）
2. 与合约交互：
```bash
cd solidity
npx hardhat run scripts/testTx.js --network localhost
```

这将执行两笔测试交易：
- **测试 1**：存入 1 ETH（正常交易）
- **测试 2**：存入 3 ETH（触发风险警报的大额交易）

3. 观察：
   - Go 控制台日志显示检测到的事件
   - Java 服务控制台显示风险评估结果
   - 大额存款（>2 ETH）触发风险警告

### AI Agent 服务测试

#### 测试 1：账户余额查询
```bash
curl -X POST http://localhost:8081/ai/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"查询用户1的账户余额"}'
```

预期：AI 调用 AccountBalanceTool 并返回余额信息

#### 测试 2：交易历史查询
```bash
curl -X POST http://localhost:8081/ai/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"用户1最近的交易记录是什么？"}'
```

预期：AI 调用 TransactionHistoryTool 并返回交易列表

#### 测试 3：ETH 余额查询
```bash
curl -X POST http://localhost:8081/ai/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"查询地址 0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266 的ETH余额"}'
```

预期：AI 调用 EthBalanceTool 并返回 ETH 余额

#### 测试 4：风险评估
```bash
curl -X POST http://localhost:8081/ai/agent/chattorisk \
  -H "Content-Type: application/json" \
  -d '{"message":"检测这笔交易是否有风险"}'
```

预期：AI 执行风险分析并调用工具

#### 测试 5：普通对话（不使用工具）
```bash
curl -X POST http://localhost:8081/ai/agent/chatwithnotool \
  -H "Content-Type: application/json" \
  -d '{"message":"你好，请介绍一下你自己"}'
```

预期：对话式响应，不调用工具

### Go 服务 API 测试

测试 ETH 余额查询端点：

```bash
curl http://localhost:8085/eth/balance/0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266
```

预期响应：
```json
{
  "address": "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266",
  "balance": 10000.0,
  "usdtValue": 10000.0
}
```

## 🔍 关键实现细节

### 事件解析策略
Go 监听器正确处理 Solidity 事件参数：
- **索引参数**（address）：从 `logData.Topics[1]` 提取
- **非索引参数**（uint256）：使用 ABI 解包从 `logData.Data` 解析

### 安全考虑
- Vault 合约使用 `gas: 2300` 限制进行 ETH 转账以防止重入攻击
- 输入验证确保存款/取款金额为正数
- 余额检查防止透支

### 错误处理
- Go 服务为 WebSocket 故障实现自动重连
- 基于上下文的取消实现优雅关闭
- Java 服务提供健康检查端点用于监控
- Feign 客户端配置超时保护（连接：3秒，读取：10秒）

### AI 函数调用
- 使用 `@Tool` 注解注册工具，并提供描述性名称
- 明确指定输入类型以生成正确的 JSON Schema
- 温度设置为较低值（0.1）以实现确定性工具选择
- 启用流式传输以实现实时响应交付

## 📚 API 文档

### 风险评估服务（端口 8080）

**POST** `/api/risk/check`
检查交易风险等级。

请求体：
```json
{
  "from": "0x123...",
  "to": "0x456...",
  "amount": 1.5
}
```

**GET** `/api/risk/health`
返回服务健康状态。

### Go Web3 服务（端口 8085）

**GET** `/eth/balance/{address}`
查询以太坊地址的 ETH 余额。

路径参数：
- `address`：以太坊地址（例如：`0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266`）

响应：
```json
{
  "address": "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266",
  "balance": 10000.0,
  "usdtValue": 10000.0
}
```

### AI Agent 服务（端口 8081）

**POST** `/ai/agent/chat`
支持函数调用的通用聊天端点。

请求体：
```json
{
  "message": "查询用户1的账户余额"
}
```

响应：Server-Sent Events（流式）
```
data: {"answer":"用户1的账户余额为"}
data: {"answer":" 15800.50 ETH"}
data: {"answer":"，状态正常。"}
```

**POST** `/ai/agent/chattorisk`
使用 AI Agent 和函数调用的风险评估端点。

请求体：
```json
{
  "message": "检测地址 0x111 向 0x222 转账 100 ETH 的风险"
}
```

响应：
```
{
  "answer": "经过分析，该交易的风险等级为低..."
}
```

**POST** `/ai/agent/chatwithnotool`
不使用工具调用的聊天端点。

请求体：
```json
{
  "message": "你好，请介绍自己"
}
```

响应：
```
{
  "answer": "你好！我是Web3风控助手..."
}
```

## 📊 项目完成状态

| 模块 | 权重 | 状态 | 描述 |
|------|------|------|------|
| 智能合约 | 10% | ✅ 完成 | Vault 合约及测试 |
| 事件采集 | 40% | ✅ 完成 | Go WebSocket 监听器 + HTTP API |
| 业务逻辑 | 20% | ✅ 完成 | Java 风险评估 |
| AI Agent | 20% | ✅ 完成 | 基于 Spring AI 的函数调用代理 |
| 文档 | 10% | ✅ 完成 | 完整的 README |

**总完成度：100%** ✅

## 🎯 应用场景

本系统可扩展用于：
- 实时巨鲸动向追踪
- AML（反洗钱）合规
- DeFi 协议监控
- 可疑活动自动警报系统
- 投资组合风险管理
- **通过 AI Agent 进行自然语言风险查询**
- **使用函数调用进行智能交易分析**
- **多轮对话式风险评估**
- **账户余额和交易历史查询**
- **跨链资产监控**

## 📝 注意事项

- 合约地址在 `go/main.go` 中硬编码 - 重新部署时需要更新
- 风险阈值（2 ETH）可在 `RiskService.java` 中调整
- WebSocket 连接需要 Hardhat 节点支持订阅
- 生产环境使用时，考虑添加身份验证、速率限制和持久化存储
- **AI Agent 需要在 `application.yml` 中配置硅基流动 API Key**
- **Base URL 必须包含 `/v1` 后缀**：`https://api.siliconflow.cn/v1`
- **函数调用在 temperature ≤ 0.1 时效果最佳**
- **AI Agent 完整功能需要三个依赖服务全部运行**：
  - account-service（8084）
  - web3go-service（8085）
  - Hardhat 节点（8545）
- **Maven 编译器必须使用 `-parameters` 标志**以正确解析参数名
- **YAML 配置不能有重复键**（合并所有 `spring:` 部分）

## 🐛 故障排除

### 常见问题

**1. AI Agent 启动失败，报错 "DuplicateKeyException"**
- **原因**：`application.yml` 中存在重复的 `spring:` 键
- **解决**：将所有 spring 配置合并到一个部分

**2. "IllegalArgumentException: Name for argument not specified"**
- **原因**：缺少 `-parameters` 编译器标志
- **解决**：在 pom.xml 中添加 `<compilerArgs><arg>-parameters</arg></compilerArgs>`

**3. Feign 客户端超时**
- **原因**：依赖服务无响应
- **解决**：检查 account-service（8084）和 web3go-service（8085）是否正在运行

**4. JSON 反序列化错误**
- **原因**：Go 响应字段与 Java DTO 不匹配
- **解决**：确保 Go 的 `EthBalanceResponse` 包含 Java 的 `EthDTO` 期望的所有字段

**5. AI 不调用工具**
- **原因**：温度过高或流式传输未启用
- **解决**：在配置中设置 `temperature: 0.1` 和 `stream: true`

## 🤝 贡献

欢迎提交问题和功能增强请求！

## 📄 许可证

本项目是开源的，遵循 MIT 许可证。

---

**最后更新**：2026年4月23日  
**维护者**：Web3 风险监控团队
