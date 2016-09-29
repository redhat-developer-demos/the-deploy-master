#!/bin/bash
oc new-app bonjour-blue -l app=bonjour-blue,hystrix.enabled=true
oc set probe dc/bonjour-blue --readiness --get-url=http://:8080/api/health