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
package org.camunda.bpm.common.auth.resource;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.camunda.bpm.common.auth.AuthenticatedPrincipal;
import org.camunda.bpm.common.auth.AuthenticationException;
import org.camunda.bpm.common.auth.AuthenticationService;
import org.camunda.bpm.common.auth.impl.UsernamePasswordAuthToken;

/**
 * <p>JAX-RS resource handling login and logout requests.
 * 
 * @author Daniel Meyer
 * 
 */
@Path("auth/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class AuthenticationResource {
  
  protected final static Logger LOGGER = Logger.getLogger(AuthenticationResource.class.getName()); 

  @Context
  private ServletContext servletContext;
  
  @POST
  @Path("login")
  public AuthenticationResponseDto login(AuthenticationRequestDto loginDto) {

    final AuthenticationService authenticationService = getAuthenticationService();

    // build auth token
    String username = loginDto.getUsername();
    String password = loginDto.getPassword();
    UsernamePasswordAuthToken token = new UsernamePasswordAuthToken(username, password);

    try {
      // perform authentication      
      authenticationService.authenticate(token);
      
      checkAuthentication(authenticationService.getCurrentPrincipal());

      return new AuthenticationResponseDto(username, true);

    } catch (AuthenticationException e) {
      
      // log silently.
      LOGGER.log(Level.INFO, "Authentication exception ", e);
      
      authenticationService.clearCurrentPrincipal();

      return new AuthenticationResponseDto(username, false);
    }
    
  }

  /** callback invoked after a successful authentication has been made 
   * @param authenticatedPrincipal 
   * @throws AuthenticationException */
  protected void checkAuthentication(AuthenticatedPrincipal authenticatedPrincipal) throws AuthenticationException {
    
  }

  /** application-specific method for providing the authentication service */
  protected abstract AuthenticationService getAuthenticationService();

  @GET
  @Path("logout")
  public String logout() {
    final AuthenticationService authenticationService = getAuthenticationService();
    
    authenticationService.clearCurrentPrincipal();
    
    return "logged out";
  }

  @GET
  @Path("user")
  @Produces(MediaType.APPLICATION_JSON)
  public String getCurrentUser() {
    
    AuthenticatedPrincipal currentPrincipal = getAuthenticationService().getCurrentPrincipal();
    
    return currentPrincipal.getName();
  }

}
