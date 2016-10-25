#!/bin/bash
#Opening Openshift console
open https://$(docker-machine ip openshift):8443
echo "Log as developer/developer"

#Download xPaaS ImageStream
oc create -f https://raw.githubusercontent.com/wildfly-swarm/sti-wildflyswarm/master/1.0/wildflyswarm-sti-all.json
echo "Waiting 5 seconds...."
sleep 5
oc logs -f bc/wildflyswarm-10-centos7-build

#Create the application GREEN
oc new-app --name demo wildflyswarm-10-centos7~https://github.com/redhat-developer-demos/the-deploy-master --context-dir=/demo-wfswarm
#Create the application BLUE
oc new-app --name demo-blue wildflyswarm-10-centos7~https://github.com/redhat-developer-demos/the-deploy-master --context-dir=/demo-wfswarm

#Expose the route GREEN
oc expose svc demo --hostname=demo.$(docker-machine ip openshift).nip.io 

#Enable incremental builds GREEN
oc patch bc/demo -p '{"spec":{"strategy":{"type":"Source","sourceStrategy":{"incremental":true}}}}'
#Enable incremental builds BLUE
oc patch bc/demo-blue -p '{"spec":{"strategy":{"type":"Source","sourceStrategy":{"incremental":true}}}}'

#Enable readiness probe GREEN
oc set probe dc/demo --readiness --get-url=http://:8080/api/health
#Enable readiness probe BLUE
oc set probe dc/demo-blue --readiness --get-url=http://:8080/api/health

#Scale GREEN application
oc scale dc/demo --replicas=3

echo "Wait until the builds complete... They should take approximately 20 minutes for the first run"
oc logs bc/demo --follow
oc logs bc/demo-blue --follow
