#!/bin/sh
while true; do curl http://demo.$(docker-machine ip openshift).nip.io/api/hello; echo; sleep 1; done

