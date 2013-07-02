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
package org.camunda.bpm.engine.identity;

import org.camunda.bpm.engine.query.Query;

/**
 * @author Daniel Meyer
 *
 */
public interface AuthorizationQuery extends Query<AuthorizationQuery, Authorization> {

  /** only selects authorizations for the given id */
  AuthorizationQuery authorizationId(String id);
  
  /** only selects authorizations for the given user ids */
  AuthorizationQuery userIdIn(String... userIds);
  
  /** only selects authorizations for the given group ids */
  AuthorizationQuery groupIdIn(String... groupIds);
  
  /** only selects authorizations for the given resource type */
  AuthorizationQuery resourceType(String resourceType);
  
  /** only selects authorizations for the given resource id */
  AuthorizationQuery resourceId(String resourceId);
  
  /** only selects authorizations which grant the permissions represented by the parameter.
   * If this method is called multiple times, all passed-in permissions will be checked with AND semantics.
   * Example:
   * 
   * <pre>
   * authorizationQuery.userId("user1")
   *   .hasPermission(Authorization.PERMISSION_TYPE_READ)
   *   .hasPermission(Authorization.PERMISSION_TYPE_WRITE)
   *   .hasPermission(Authorization.PERMISSION_TYPE_DELETE)
   *   .list();
   * </pre>
   * 
   * Selects all Authorization objects which provide READ,WRITE,DELETE 
   * Permissions for the given user. 
   * 
   */
  AuthorizationQuery hasPermission(int permission);
    
}
