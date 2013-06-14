/**
 * <p>Package containing classes used for authorization and authentication 
 * in camunda BPM webapplications. The aim of this package is to provide 
 * a thin layer of abstraction over different authentication and authorization 
 * systems (such as Spring Security).
 * 
 * <h2>Authentication:</h2>
 * In order to authenticate a user, the {@link org.camunda.bpm.common.auth.AuthenticationService} can be used.
 * The authentication service provides access to the {@link org.camunda.bpm.common.auth.AuthenticatedPrincipal} which 
 * represents the currently authenticated user.
 * 
 * <h2>Authorization</h2>
 * Upon authentication, the {@link org.camunda.bpm.common.auth.AuthenticatedPrincipal} is assigned a set of 
 * {@link org.camunda.bpm.common.auth.Authority Authoritites}. The Enum {@link org.camunda.bpm.common.auth.CamundaAuthority}
 * contains a set of built-in authorites (such as "Administrator").
 * 
 */
package org.camunda.bpm.common.auth;