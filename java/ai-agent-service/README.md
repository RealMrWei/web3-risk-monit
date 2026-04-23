# AI Agent Service

基于 Spring AI 开发的智能 AI Agent 微服务，支持 Function Calling（工具调用），能够自动查询账户余额、交易历史和以太坊地址余额。

## 📋 项目概述

AI Agent Service 是一个智能化的对话服务，集成了大语言模型（LLM）和自定义工具函数。用户可以通过自然语言与系统交互，AI 会自动判断是否需要调用工具获取实时数据，然后生成准确的回复。

### 核心特性

- ✅ **自然语言交互**：支持流式对话，响应速度快
- ✅ **智能工具调用**：AI 自动判断何时调用工具获取数据
- ✅ **多数据源集成**：
  - 平台账户余额查询
  - 用户交易历史查询
  - 以太坊地址 ETH 余额查询
- ✅ **风险检测**：专门的风险分析接口
- ✅ **微服务架构**：通过 OpenFeign 与其他服务通信

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 运行环境 |
| Spring Boot | 3.4.3 | 应用框架 |
| Spring AI | 1.0.0-M6 | AI 集成框架 |
| Spring Cloud | 2024.0.0 | 微服务支持 |
| OpenFeign | - | 服务间通信 |
| Lombok | - | 代码简化 |
| Qwen2.5-7B-Instruct | - | 大语言模型（硅基流动） |

## 📁 项目结构

```
ai-agent-service/
├── src/main/java/com/web3/ai/
│   ├── AiAgentApplication.java          # 启动类
│   ├── client/                           # LLM 客户端接口
│   │   └── LlmClient.java
│   ├── config/                           # 配置类
│   ├── controller/                       # REST 控制器
│   │   └── AiAgentController.java
│   ├── dto/                              # 数据传输对象
│   │   ├── ChatRequest.java
│   │   ├── ChatResponse.java
│   │   ├── AccountDTO.java
│   │   ├── TxDTO.java
│   │   └── EthDTO.java
│   ├── feign/                            # Feign 客户端
│   │   ├── AccountFeignClient.java
│   │   ├── TransactionFeignClient.java
│   │   └── Web3GoClient.java
│   ├── service/                          # 业务逻辑层
│   │   ├── AiAgentService.java
│   │   └── impl/
│   │       ├── OpenAiAgentServiceImpl.java
│   │       └── MockAiServiceImpl.java
│   └── tools/                            # AI 工具函数
│       ├── AccountBalanceTool.java       # 账户余额查询工具
│       ├── TransactionHistoryTool.java   # 交易历史查询工具
│       └── EthBalanceTool.java           # ETH 余额查询工具
├── src/main/resources/
│   └── application.yml                   # 配置文件
└── pom.xml                               # Maven 配置
```

## 🚀 快速开始

### 前置要求

1. **JDK 17+** 已安装并配置环境变量
2. **Maven 3.6+** 已安装
3. **依赖服务已启动**：
   - `account-service` (端口 8084)
   - `web3go-service` (Go 服务，端口 8085)
   - Hardhat 节点 (端口 8545，供 Go 服务使用)

### 配置说明

编辑 `src/main/resources/application.yml`：

```yaml
server:
  port: 8081

spring:
  application:
    name: ai-agent-service
  ai:
    openai:
      api-key: your-api-key-here          # 替换为你的 API Key
      base-url: https://api.siliconflow.cn
      chat:
        options:
          model: Qwen/Qwen2.5-7B-Instruct
          stream: true                     # 必须开启流式响应
          max-tokens: 1024
          temperature: 0.1                 # 工具调用建议较低温度

feign:
  account-service:
    url: http://127.0.0.1:8084
  transaction-service:
    url: http://127.0.0.1:8084
  web3go-service:
    url: http://127.0.0.1:8085
```

### 启动步骤

1. **编译项目**
```bash
cd java/ai-agent-service
mvn clean install
```

2. **启动服务**
```bash
mvn spring-boot:run
```

或者运行主类 `com.web3.ai.AiAgentApplication`

3. **验证启动**
```bash
curl http://localhost:8081/actuator/health
```

## 📡 API 接口文档

### 1. 普通聊天接口（支持工具调用）

**接口**: `POST /ai/agent/chat`

**请求体**:
```json
{
  "message": "帮我查一下用户1的账户余额"
}
```

**响应**: 流式响应（Server-Sent Events）
```
data: {"answer":"用户1的账户余额为"}
data: {"answer":" 15800.50 ETH"}
data: {"answer":"，状态正常。"}
```

**特点**:
- 支持流式输出，用户体验更好
- AI 会自动判断是否需要调用工具
- 适合通用对话场景

---

### 2. 风险检测聊天接口

**接口**: `POST /ai/agent/chattorisk`

**请求体**:
```json
{
  "message": "检查这笔交易是否有风险"
}
```

**响应**:
```json
{
  "answer": "经过分析，该交易存在以下风险点：..."
}
```

**特点**:
- 专门用于风险分析场景
- 启用特定的风险控制工具

---

### 3. 不使用工具的聊天接口

**接口**: `POST /ai/agent/chatwithnotool`

**请求体**:
```json
{
  "message": "你好，请介绍一下你自己"
}
```

**响应**:
```json
{
  "answer": "你好！我是Web3风控助手..."
}
```

**特点**:
- 不调用任何工具
- 纯 LLM 对话
- 适合问候、闲聊等场景

## 🔧 AI 工具函数

AI Agent 内置了以下工具函数，当用户提问涉及相关数据时，AI 会自动调用：

### 1. AccountBalanceTool - 账户余额查询

**功能**: 查询平台用户的账户余额和状态

