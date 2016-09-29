#!/bin/bash
oc patch svc/bonjour -p '{"spec":{"selector":{"app":"bonjour","deploymentconfig":"bonjour"}}}'