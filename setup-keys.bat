@echo off
if not exist secrets mkdir secrets
openssl genrsa -out secrets\rsa.private.key 4096
openssl rsa -in secrets\rsa.private.key -pubout -out secrets\rsa.public.key
echo Keys generated successfully
