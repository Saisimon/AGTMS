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
├── README.md           README 文件
├── start.cmd           一键启动脚本 (Windows)
└── start               一键启动脚本 (Unix)
```

## 要求
1. [JRE(JDK) 8+](https://www.java.com)
2. [Node.js](https://nodejs.org/)
3. RAM 2G+

## 最小安装并启动
* `默认使用 H2 内存数据库，每次重启数据会重制。要想保存数据，请自行配置数据库连接`
### 一键启动
1. 执行启动脚本
* **`start.cmd 默认会杀掉占用 7892端口的进程，请确认以后再执行操作`**
```sh
# Unix
./start

# Windows
start.cmd
```
2. 访问
```html
http://localhost:8080
```
3. 日志
```
data
└── logs 
    └── web.log    Web 服务日志
```

### 分步启动
1. 打包
```sh
# Unix
./mvnw clean package

# Windows
mvnw.cmd clean package
```
2. 启动Web 服务 (agtms-web)
```sh
java -jar agtms-parent/agtms-web/target/agtms-web.jar
```
3. 启动前端页面 (agtms-vue)
```sh
cd agtms-vue
npm install
npm run serve
```
4. 访问
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

## 待完成
1. ~~用户初次登录修改密码~~
2. ~~用户信息编辑~~
3. ~~管理员重置密码~~
4. ~~图片编辑上传~~
5. ~~富文本编辑~~
6. 导入导出优化
7. 图片上传优化
8. 完善自动化测试
9. 系统引导
10. 数据可视化
11. Gradle
12. Docker
13. ~~Standalone Branch~~
14. ...