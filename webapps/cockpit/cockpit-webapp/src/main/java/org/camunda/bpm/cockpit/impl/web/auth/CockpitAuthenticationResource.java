/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.camunda.bpm.cockpit.impl.web.auth;

import org.camunda.bpm.cockpit.Cockpit;
import org.camunda.bpm.common.auth.AuthenticatedPrincipal;
import org.camunda.bpm.common.auth.AuthenticationException;
import org.camunda.bpm.common.auth.AuthenticationService;
import org.camunda.bpm.common.auth.CamundaAuthority;
import org.camunda.bpm.common.auth.resource.AuthenticationResource;

/**
 * <p>Authentication resource for cockpit providing the Authentication service</p> 
 * 
 * @author Daniel Meyer
 *
 */
public class CockpitAuthenticationResource extends AuthenticationResource {

  protected AuthenticationService getAuthenticationService() {
    return Cockpit.getRuntimeDelegate().getAuthenticationService();
  }
  
  protected void checkAuthentication(AuthenticatedPrincipal authenticatedPrincipal) throws AuthenticationException {
    if(!CamundaAuthority.COCKPIT_USER.isGrantedTo(authenticatedPrincipal)) {
      throw new AuthenticationException("user must be granted authority "+CamundaAuthority.COCKPIT_USER.getName());
    }
  }

}
