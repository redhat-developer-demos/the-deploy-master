# The Deploy Master - From Basic to Zero Downtime, Blue:Green, A:B, and Canary
Repository with scripts to demo "The Deploy Master"

Slides are available at http://bit.ly/thedeploymaster

This demo uses OSE 3.3.

Start it with the following command:

    oc cluster up --create-machine=true   \
                --use-existing-config   \
                --host-data-dir=/mydata \

After that, execute `./setup.sh` and wait (approximately 20 minutes) for the demo application to be deployed.

The OpenShift console credentials are developer/developer