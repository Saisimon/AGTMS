@echo off
chcp 65001

@REM 创建日志文件夹
md data\eureka
md data\web
md data\zuul

for /f "tokens=5" %%i in ('netstat -ano ^| findstr 7890') do (
    taskkill /f /pid %%i
    echo "agtms-eureka 已停止."
    goto eureka
)
:eureka
for /f "tokens=5" %%i in ('netstat -ano ^| findstr 7892') do (
    taskkill /f /pid %%i
    echo "agtms-web 已停止."
    goto web
)
:web
for /f "tokens=5" %%i in ('netstat -ano ^| findstr 7891') do (
    taskkill /f /pid %%i
    echo "agtms-zuul 已停止."
    goto zuul
)
:zuul

@REM Maven 打包
CALL mvnw.cmd clean install -DskipTests -Ddockerfile.skip=true

@REM 启动 agtms-eureka
START /b java.exe -jar -Xms512m -Xmx512m -Xmn192m -Xss256k agtms-eureka/target/agtms-eureka.jar > data/eureka/logs/agtms-eureka.log 2>&1
echo "agtms-eureka 启动中..."
ping 192.0.2.2 -n 1 -w 10000 > nul
echo "agtms-eureka 已启动."

@REM 启动 agtms-web
START /b java.exe -jar -Xms1g -Xmx1g -Xmn384m -Xss256k agtms-parent/agtms-web/target/agtms-web.jar > data/web/logs/agtms-web.log 2>&1
echo "agtms-web 启动中..."
ping 192.0.2.2 -n 1 -w 10000 > nul
echo "agtms-web 已启动."

@REM 启动 agtms-zuul
START /b java.exe -jar -Xms512m -Xmx512m -Xmn192m -Xss256k agtms-zuul/target/agtms-zuul.jar > data/zuul/logs/agtms-zuul.log 2>&1
echo "agtms-zuul 启动中..."
ping 192.0.2.2 -n 1 -w 10000 > nul
echo "agtms-zuul 已启动."

@REM 启动 agtms-vue
cd agtms-vue
echo "agtms-vue 启动中..."
CALL npm.cmd install
CALL npm.cmd run serve