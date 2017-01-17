#!/bin/bash
#Opening Openshift console
open https://$(docker-machine ip openshift):8443
echo "Log as developer/developer"

#Login and prepare the project
oc login --insecure-skip-tls-verify=true -u developer -p developer $(docker-machine ip openshift):8443
oc new-project deploy-master


#Download xPaaS ImageStream
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/jboss-image-streams.json
until oc get is jboss-eap70-openshift|grep 1.4; do echo "Waiting ImageStream download..."; sleep 5; done

#Create the application GREEN
oc new-app --name demo -e SWARM_JVM_ARGS=-Xmx512m jboss-eap70-openshift~https://github.com/redhat-developer-demos/the-deploy-master --context-dir=/demo-eap
#Create the application BLUE
oc new-app --name demo-blue -e SWARM_JVM_ARGS=-Xmx512m jboss-eap70-openshift~https://github.com/redhat-developer-demos/the-deploy-master --context-dir=/demo-eap

#Expose the route GREEN
oc expose svc demo --hostname=demo.$(docker-machine ip openshift).nip.io 

#Enable incremental builds GREEN
oc patch bc/demo -p '{"spec":{"strategy":{"type":"Source","sourceStrategy":{"incremental":true}}}}'
#Enable incremental builds BLUE
oc patch bc/demo-blue -p '{"spec":{"strategy":{"type":"Source","sourceStrategy":{"incremental":true}}}}'


#Enable cluster GREEN
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default -n $(oc project -q)
oc patch dc/demo -p '{"spec":{"template":{"spec":{"containers":[{"name":"demo","ports":[{"containerPort":8888,"protocol":"TCP","name":"ping"}]}]}}}}'
oc env dc/demo -e OPENSHIFT_KUBE_PING_NAMESPACE=myproject  OPENSHIFT_KUBE_PING_LABELS=app=demo

#Enable cluster BLUE
oc patch dc/demo-blue -p '{"spec":{"template":{"spec":{"containers":[{"name":"demo-blue","ports":[{"containerPort":8888,"protocol":"TCP","name":"ping"}]}]}}}}'
oc env dc/demo-blue -e OPENSHIFT_KUBE_PING_NAMESPACE=myproject  OPENSHIFT_KUBE_PING_LABELS=app=demo


#Enable readiness probe GREEN
oc set probe dc/demo --readiness --get-url=http://:8080/demo/api/session/health
#Enable readiness probe BLUE
oc set probe dc/demo-blue --readiness --get-url=http://:8080/demo/api/session/health

#Scale GREEN application
oc scale dc/demo --replicas=3

echo "Wait until the builds complete... They should take approximately 20 minutes for the first run"
oc logs bc/demo --follow
oc logs bc/demo-blue --follow
