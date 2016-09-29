#!/bin/sh
while true; do curl -c cookies.txt -b cookies.txt http://bonjour-helloworld-msa.app.redhatmsa.com/api/bonjour; echo; sleep 1; done

