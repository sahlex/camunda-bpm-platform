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
package org.camunda.bpm.cockpit.impl.web.auth.impl.spring;

import org.camunda.bpm.cockpit.auth.AuthenticatedPrincipal;
import org.camunda.bpm.cockpit.auth.AuthenticationException;
import org.camunda.bpm.cockpit.auth.AuthenticationProvider;
import org.camunda.bpm.cockpit.auth.AuthenticationService;
import org.camunda.bpm.cockpit.auth.AuthenticationToken;
import org.camunda.bpm.cockpit.impl.auth.UsernamePasswordAuthToken;
import org.camunda.bpm.cockpit.impl.web.auth.impl.unsafe.SslUnsafeTrustManagerHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * <p>{@link AuthenticationProvider} implementation backed by the SpringSecurity Framework.</p> 
 * 
 * <p>This implementation is meant to be initialized from a Spring Application context.</p>
 *   
 * @author Daniel Meyer
 *
 */
public class SpringSecurityAuthenticationService implements AuthenticationService, InitializingBean {
  
  /** a reference to the spring security authentication manager **/
  protected AuthenticationManager authenticationManager;
  
  /** indicated whether this authentication provider 
   * should accept all SSL certificates (even unverified, self-signed ones.). **/
  protected boolean isAcceptAllSslCertificates = false;
  
  public AuthenticatedPrincipal authenticate(AuthenticationToken token) throws AuthenticationException {
    
    String username = null;
    String password = null;
    
    if (token instanceof UsernamePasswordAuthToken) {      
      UsernamePasswordAuthToken authToken = (UsernamePasswordAuthToken) token;
      username = authToken.getUsername();
      password = authToken.getPassword();
    } else {
      throw new AuthenticationException("This authentication provider implementation does not support AuthenticationToken of type '"+token);
    }

    // build auth token
    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
    
    try {
      // perform authentication
      Authentication authResult = authenticationManager.authenticate(authRequest);

      // set authentication to spring security context
      SecurityContextHolder.getContext().setAuthentication(authResult);

      return getCurrentPrincipal();

    } catch (org.springframework.security.core.AuthenticationException e) {

      // cleanup
      SecurityContextHolder.clearContext();

      throw new AuthenticationException("Could not authenticate", e);
    }
    
  }

  public void afterPropertiesSet() throws Exception {
    
    if(isAcceptAllSslCertificates) {
      SslUnsafeTrustManagerHelper.accepptAllSSLCertificates();
    }
    
  }
    
  public AuthenticatedPrincipal getCurrentPrincipal() {
    return new SpringAuthenticatedPrincipal(SecurityContextHolder.getContext().getAuthentication());
  }
  
  public void clearCurrentPrincipal() {
    SecurityContextHolder.clearContext();
  }
  
  // getters / setters ////////////////////////////////////////////
  
  public AuthenticationManager getAuthenticationManager() {
    return authenticationManager;
  }
  
  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }
  
  public boolean isAcceptAllSslCertificates() {
    return isAcceptAllSslCertificates;
  }

  public void setAcceptAllSslCertificates(boolean isAcceptAllSslCertificates) {
    this.isAcceptAllSslCertificates = isAcceptAllSslCertificates;
  }

}
