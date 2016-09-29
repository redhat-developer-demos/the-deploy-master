#!/bin/bash
oc patch dc/bonjour -p '{"spec":{"template":{"metadata":{"labels":{"svc":"canary-bonjour"}}}}}'
oc patch dc/bonjour-blue -p '{"spec":{"template":{"metadata":{"labels":{"svc":"canary-bonjour"}}}}}'