#!/bin/bash
oc patch route/demo -p '{"spec": {"to": {"name": "demo-blue" }}}'