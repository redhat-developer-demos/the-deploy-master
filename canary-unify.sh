#!/bin/bash
oc patch svc/bonjour -p '{"spec":{"selector":{"svc":"canary-bonjour","app": null, "deploymentconfig": null}, "sessionAffinity":"ClientIP"}}'