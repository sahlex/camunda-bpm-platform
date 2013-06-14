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
import java.util.HashSet;
import java.util.Set;

import org.camunda.bpm.common.auth.AuthenticatedPrincipal;
import org.camunda.bpm.common.auth.Authority;
import org.camunda.bpm.common.auth.CamundaAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * <p>An {@link AuthenticatedPrincipal} implementation backed by a 
 * Spring security {@link Authentication} Object.</p>
 * 
 * <p>This implementation is immutable (ie. the wrapped 
 * principal cannot be changed). However it does deleagte all method 
 * invocations to the wrapped {@link Authentication} such that changes 
 * in the underlying object are reflected in the method calls to this 
 * object.</p>
 * 
 * @author Daniel Meyer
 *
 */
public class SpringAuthenticatedPrincipal implements AuthenticatedPrincipal {

  private static final long serialVersionUID = 1L;
  
  protected final Authentication authentication;
  
  public SpringAuthenticatedPrincipal(Authentication authentication) {
    this.authentication = authentication;    
  }

  public String getName() {
    return authentication.getName();
  }

  public Set<Authority> getAuthoritites() {
    return transformRoles();
  }

  protected Set<Authority> transformRoles() {
    final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    
    HashSet<Authority> result = new HashSet<Authority>(authorities.size());
    
    for (GrantedAuthority grantedAuthority : authorities) {
      for (Authority authority : CamundaAuthority.values()) {
        if(authority.getId().equals(grantedAuthority.getAuthority())) {
          result.add(authority);       
        }
      } 
    }
    
    return result;
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof SpringAuthenticatedPrincipal) {
      return authentication.equals(obj);      
      
    } else {
      return super.equals(obj);
      
    }
  }
  
  public int hashCode() {
    return authentication.hashCode();
  }

}
