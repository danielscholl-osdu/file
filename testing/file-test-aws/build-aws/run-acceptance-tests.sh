#!/bin/bash
# Copyright © Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -e

# Store current directory
CUR_DIR=$(pwd)
SCRIPT_SOURCE_DIR=$(dirname "$0")
cd "$SCRIPT_SOURCE_DIR"

# Required variables for the tests
export AWS_BASE_URL="https://${AWS_DOMAIN}"

export TEST_OPENID_PROVIDER_CLIENT_ID="integration-tester"
export PRIVILEGED_USER_OPENID_PROVIDER_CLIENT_ID="${TEST_OPENID_PROVIDER_CLIENT_ID}"
export TEST_OPENID_PROVIDER_URL="https://keycloak.$CIMPL_DOMAIN/realms/osdu"
export PRIVILEGED_USER_OPENID_PROVIDER_CLIENT_SECRET="${CIMPL_OPENID_PROVIDER_CLIENT_SECRET}"
export INTEGRATION_TESTER_EMAIL="${AWS_COGNITO_AUTH_PARAMS_USER}"

export STORAGE_HOST="${AWS_BASE_URL}/api/storage/v2/"
export FILE_SERVICE_HOST="${AWS_BASE_URL}/api/file/v2"
export PRIVATE_TENANT1="${AWS_DEFAULT_DATA_PARTITION_ID_TENANT1}"
export PRIVATE_TENANT2="tenant2"
export SHARED_TENANT="${AWS_DEFAULT_DATA_PARTITION_ID_TENANT2}"
export TENANT_NAME="${AWS_TENANT_NAME}"
export ACL_VIEWERS="data.default.viewers"
export ACL_OWNERS="data.default.owners"
export ENTITLEMENTS_DOMAIN="example.com"
export LEGAL_TAG="${AWS_LEGAL_TAG}"
export TEST_OPENID_PROVIDER_CLIENT_SECRET="${CIMPL_OPENID_PROVIDER_CLIENT_SECRET}"

export AWS_COGNITO_AUTH_FLOW="USER_PASSWORD_AUTH"
export COGNITO_NAME="$(aws ssm get-parameter --name "/osdu/instances/${OSDU_INSTANCE_NAME}/config/cognito/name" --query Parameter.Value --output text --region $AWS_REGION)"
export AWS_COGNITO_CLIENT_ID="$(aws ssm get-parameter --name "/osdu/cognito/${COGNITO_NAME}/client/id" --query Parameter.Value --output text --region $AWS_REGION)"
export PRIVILEGED_USER_TOKEN=$(aws cognito-idp initiate-auth --region ${AWS_REGION} --auth-flow ${AWS_COGNITO_AUTH_FLOW} --client-id ${AWS_COGNITO_CLIENT_ID} --auth-parameters USERNAME=${AWS_COGNITO_AUTH_PARAMS_USER},PASSWORD=${AWS_COGNITO_AUTH_PARAMS_PASSWORD} --query AuthenticationResult.AccessToken --output text)
export SIGNED_URL_EXPIRY_TIME_MINUTES="15"

# Run the tests
mvn clean test
TEST_EXIT_CODE=$?

# Return to original directory
cd "$CUR_DIR"

# Copy test reports if output directory is specified
if [ -n "$1" ]; then
  mkdir -p "$1/file-acceptance-test"
  cp -R "$SCRIPT_SOURCE_DIR/target/surefire-reports/"* "$1/file-acceptance-test"
fi

exit $TEST_EXIT_CODE
