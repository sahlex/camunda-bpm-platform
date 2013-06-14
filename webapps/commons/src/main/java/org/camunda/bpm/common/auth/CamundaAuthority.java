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
 * <p>The list of built-in authorities in camunda BPM</p>
 * 
 * @author Daniel Meyer
 *
 */
public enum CamundaAuthority implements Authority {
  
  COCKPIT_USER("ROLE_COCKPIT_USER", "cockpit user"),
  TASKLIST_USER("ROLE_TASKLIST_USER", "tasklist user"),
  ADMINISTRATOR("ROLE_ADMINISTRATOR", "administrator");  
  
  // implementation ////////////////////////////////////////
  
  private String id;
  private String name;

  private CamundaAuthority(String id, String name) {
    this.id = id;
    this.name = name;
  }
  
  public String getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }

  public boolean isGrantedTo(AuthenticatedPrincipal authenticatedPrincipal) {
    for (Authority authority : authenticatedPrincipal.getAuthoritites()) {
      // the administrator has all authorities
      if(this.equals(authority) || ADMINISTRATOR.equals(authority)) {
        return true;
      }
    }
    return false;
  }

}
