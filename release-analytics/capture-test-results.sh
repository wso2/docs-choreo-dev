#!/bin/bash

# This script is used to capture the integration test results from the devops
# and upload them to BigQuery

# Install virtual env
echo "----------------------------------"
echo "Installing virtual env"
echo "----------------------------------"
python3 -m venv venv

# Activate virtual env
echo "----------------------------------"
echo "Activating virtual env"
echo "----------------------------------"
source venv/bin/activate

# Install dependencies
echo "----------------------------------"
echo "Installing dependencies"
echo "----------------------------------"
python -m pip install -r requirements.txt

# Capture integration test results
echo "----------------------------------"
echo "Capturing integration test results"
echo "----------------------------------"
python capture_test_results.py "$1"

echo "----------------------------------"
echo "Deactivate virtual env"
echo "----------------------------------"
# Deactivate virtual env
deactivate