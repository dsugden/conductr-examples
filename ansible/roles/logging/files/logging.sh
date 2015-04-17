#!/bin/bash

echo '$ModLoad imtcp' | sudo tee -a /etc/rsyslog.d/conductr.conf
echo '$InputTCPServerRun 514' | sudo tee -a /etc/rsyslog.d/conductr.conf
sudo service rsyslog restart