#!/bin/sh
while true; do curl http://demo.$(docker-machine ip openshift).nip.io/demo/api/session/get; echo; sleep 1; done

