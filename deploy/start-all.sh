#!/bin/bash
# 生鲜农产品溯源电商平台 - 一键启动脚本
# 使用方式：bash start-all.sh

echo "=========================================="
echo "  生鲜农产品溯源电商平台 - 启动中..."
echo "=========================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 未找到Java环境，请先安装JDK"
    exit 1
fi

echo ""
echo ">>> 启动 溯源+农户端 (端口 8080)..."
nohup java -jar fresh-trace-traceability-1.0.0.jar > log-trace.log 2>&1 &
PID1=$!
echo "    PID: $PID1"

echo ""
echo ">>> 启动 管理员后端 (端口 8081)..."
nohup java -jar fresh-trace-admin-1.0.0.jar > log-admin.log 2>&1 &
PID2=$!
echo "    PID: $PID2"

echo ""
echo ">>> 启动 用户端后端 (端口 8082)..."
nohup java -jar foodfound-1.0-SNAPSHOT.jar > log-user.log 2>&1 &
PID3=$!
echo "    PID: $PID3"

echo ""
echo "=========================================="
echo "  等待启动完成（约30秒）..."
echo "=========================================="
sleep 30

echo ""
echo "  检查服务状态："
echo ""

# 检查8080
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/trace/list 2>/dev/null | grep -q "200"; then
    echo "  ✅ 溯源+农户端   http://localhost:8080  运行正常"
else
    echo "  ❌ 溯源+农户端   http://localhost:8080  启动失败，请查看 log-trace.log"
fi

# 检查8081
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/admin/statistics/home 2>/dev/null | grep -q "200"; then
    echo "  ✅ 管理员后端    http://localhost:8081  运行正常"
else
    echo "  ❌ 管理员后端    http://localhost:8081  启动失败，请查看 log-admin.log"
fi

# 检查8082
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/home 2>/dev/null | grep -q "200"; then
    echo "  ✅ 用户端后端    http://localhost:8082  运行正常"
else
    echo "  ❌ 用户端后端    http://localhost:8082  启动失败，请查看 log-user.log"
fi

echo ""
echo "=========================================="
echo "  日志文件："
echo "    tail -f log-trace.log"
echo "    tail -f log-admin.log"
echo "    tail -f log-user.log"
echo ""
echo "  停止服务："
echo "    kill $PID1 $PID2 $PID3"
echo "=========================================="