**触发示例**:
- "用户1的余额是多少？"
- "查一下账户状态"
- "我的账户还有多少钱？"

**返回数据**:
```json
{
  "userId": "1",
  "balance": "15800.50",
  "status": "正常"
}
```

---

### 2. TransactionHistoryTool - 交易历史查询

**功能**: 查询用户的交易历史记录

**触发示例**:
- "用户1最近的交易记录"
- "显示我的转账历史"
- "查看最近5笔交易"

**返回数据**:
```json
[
  {
    "txId": "txId1",
    "amount": 100,
    "type": "deposit",
    "time": "2024-01-01T10:00:00"
  }
]
```

---

### 3. EthBalanceTool - ETH 余额查询

**功能**: 查询以太坊地址的 ETH 余额

**触发示例**:
- "查询地址 0x... 的余额"
- "这个钱包有多少 ETH？"
- "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266 的余额"

**返回数据**:
```json
{
  "address": "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266",
  "ethBalance": 10000.0,
  "usdtValue": 10000.0
}
```

## 🧪 测试示例

使用 `test.http` 文件或 curl 命令进行测试：

### 测试1: 查询账户余额
```http
POST http://localhost:8081/ai/agent/chat
Content-Type: application/json

{
  "message": "帮我查一下用户1的账户余额"
}
```

### 测试2: 查询交易历史
```http
POST http://localhost:8081/ai/agent/chat
Content-Type: application/json

{
  "message": "用户1最近的交易记录是什么？"
}
```

### 测试3: 查询 ETH 余额
```http
POST http://localhost:8081/ai/agent/chat
Content-Type: application/json

{
  "message": "查询地址 0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266 的ETH余额"
}
```

### 测试4: 风险检测
```http
POST http://localhost:8081/ai/agent/chattorisk
Content-Type: application/json

{
  "message": "检查用户2的交易是否有风险"
}
```

## ⚙️ 配置详解

### Spring AI 配置

| 配置项 | 说明 | 推荐值 |
|--------|------|--------|
| `spring.ai.openai.api-key` | API 密钥 | 从硅基流动获取 |
| `spring.ai.openai.base-url` | API 基础地址 | `https://api.siliconflow.cn` |
| `spring.ai.openai.chat.options.model` | 使用的模型 | `Qwen/Qwen2.5-7B-Instruct` |
| `spring.ai.openai.chat.options.stream` | 是否流式响应 | `true`（必须） |
| `spring.ai.openai.chat.options.temperature` | 温度参数 | `0.1`（工具调用） |
| `spring.ai.openai.chat.options.max-tokens` | 最大 token 数 | `1024` |

### Feign 客户端配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `feign.account-service.url` | 账户服务地址 | `http://127.0.0.1:8084` |
| `feign.transaction-service.url` | 交易服务地址 | `http://127.0.0.1:8084` |
| `feign.web3go-service.url` | Web3 Go服务地址 | `http://127.0.0.1:8085` |
| `spring.cloud.httpclient.connect-timeout` | 连接超时 | `3000ms` |
| `spring.cloud.httpclient.response-timeout` | 读取超时 | `10000ms` |

## 🔍 常见问题

### 1. 启动时报错 "DuplicateKeyException"

**原因**: YAML 配置文件中存在重复的键（如多个 `spring:`）

**解决**: 确保同一层级没有重复的键名，将所有配置合并到一个父节点下

### 2. 调用接口报 "IllegalArgumentException: Name for argument not specified"

**原因**: 编译时未启用 `-parameters` 标志

**解决**: 在父 POM 的 `maven-compiler-plugin` 中添加：
```xml
<compilerArgs>
    <arg>-parameters</arg>
</compilerArgs>
```

### 3. Feign 调用超时

**原因**: 依赖服务响应慢或未启动

**解决**: 
- 检查依赖服务是否正常运行
- 调整超时配置：
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
```

### 4. JSON 解析错误 "JsonEOFException"

**原因**: 服务返回的 JSON 格式不完整或字段不匹配

**解决**: 
- 检查被调用服务的响应格式
- 确保 DTO 字段与服务返回字段一致
- 查看控制台日志确认实际返回内容

### 5. AI 不调用工具

**可能原因**:
- Temperature 设置过高（建议 0.1）
- Stream 未开启（必须设置为 true）
- 工具描述不够清晰

**解决**: 检查 `application.yml` 中的配置是否正确

## 📝 开发指南

### 添加新工具

1. 创建工具类，添加 `@Component` 注解
2. 定义请求参数类（使用 `@ToolParam` 标注参数）
3. 实现工具方法，添加 `@Tool` 注解并提供清晰的 description
4. 返回 `Map<String, Object>` 或简单类型

示例：
```java
@Component
public class MyCustomTool {
    
    @Tool(description = "工具的功能描述")
    public Map<String, Object> myMethod(MyRequest request) {
        // 实现逻辑
        return Map.of("key", "value");
    }
}
```

### 调试技巧

1. **查看工具调用日志**: 控制台会打印工具调用信息
2. **启用 DEBUG 日志**: 在 `application.yml` 中添加：
```yaml
logging:
  level:
    com.web3.ai: DEBUG
    org.springframework.ai: DEBUG
```

## 🤝 依赖服务

AI Agent Service 依赖以下服务正常运行：

| 服务 | 端口 | 说明 |
|------|------|------|
| account-service | 8084 | 提供账户余额和交易历史查询 |
| web3go-service | 8085 | 提供以太坊地址余额查询（Go 服务） |
| Hardhat Node | 8545 | 本地区块链节点（供 Go 服务使用） |

## 📄 License

本项目为学习和研究用途。

---

**作者**: Web3 Risk Monitor Team  
**最后更新**: 2024-04-23
