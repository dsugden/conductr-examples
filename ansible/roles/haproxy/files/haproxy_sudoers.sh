#!/bin/bash

echo "conductr-haproxy ALL=(root) NOPASSWD: /etc/init.d/haproxy reload" | sudo tee -a /etc/sudoers