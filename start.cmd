@echo off
chcp 65001

@REM 创建日志文件夹
md data\web

for /f "tokens=5" %%i in ('netstat -ano ^| findstr 7892') do (
    taskkill /f /pid %%i
    echo "agtms-web 已停止."
    goto web
)
:web

@REM Maven 打包
CALL mvnw.cmd clean install -DskipTests -Ddockerfile.skip=true

@REM 启动 agtms-web
START /b java.exe -jar -Xms1g -Xmx1g -Xmn384m -Xss256k agtms-parent/agtms-web/target/agtms-web.jar > data/web/logs/agtms-web.log 2>&1
echo "agtms-web 启动中..."
ping 192.0.2.2 -n 1 -w 10000 > nul
echo "agtms-web 已启动."

@REM 启动 agtms-vue
cd agtms-vue
echo "agtms-vue 启动中..."
CALL npm.cmd install
CALL npm.cmd run serve