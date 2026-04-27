#!/bin/sh

MYSQL_USER=$1
MYSQL_PASSWORD=$2
MYSQL_DATABASE=$3
BACKUP_FILE=$4

docker exec -i mysql-container mysql -u "${MYSQL_USER}" -p "${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" < BACKUP_FILE
