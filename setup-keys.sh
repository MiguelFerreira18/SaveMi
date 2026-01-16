#!/bin/bash
mkdir -p secrets
openssl genrsa -out secrets/rsa.private.key 4096
openssl rsa -in secrets/rsa.private.key -pubout -out secrets/rsa.public.key
chmod 600 secrets/rsa.private.key 2>/dev/null || true
chmod 644 secrets/rsa.public.key 2>/dev/null || true
echo "Keys generated successfully"
