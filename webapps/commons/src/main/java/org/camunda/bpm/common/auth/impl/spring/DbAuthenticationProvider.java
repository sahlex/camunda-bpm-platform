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

import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.identity.Group;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * <p>Spring security authentication provider that authenticates against the 
 * process engine database</p>
 * 
 * @author Daniel Meyer
 *
 */
public class DbAuthenticationProvider implements AuthenticationProvider {
  
  protected String processEngineName;

  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    ProcessEngine processEngine = ProcessEngines.getProcessEngine(processEngineName);
    
    UsernamePasswordAuthenticationToken credentials = (UsernamePasswordAuthenticationToken) authentication.getCredentials();
    
    // check credentials
    boolean authSuccess = processEngine.getIdentityService()
      .checkPassword(credentials.getName(), (String) credentials.getCredentials());
    
    // erase password
    credentials.eraseCredentials();
    
    if(authSuccess) {
      // load the user's groups:
      loadAuthorities(processEngine, credentials);
      
      return credentials;
      
    } else {
      throw new BadCredentialsException("Could not validate credentials against user database.");
      
    }
    
  }

  protected void loadAuthorities(ProcessEngine processEngine, UsernamePasswordAuthenticationToken credentials) {
    List<Group> groups = processEngine.getIdentityService()
      .createGroupQuery()
      .groupMember(credentials.getName())
      .list();
    
    for (Group group : groups) {
      SimpleGrantedAuthority authority = new SimpleGrantedAuthority(group.getId());
      credentials.getAuthorities().add(authority);        
    }
  }

  public boolean supports(Class<?> authentication) {
    return authentication.getClass().isAssignableFrom(UsernamePasswordAuthenticationToken.class);
  }

}
