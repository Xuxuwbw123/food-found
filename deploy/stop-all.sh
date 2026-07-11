#!/bin/bash
# 停止所有服务

echo "正在停止所有服务..."
pkill -f "fresh-trace-traceability" 2>/dev/null
pkill -f "fresh-trace-admin" 2>/dev/null
pkill -f "foodfound" 2>/dev/null
echo "✅ 所有服务已停止"
