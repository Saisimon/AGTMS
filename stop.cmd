@echo off
chcp 65001

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