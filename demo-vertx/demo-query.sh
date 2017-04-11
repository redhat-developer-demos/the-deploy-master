#!/bin/sh
while true; do curl http://demo.$OPENSHIFT_IP.nip.io/api/hello/Rafael; echo; sleep 1; done

