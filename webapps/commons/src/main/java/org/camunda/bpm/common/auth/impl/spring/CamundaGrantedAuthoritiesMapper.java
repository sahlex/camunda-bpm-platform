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
package org.camunda.bpm.common.auth.impl.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

/**
 * <p>Takes a java.util.Map as input and allows mapping your backend system authorities to 
 * camunda authorities. Example: assume you are connecting to an existing company LDAP directory. 
 * Then you will have to map the group or role attributes you find there to camunda authorities.
 * </p>
 * 
 * <p><strong>Usage Example:</strong>
 * 
 * <pre>
 * &lt;bean id="camundaGrantedAuthoritiesMapper" class="org.camunda.bpm.common.auth.impl.spring.CamundaGrantedAuthoritiesMapper"&gt;
 *   &lt;property name="roleNameMap"&gt;
 *     &lt;map&gt;
 *       &lt;entry key="ROLE_DEVELOPER" value="ROLE_COCKPIT_USER" /&gt;
 *       &lt;entry key="ROLE_MY_CORP_ADMIN" value="ROLE_ADMINISTRATOR" /&gt;
 *     &lt;/map&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * <p>In this example we map the roles <code>developer and my_corp_admin</code> to camunda authorities as used by the webapplications.</p>
 * 
 * <p>The camundaGrantedAuthoritiesMapper can then be injected into a an authentiction provider:</p>
 *  
 * <pre>
 * &lt;bean id="ldapAuthProvider" class="org.springframework.security.ldap.authentication.LdapAuthenticationProvider"&gt;
 *    ...
 *   &lt;property name="authoritiesMapper" ref="camundaGrantedAuthoritiesMapper" /&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * @author Daniel Meyer
 *
 */
public class CamundaGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {
  
  protected Map<String, String> roleNameMap = new HashMap<String, String>();

  public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
    
    final Set<GrantedAuthority> mappedAuthoritites = new HashSet<GrantedAuthority>();
    
    for (GrantedAuthority grantedAuthority : authorities) {
      
      final String authority = grantedAuthority.getAuthority();
      final String mappedAuthority = roleNameMap.get(authority);
      
      if(mappedAuthority != null) {
        mappedAuthoritites.add(new SimpleGrantedAuthority(mappedAuthority));
        
      } else {
        mappedAuthoritites.add(grantedAuthority);
        
      }
      
    }
    return mappedAuthoritites;
  }
  
  // getters / setters //////////////////////////////////////
  
  public void setRoleNameMap(Map<String, String> roleNameMap) {
    this.roleNameMap = roleNameMap;
  }
  
  public Map<String, String> getRoleNameMap() {
    return roleNameMap;
  }

}
