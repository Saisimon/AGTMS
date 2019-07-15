# 自动生成模板管理系统 (Auto-Generate Template Management System)
[![Build Status](https://travis-ci.com/Saisimon/AGTMS.svg?token=NEGBSLzoPsiP31io9ioJ&branch=standalone)](https://travis-ci.com/Saisimon/AGTMS)
[![codecov](https://codecov.io/gh/Saisimon/AGTMS/branch/standalone/graph/badge.svg?token=HuncHUyard)](https://codecov.io/gh/Saisimon/AGTMS)
![GitHub](https://img.shields.io/github/license/Saisimon/AGTMS.svg)

AGTMS 是一个基于 Spring Cloud 和 Vue.js 的自定义配置对象管理系统，支持 Oracle、Mysql、Sqlserver、Postgresql、Mongodb、REST 等方式获取数据。

## 分支
* [master](https://github.com/Saisimon/AGTMS) - 分布式环境
* [standalone](https://github.com/Saisimon/AGTMS/tree/standalone) - 单机环境
* [dev](https://github.com/Saisimon/AGTMS/tree/dev) - 开发分支

## 项目结构
```
.
├── agtms-autotest      自动化测试
├── agtms-parent 
│   ├── agtms-config    配置类库
│   ├── agtms-core      核心类库
│   ├── agtms-jpa       JPA 实现支持类库 (默认为 H2，支持 Oracle、Mysql、Sqlserver、Postgresql 数据库)
│   ├── agtms-mongodb   MongoDB 实现支持类库 (可选)
│   ├── agtms-redis     Redis 支持类库 (可选)
│   └── agtms-web       Web 服务 (默认端口：7892)
├── agtms-record        集成测试报告聚合模块
├── agtms-vue           前端页面 (默认端口：8080)
├── data                Docker 相关数据
│   └── web
|       ├── config      Web 服务额外配置
|       └── libs        Web 服务额外 jar 库路径
├── docker-compose.yml  Docker Compose 配置
├── README.md           README 文件
├── start.cmd           一键启动脚本 (Windows)
└── start               一键启动脚本 (Unix)
```

## 要求
1. [JRE(JDK) 8+](https://www.java.com)
2. [Node.js](https://nodejs.org/)
3. RAM 2G+

## 安装并启动
### 一键启动
`默认使用 H2 内存数据库，每次重启数据会重制。要想保存数据，请自行配置数据库连接`
1. 配置 hosts
* `/etc/hosts` (Unix)
* `c:\windows\system32\drivers\etc\hosts` (Windows)
```
127.0.0.1   eurekaserver
```
2. 执行启动脚本
* **`start.cmd 默认会杀掉占用 7892端口的进程，请确认以后再执行操作`**
```sh
# Unix
./start

# Windows
start.cmd
```
3. 访问
```html
http://localhost:8080
```
4. 日志
```
data
└── web 
    └── agtms-web.log     Web 服务日志
```

### 分步启动
`默认使用 H2 内存数据库，每次重启数据会重制。要想保存数据，请自行配置数据库连接`
1. 配置 hosts
* `/etc/hosts` (Unix)
* `c:\windows\system32\drivers\etc\hosts` (Windows)
```
127.0.0.1   eurekaserver
```
2. 打包
```sh
# Unix
./mvnw clean package -Ddockerfile.skip=true

# Windows
mvnw.cmd clean package -Ddockerfile.skip=true
```
3. 启动Web 服务 (agtms-web)
```sh
java -jar agtms-parent/agtms-web/target/agtms-web.jar
```
4. 启动前端页面 (agtms-vue)
```sh
cd agtms-vue
npm install
npm run serve
```
5. 访问
```html
http://localhost:8080
```
6. 日志
```
data
└── web 
    └── agtms-web.log     Web 服务日志
```

### Docker 容器启动
1. 安装 [Docker CE](https://docs.docker.com/install/) 或者 [Docker EE](https://docs.docker.com/ee/supported-platforms/)，请参考官方文档下载安装
2. 安装 [Docker Compose](https://docs.docker.com/compose/install/)，请参考官方文档下载安装
3. 编译 Java 项目并生成 Docker 镜像
```sh
# Unix
./mvnw clean package
# Windows
mvnw.cmd clean package
```
4. data/web/libs 路径下添加对应数据库驱动 jar 包
* 默认使用 MySQL 5.7
```sh
# mysql-connector-java-8.0.16.jar
wget -P data/web/libs http://central.maven.org/maven2/mysql/mysql-connector-java/8.0.16/mysql-connector-java-8.0.16.jar
```
5. 启动容器
* DATA_HOME：数据目录，默认为 ./data
* WEB_CONFIG_HOME：Web 服务额外配置，默认为 ./data/web/config
* WEB_LIBS_HOME：Web 服务额外 jar 库路径，默认为 ./data/web/libs
* REVISION:：版本号，默认为 latest
```sh
# Docker Compose
docker-compose up -d
# Docker Swarm
docker stack deploy -c docker-compose.yml agtms
```
6. 访问
```html
http://localhost:8080
```

## 自动化测试

### Chrome
1. 下载与 Chrome 浏览器对应版本的 [Chrome Driver](https://sites.google.com/a/chromium.org/chromedriver/downloads)，并解压缩
2. 配置 Chrome Driver 路径
* `agtms-autotest/src/main/resources/autotest.properties`
```properties
auto.test.chrome.driver=/Users/saisimon/Downloads/chromedriver
```
3. 运行测试用例
* `agtms-autotest/src/test/java/net/saisimon/agtms/autotest/ChromeTest`

### Firefox
1. 下载最新版本的 [Gecko Driver](https://github.com/mozilla/geckodriver/releases)，并解压缩
2. 配置 Gecko Driver 路径
* `agtms-autotest/src/main/resources/autotest.properties`
```properties
auto.test.firefox.driver=/Users/saisimon/Downloads/geckodriver
```
3. 运行测试用例
* `agtms-autotest/src/test/java/net/saisimon/agtms/autotest/FirefoxTest`