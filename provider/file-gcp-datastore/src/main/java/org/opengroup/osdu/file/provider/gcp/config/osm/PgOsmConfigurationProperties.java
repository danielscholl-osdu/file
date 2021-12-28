/*
  Copyright 2021 Google LLC
  Copyright 2021 EPAM Systems, Inc

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package org.opengroup.osdu.file.provider.gcp.config.osm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "osmDriver", havingValue = "postgres")
@ConfigurationProperties(prefix = "osm.postgres")
@Getter
@Setter
public class PgOsmConfigurationProperties {

    private String partitionPropertiesPrefix = "osm.postgres";

    private Integer maximumPoolSize = 40;
    private Integer minimumIdle = 0;
    private Integer idleTimeout = 30000;
    private Integer maxLifetime = 1800000;
    private Integer connectionTimeout = 30000;
}
