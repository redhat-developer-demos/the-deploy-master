#!/bin/bash
mvn clean -f demo/pom.xml
oc start-build demo --from-dir=. --follow