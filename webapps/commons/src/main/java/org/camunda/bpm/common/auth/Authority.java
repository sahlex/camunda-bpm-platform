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
package org.camunda.bpm.common.auth;

/**
 * <p>An authority is a role that is granted to an {@link AuthenticatedPrincipal}.</p>
 * 
 * <p>An authority represents a set of use cases for which a user must be authorized. 
 * Example: Some use cases may only be performed by an administraor. In that case 
 * the currently authenticated user ( {@link AuthenticatedPrincipal} ) must be granted the 
 * authority "ADMINISTRAOTR".
 *
 * <p>See {@link CamundaAuthority} for a set of built-in {@link Authority Authorities}.</p>
 * 
 * 
 * @see CamundaAuthority
 * 
 * @author Daniel Meyer
 *
 */
public interface Authority {
  
  /** return the Id of the Authority */
  public String getId();
  
  /** the human readable name of the authority */
  public String getName();
  
  /** allows to check whether a principal has been granted this authority.
   * @return true if this authority has been granted to the principal, 
   * false otherwise. */
  public boolean isGrantedTo(AuthenticatedPrincipal authenticatedPrincipal);

}
