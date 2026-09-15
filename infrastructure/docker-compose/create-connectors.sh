#!/bin/bash

# Check Debezium Connect status
servicesCheckResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://localhost:8083)

echo "Result status code:" "$servicesCheckResult"

while [[ ! $servicesCheckResult == "200" ]]; do
  >&2 echo "Debezium is not running yet!"
  sleep 2
  servicesCheckResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://localhost:8083)
done

# Optional: Clean up existing connectors before registering
echo "Cleaning up existing connectors if any..."
connectors=("order-payment-connector" "order-restaurant-connector" "payment-order-connector" "restaurant-order-connector")
for connector in "${connectors[@]}"; do
  curl -s -X DELETE "http://localhost:8083/connectors/$connector" > /dev/null
done

echo "Creating debezium connectors..."

curl --location --request POST 'http://localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "order-payment-connector",
  "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "host.docker.internal",
      "database.port": "5432",
      "database.user": "postgres",
      "database.password": "password",
      "database.dbname" : "postgres",
      "database.server.name": "PostgreSQL-15",
      "table.include.list": "order.payment_outbox",
      "topic.prefix": "debezium",
      "tombstones.on.delete" : "false",
      "slot.name" : "order_payment_outbox_slot",
      "plugin.name": "pgoutput",
      "auto.create.topics.enable": false,
      "auto.register.schemas": false
      }
 }'

echo ""

curl --location --request POST 'http://localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "order-restaurant-connector",
  "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "host.docker.internal",
      "database.port": "5432",
      "database.user": "postgres",
      "database.password": "password",
      "database.dbname" : "postgres",
      "database.server.name": "PostgreSQL-15",
      "table.include.list": "order.restaurant_approval_outbox",
      "topic.prefix": "debezium",
      "tombstones.on.delete" : "false",
      "slot.name" : "order_restaurant_approval_outbox_slot",
      "plugin.name": "pgoutput",
      "auto.create.topics.enable": false,
      "auto.register.schemas": false
      }
 }'

echo ""

curl --location --request POST 'http://localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "payment-order-connector",
  "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "host.docker.internal",
      "database.port": "5432",
      "database.user": "postgres",
      "database.password": "password",
      "database.dbname" : "postgres",
      "database.server.name": "PostgreSQL-15",
      "table.include.list": "payment.order_outbox",
      "topic.prefix": "debezium",
      "tombstones.on.delete" : "false",
      "slot.name" : "payment_order_outbox_slot",
      "plugin.name": "pgoutput",
      "auto.create.topics.enable": false,
      "auto.register.schemas": false
      }
 }'

echo ""

curl --location --request POST 'http://localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "restaurant-order-connector",
  "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "host.docker.internal",
      "database.port": "5432",
      "database.user": "postgres",
      "database.password": "password",
      "database.dbname" : "postgres",
      "database.server.name": "PostgreSQL-15",
      "table.include.list": "restaurant.order_outbox",
      "topic.prefix": "debezium",
      "tombstones.on.delete" : "false",
      "slot.name" : "restaurant_order_outbox_slot",
      "plugin.name": "pgoutput",
      "auto.create.topics.enable": false,
      "auto.register.schemas": false
      }
 }'

echo -e "\nWaiting for connectors to initialize..."
sleep 3

# Check status of registered connectors
echo -e "\nChecking registered connector statuses:"
for connector in "${connectors[@]}"; do
  status=$(curl -s "http://localhost:8083/connectors/$connector/status" | grep -o '"state":"[^"]*"' | head -1)
  echo "$connector status: $status"
done

echo -e "\nStart-up completed"