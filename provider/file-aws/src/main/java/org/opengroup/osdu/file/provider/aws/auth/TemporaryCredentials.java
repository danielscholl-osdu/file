/**
* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*      http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.opengroup.osdu.file.provider.aws.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryCredentials {

    private static String connStringFormat = "AccessKeyId=%s;SecretAccessKey=%s;SessionToken=%s;Expiration=%s";

    @JsonProperty("accessKeyId")
    private String accessKeyId;

    @JsonProperty("secretAccessKey")
    private String secretAccessKey;

    @JsonProperty("sessionToken")
    private String sessionToken;

    @JsonProperty("expiration")
    private Date expiration;

    public String toConnectionString() {
        if (accessKeyId == null || accessKeyId.equals("")) {
            return "";
        }

        String expirationString = DateTimeFormatter.ISO_INSTANT.format(expiration.toInstant());
        return String.format(connStringFormat, accessKeyId, secretAccessKey, sessionToken, expirationString);
    }
}