#!/bin/bash
mvn clean
oc start-build demo --from-dir=.. --follow