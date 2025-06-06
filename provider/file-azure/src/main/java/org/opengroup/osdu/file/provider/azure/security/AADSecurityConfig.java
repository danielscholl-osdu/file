/*
 * Copyright © Microsoft Corporation
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

package org.opengroup.osdu.file.provider.azure.security;

import com.azure.spring.cloud.autoconfigure.implementation.aad.filter.AadAppRoleStatelessAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(value = "azure.istio.auth.enabled", havingValue = "false", matchIfMissing = false)
public class AADSecurityConfig {
  @Autowired
  private AadAppRoleStatelessAuthenticationFilter appRoleAuthFilter;

  private static final String[] AUTH_ALLOWLIST = {"/", "/index.html",
      "/actuator/*",
      "/v2/api-docs.yaml",
      "/v2/api-docs/swagger-config",
      "/v2/api-docs/**",
      "/v2/info",
      "/v2/swagger",
      "/v2/swagger-ui/**"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement((sess) -> sess.sessionCreationPolicy(SessionCreationPolicy.NEVER))
        .authorizeHttpRequests(request -> request.requestMatchers(AUTH_ALLOWLIST).permitAll())
        .addFilterBefore(appRoleAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
