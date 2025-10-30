/*
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

package org.opengroup.osdu.file.provider.interfaces.delivery;


import org.opengroup.osdu.file.model.delivery.SignedUrl;

public interface IDeliveryStorageService {

    /**
     * Gets a signed url from an unsigned url
     *
     * @param unsignedUrl
     * @param authorizationToken
     * @return
     */
    SignedUrl createSignedUrl(String unsignedUrl, String authorizationToken);

    /**
     * Gets a signed url from an unsigned url
     *
     * @param srn
     * @param unsignedUrl
     * @param authorizationToken
     * @return
     */
    default SignedUrl createSignedUrl(String srn, String unsignedUrl, String authorizationToken) {
        return createSignedUrl(unsignedUrl, authorizationToken);
    }
}
