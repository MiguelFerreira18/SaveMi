#!/bin/sh

while true; do
  TIMESTAMP=$(date +'%Y%m%d%H%M%S')
  echo "Backing up database..."
  mysqldump -h db -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" | gzip >/backups/savemi-$TIMESTAMP.sql.gz
  echo "Backup done: /backups/savemi-$TIMESTAMP.sql.gz"
  sleep 1000
done
