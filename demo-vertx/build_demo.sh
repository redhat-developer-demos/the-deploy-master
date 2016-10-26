#!/bin/bash
mvn package
oc start-build demo --from-dir=. --follow