#!/bin/bash
echo "Type the year that you want to add to the session, followed by [ENTER]:"

read value

curl -b cookies.txt -c cookies.txt http://demo.$OPENSHIFT_IP.nip.io/demo/api/session/add/$value