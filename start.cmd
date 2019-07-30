@echo off
chcp 65001

echo "================================================================"
echo "          d8888  .d8888b.  88888888888 888b     d888  .d8888b.  "
echo "         d88888 d88P  Y88b     888     8888b   d8888 d88P  Y88b "
echo "        d88P888 888    888     888     88888b.d88888 Y88b.      "
echo "       d88P 888 888            888     888Y88888P888  'Y888b.   "
echo "      d88P  888 888  88888     888     888 Y888P 888     'Y88b. "
echo "     d88P   888 888    888     888     888  Y8P  888       '888 "
echo "    d8888888888 Y88b  d88P     888     888   '   888 Y88b  d88P "
echo "   d88P     888  'Y8888P88     888     888       888  'Y8888P'  "
echo "================================================================"

@REM 创建日志文件夹
md data\admin
md data\admin\logs
md data\web
md data\web\logs
md data\zuul
md data\zuul\logs

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
for /f "tokens=5" %%i in ('netstat -ano ^| findstr 7890') do (
    taskkill /f /pid %%i
    echo "agtms-admin 已停止."
    goto admin
)
:admin

@REM Maven 打包
CALL mvnw.cmd clean install -DskipTests -Ddockerfile.skip=true

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

@REM 启动 agtms-admin
START /b java.exe -jar -Xms512m -Xmx512m -Xmn192m -Xss256k agtms-admin/target/agtms-admin.jar > data/admin/logs/agtms-admin.log 2>&1
echo "agtms-admin 启动中..."
ping 192.0.2.2 -n 1 -w 10000 > nul
echo "agtms-admin 已启动."

@REM 启动 agtms-vue
cd agtms-vue
echo "agtms-vue 启动中..."
CALL npm.cmd install
CALL npm.cmd run serve