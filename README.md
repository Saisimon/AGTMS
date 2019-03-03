# 自动生成模板管理系统 (Auto-Generate Template Management System)
AGTMS 是一个基于 Spring Cloud 和 Vue.js 的自定义配置对象管理系统。

## 项目结构
```
.
├── agtms-autoconfigure 自动配置支持类库
├── agtms-eureka        Eureka 服务发现 (默认端口：7890)
├── agtms-example       远程调用示例服务 (默认端口：7899)
├── agtms-gateway       Spring-Gateway 网关服务 (默认端口：7891)
├── agtms-parent 
│   ├── agtms-api       内部接口类库
│   ├── agtms-config    配置类库
│   ├── agtms-core      核心类库
│   ├── agtms-jpa       JPA 实现支持类库 (默认)
│   ├── agtms-mongodb   MongoDB 实现支持类库 (可选)
│   ├── agtms-redis     Redis 支持类库 (可选)
│   ├── agtms-remote    远程调用实现支持类库 (可选)
│   └── agtms-web       Web 服务 (默认端口：7892)
├── agtms-vue           前端页面 (默认端口：8080)
├── agtms-zuul          Zuul 网关服务 (默认端口：7891)
├── README.md           README 文件
├── start.cmd           一键启动脚本 (Windows)
└── start               一键启动脚本 (Unix)
```

## 要求
1. [JRE(JDK) 8+](https://www.java.com)
2. [Node.js](https://nodejs.org/)
3. RAM 4G+

## 最小安装并启动
> 默认使用 H2 内存数据库，每次重启数据会重制。要想保存数据，请自行配置数据库连接
### 一键启动
1. 执行启动脚本
```sh
./start
```
2. 访问
```
http://localhost:8080
```

### 分步启动
1. 打包
```sh
./mvnw clean package
```
2. 启动服务发现 (agtms-eureka)
```sh
java -jar agtms-eureka/target/agtms-eureka.jar
```
3. 启动Web 服务 (agtms-web)
```sh
java -jar agtms-parent/agtms-web/target/agtms-web.jar
```
4. 启动网关服务 (agtms-gateway 或 agtms-zuul)
```sh
java -jar agtms-zuul/target/agtms-zuul.jar
```
5. 启动前端页面 (agtms-vue)
```sh
cd agtms-vue
npm install
npm run serve
```
6. 访问
```
http://localhost:8080
```

## 远程调用示例
1. 添加远程调用实现支持
> agtms-parent/agtms-web/pom.xml
```xml
<dependency>
    <groupId>net.saisimon</groupId>
    <artifactId>agtms-remote</artifactId>
</dependency>
```
2. 启动 agtms 服务
```sh
./start
```
3. 启动 agtms-example 服务
```
java -jar agtms-example/target/agtms-example.jar
```
4. 访问
```
http://localhost:8080
```