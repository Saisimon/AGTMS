# 自动生成模板管理系统 (Auto-Generate Template Management System)
AGTMS 是一个基于 Spring Cloud 和 Vue.js 的自定义配置对象管理系统

## 项目结构
```
.
├── agtms-config        配置服务 (可选)
├── agtms-eureka        Eureka 服务发现
├── agtms-gateway       Spring-Gateway 网关服务
├── agtms-parent 
│   ├── agtms-api       内部接口类库
│   ├── agtms-core      核心类库
│   ├── agtms-jpa       JPA 实现支持类库
│   ├── agtms-mongodb   MongoDB 实现支持类库 (可选)
│   ├── agtms-redis     Redis 支持类库 (可选)
│   ├── agtms-rpc       远程调用 实现支持类库 (可选)
│   └── agtms-web       Web 服务
├── agtms-vue           前端页面
├── agtms-zuul          Zuul 网关服务
└── README.md           README 文件
```

## 安装
### 启动服务发现 (agtms-eureka)
```
cd agtms-eureka
./mvnw clean install spring-boot:run
```

### 启动Web 服务 (agtms-web)
```
cd agtms-parent
./mvnw clean install && ./mvnw spring-boot:run -pl agtms-web
```

### 启动网关服务 (agtms-gateway 或 agtms-zuul)
```
cd agtms-gateway
./mvnw clean install spring-boot:run
```

### 启动前端页面 (agtms-vue)
```
npm install
npm run serve
```

### 访问
```
http://localhost:8080
```