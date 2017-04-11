#!/bin/sh
while true; do curl -b cookies.txt -c cookies.txt http://demo.$OPENSHIFT_IP.nip.io/demo/api/session/get; echo; sleep 1; done

