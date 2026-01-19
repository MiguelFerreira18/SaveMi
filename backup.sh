#!/bin/sh

while true; do
  TIMESTAMP=$(date +'%Y%m%d%H%M%S')
  echo "Backing up database..."
  mysqldump -h db -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" "${MYSQL_DATABASE}" | gzip >/backups/saveme-$TIMESTAMP.sql.gz
  echo "Backup done: /backups/saveme-$TIMESTAMP.sql.gz"
  sleep 1000
done
