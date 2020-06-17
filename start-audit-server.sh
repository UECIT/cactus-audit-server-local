#!/usr/bin/env sh
echo "Starting a Spring Boot Audit Server on $HOSTNAME"
exec java -jar cactus-audit-server-local.war
