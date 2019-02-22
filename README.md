# 自动生成模板管理系统 (Auto-Generate Template Management System)
AGTMS 是一个基于 Spring Cloud 和 Vue.js 的自定义配置对象管理系统

## 项目结构
```
.
├── agtms-eureka        Eureka 服务发现 (默认端口：7890)
├── agtms-gateway       Spring-Gateway 网关服务 (默认端口：7891)
├── agtms-parent 
│   ├── agtms-api       内部接口类库
│   ├── agtms-core      核心类库
│   ├── agtms-jpa       JPA 实现支持类库
│   ├── agtms-mongodb   MongoDB 实现支持类库 (可选)
│   ├── agtms-redis     Redis 支持类库 (可选)
│   ├── agtms-rpc       远程调用 实现支持类库 (可选)
│   └── agtms-web       Web 服务 (默认端口：7892)
├── agtms-vue           前端页面 (默认端口：8080)
├── agtms-zuul          Zuul 网关服务 (默认端口：7891)
├── README.md           README 文件
└── start               一键启动脚本 (Unix)
```

## 要求
1. [JRE(JDK) 8+](https://www.java.com)
2. [Node.js](https://nodejs.org/)

## 安装并启动
### 一键启动
```
./start
```

### 分步启动
1. 启动服务发现 (agtms-eureka)
```
cd agtms-eureka
./mvnw clean install spring-boot:run
```
2. 启动Web 服务 (agtms-web)
```
cd agtms-parent
./mvnw clean install && ./mvnw spring-boot:run -pl agtms-web
```
3. 启动网关服务 (agtms-gateway 或 agtms-zuul)
```
cd agtms-gateway
./mvnw clean install spring-boot:run
```
4. 启动前端页面 (agtms-vue)
```
npm install
npm run serve
```

## 访问
```
http://localhost:8080
```