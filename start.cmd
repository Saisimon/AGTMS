@echo off
chcp 65001

@REM 创建日志文件夹
md data\logs

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
CALL mvnw.cmd clean install

@REM 启动 agtms-eureka
START /b java.exe -jar -Xms1g -Xms1g agtms-eureka/target/agtms-eureka.jar > data/logs/eureka.log 2>&1
echo "agtms-eureka 启动中..."
ping 192.0.2.2 -n 1 -w 10000 > nul
echo "agtms-eureka 已启动."

@REM 启动 agtms-web
START /b java.exe -jar -Xms2g -Xms2g agtms-parent/agtms-web/target/agtms-web.jar > data/logs/web.log 2>&1
echo "agtms-web 启动中..."
ping 192.0.2.2 -n 1 -w 10000 > nul
echo "agtms-web 已启动."

@REM 启动 agtms-zuul
START /b java.exe -jar -Xms1g -Xms1g agtms-zuul/target/agtms-zuul.jar > data/logs/zuul.log 2>&1
echo "agtms-zuul 启动中..."
ping 192.0.2.2 -n 1 -w 10000 > nul
echo "agtms-zuul 已启动."

@REM 启动 agtms-vue
cd agtms-vue
echo "agtms-vue 启动中..."
CALL npm.cmd install
CALL npm.cmd run serve