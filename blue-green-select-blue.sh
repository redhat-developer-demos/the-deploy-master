#!/bin/bash
oc patch svc/bonjour -p '{"spec":{"selector":{"app":"bonjour-blue","deploymentconfig":"bonjour-blue"}}}'