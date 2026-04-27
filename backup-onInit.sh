#!/bin/sh

MYSQL_USER=$1
MYSQL_PASSWORD=$2
MYSQL_DATABASE=$3

docker exec mysql-container mysqldump -u "${MYSQL_USER}" -p "${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" > backup_$(date +%Y%m%d_%H%M%S).sql
