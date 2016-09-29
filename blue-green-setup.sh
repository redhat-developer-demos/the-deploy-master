#!/bin/bash
oc new-build --binary --name=bonjour-blue -l app=bonjour-blue
