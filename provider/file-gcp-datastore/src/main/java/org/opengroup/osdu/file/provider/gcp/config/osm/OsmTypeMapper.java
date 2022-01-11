/*
 * Copyright 2021 Google LLC
 * Copyright 2021 EPAM Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opengroup.osdu.file.provider.gcp.config.osm;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Key;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import org.opengroup.osdu.core.gcp.osm.persistence.IdentityTranslator;
import org.opengroup.osdu.core.gcp.osm.translate.Instrumentation;
import org.opengroup.osdu.core.gcp.osm.translate.TypeMapper;
import org.opengroup.osdu.file.provider.gcp.model.entity.FileLocationOsm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "osmDriver")
public class OsmTypeMapper extends TypeMapper {

  public OsmTypeMapper() {
    super(ImmutableList.of(
        new Instrumentation<>(
            FileLocationOsm.class,
            Collections.emptyMap(),
            ImmutableMap.of(
                "createdAt", Timestamp.class
            ),
            new IdentityTranslator<>(
                FileLocationOsm::getId,
                (f, o) -> f.setId(((Key) o).getId())
            ), Collections.singletonList("id")
        )));
  }
}
