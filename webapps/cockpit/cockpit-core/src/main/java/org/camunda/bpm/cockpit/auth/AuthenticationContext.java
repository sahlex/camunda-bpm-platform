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
package org.camunda.bpm.cockpit.auth;

/**
 * <p>provides context about the currently authenticated principal</p>
 * 
 * @author Daniel Meyer
 *
 */
public interface AuthenticationContext {
  
  /**
   * @return Returns the current {@link AuthenticatedPrincipal} (ie. the user
   *         that is currently authenticated).</p>
   */
  public AuthenticatedPrincipal getCurrentPrincipal();
  
  /**
   * clear the current authentication. (Effectively logg out the current user).
   */
  public void clearCurrentPrincipal();

}
