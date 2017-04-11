#!/bin/bash
mvn package
oc start-build demo-blue --from-dir=. --follow