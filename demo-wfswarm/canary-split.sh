#!/bin/bash
oc patch route/demo -p '{"spec": {"to": {"name": "demo" }}}'
oc patch route demo -p '{"spec":{"alternateBackends":null}}'
