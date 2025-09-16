#!/bin/bash

# Database Management API Test Script
echo "🗄️ Database Management API Test"
echo "================================="

BASE_URL="http://localhost:9090"

echo ""
echo "1. Testing API Health Check..."
curl -s "$BASE_URL/api/database/hello"
echo ""

echo ""
echo "2. Testing MySQL Delete Command Generation..."
curl -s -X POST "$BASE_URL/api/database/delete/mysql" \
  -H "Content-Type: application/json" \
  -d '{"host": "localhost", "username": "admin", "databaseName": "prod_db"}' | jq .
echo ""

echo ""
echo "3. Testing PostgreSQL Delete Command Generation with Port..."
curl -s -X POST "$BASE_URL/api/database/delete/postgresql" \
  -H "Content-Type: application/json" \
  -d '{"host": "localhost", "username": "postgres", "databaseName": "prod_db", "port": 5432}' | jq .
echo ""

echo ""
echo "4. Testing MongoDB Delete Command Generation..."
curl -s -X POST "$BASE_URL/api/database/delete/mongodb" \
  -H "Content-Type: application/json" \
  -d '{"host": "localhost", "username": "admin", "databaseName": "prod_db", "port": 27017}' | jq .
echo ""

echo ""
echo "5. Testing Error Handling (Missing Parameters)..."
curl -s -X POST "$BASE_URL/api/database/delete/mysql" \
  -H "Content-Type: application/json" \
  -d '{"host": "localhost"}' | jq .
echo ""

echo ""
echo "✅ Test Complete!"
echo "⚠️  Remember: These are command generators only. Review and execute commands manually!"