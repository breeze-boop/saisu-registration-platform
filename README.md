# 赛速报 · 名额高并发抢占平台

赛速报面向马拉松、越野赛、城市联赛等赛事报名场景，提供赛事检索、限时抢名额、报名占位、参赛信息管理与智能报名助手能力。项目重点围绕高并发名额抢占链路、缓存治理、异步占位、报名状态一致性，以及 Java AgentScope 风格的智能报名助手进行工程化实现。

## 技术栈

- 后端：Java 17、Spring Boot 3、MyBatis-Plus、MySQL、Redis、Lua、RabbitMQ、Caffeine、AgentScope Java
- 前端：React、Vite、TypeScript、Ant Design
- 基础设施：Docker Compose、MySQL 8.4、Redis 7.4、RabbitMQ 3.13

## 目录结构

```text
.
├── backend/                  # Spring Boot 后端
│   ├── src/main/java/com/mishi
│   │   ├── agent/            # Java AgentScope 风格智能报名助手工具与路由
│   │   ├── cache/            # Caffeine + Redis 多级缓存、布隆过滤器
│   │   ├── controller/       # REST API
│   │   ├── mapper/           # MyBatis-Plus Mapper
│   │   ├── order/            # 报名占位状态机和超时释放
│   │   └── seckill/          # Redis Lua 名额抢占和 RabbitMQ 异步占位
│   └── src/main/resources
│       ├── db/               # MySQL schema/data
│       └── lua/seckill.lua   # 原子名额扣减和一人一位脚本
├── frontend/                 # React/Vite 前端
└── infra/docker-compose.yml  # MySQL/Redis/RabbitMQ
```

## 智能报名助手 LLM 配置

默认不配置 API Key 时，系统使用本地 deterministic ReAct-like fallback，根据关键词选择名额查询、报名校验、占位状态等后端工具。

如需接入真实模型，配置兼容 OpenAI Chat Completions 的服务即可，例如 OpenAI、DeepSeek、Qwen 兼容网关或私有模型网关：

```bash
export MISHI_LLM_API_KEY=你的_api_key
export MISHI_LLM_BASE_URL=https://api.openai.com/v1
export MISHI_LLM_MODEL=gpt-4o-mini
export MISHI_LLM_TIMEOUT_SECONDS=10
```

模型只负责选择工具，不直接编造业务结果。后端会要求模型输出工具决策 JSON，例如：

```json
{"toolName":"check_inventory","voucherId":1,"orderId":10001,"reason":"用户询问赛事名额"}
```

如果模型不可用、返回格式不合法或超时，`CustomerAgentService` 会自动走本地 fallback，保证智能报名助手接口仍可用。

## 本地启动

### 1. 启动基础设施

```bash
cd infra
docker compose up -d
```

RabbitMQ 管理后台：<http://localhost:15672>，账号/密码：`mishi` / `mishi123`。

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：<http://localhost:8080>。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：<http://localhost:5173>。

## 常用接口

```bash
# 赛事列表
curl http://localhost:8080/api/shops

# 名额批次列表
curl http://localhost:8080/api/vouchers

# 预热赛事名额到 Redis
curl -X POST http://localhost:8080/api/vouchers/1/preload

# 发起限时抢名额，X-User-Id 模拟登录用户
curl -X POST -H 'X-User-Id: 7' http://localhost:8080/api/seckill/1

# 智能报名助手
curl -X POST http://localhost:8080/api/agent/chat \
  -H 'Content-Type: application/json' \
  -d '{"userId":7,"sessionId":"demo","question":"这个赛事还有名额吗？"}'
```

## 验证命令

```bash
cd backend
mvn test

cd ../frontend
npm install
npm run build
```
