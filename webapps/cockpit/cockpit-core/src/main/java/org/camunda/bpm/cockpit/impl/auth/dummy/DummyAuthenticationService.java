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
package org.camunda.bpm.cockpit.impl.auth.dummy;

import java.util.HashSet;

import org.camunda.bpm.cockpit.auth.AuthenticatedPrincipal;
import org.camunda.bpm.cockpit.auth.AuthenticationException;
import org.camunda.bpm.cockpit.auth.AuthenticationService;
import org.camunda.bpm.cockpit.auth.AuthenticationToken;

/**
 * <p>Dummy implementation of the authentication services</p>
 * 
 * <p>This service accepts all authentication requests.</p>
 * 
 * @author Daniel Meyer
 *
 */
public class DummyAuthenticationService implements AuthenticationService {
  
  private AuthenticatedPrincipal principal = null;

  public AuthenticatedPrincipal authenticate(AuthenticationToken token) throws AuthenticationException {
    String[] parts = token.getParts();
    principal = new DummyAuthenticatedPrincipal(parts[0], new HashSet<String>());
    return getCurrentPrincipal();
  }

  public AuthenticatedPrincipal getCurrentPrincipal() {
    return principal;
  }
  
  public void clearCurrentPrincipal() {
    principal = null;    
  }

}
