#!/bin/bash
oc scale dc/bonjour --replicas=4
oc scale dc/aloha --replicas=4
