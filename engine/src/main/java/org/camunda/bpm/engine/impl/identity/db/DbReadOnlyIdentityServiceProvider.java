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
package org.camunda.bpm.engine.impl.identity.db;

import java.util.List;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.AbstractManager;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;

/**
 * <p>Read only implementation of DB-backed identity service</p>
 * 
 * @author Daniel Meyer
 *
 */
@SuppressWarnings("unchecked")
public class DbReadOnlyIdentityServiceProvider extends AbstractManager implements ReadOnlyIdentityProvider {

  // users /////////////////////////////////////////

  public UserEntity findUserById(String userId) {
    return getDbSqlSession().selectById(UserEntity.class, userId);
  }

  public UserQuery createUserQuery() {
    return new DbUserQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
  }
  
  public UserQueryImpl createUserQuery(CommandContext commandContext) {
    return new DbUserQueryImpl(commandContext);
  }
  
  public long findUserCountByQueryCriteria(DbUserQueryImpl query) {
    return (Long) getDbSqlSession().selectOne("selectUserCountByQueryCriteria", query);
  }
  
  public List<User> findUserByQueryCriteria(DbUserQueryImpl query) {
    return getDbSqlSession().selectList("selectUserByQueryCriteria", query);
  }

  public Boolean checkPassword(String userId, String password) {
    User user = findUserById(userId);
    if ((user != null) && (password != null) && (password.equals(user.getPassword()))) {
      return true;
    } else {
      return false;
    }
  }
  
  // groups //////////////////////////////////////////

  public GroupEntity findGroupById(String groupId) {
    return getDbSqlSession().selectById(GroupEntity.class, groupId);
  }

  public GroupQuery createGroupQuery() {
    return new DbGroupQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
  }
  
  public GroupQuery createGroupQuery(CommandContext commandContext) {
    return new DbGroupQueryImpl(commandContext);
  }
  
  public long findGroupCountByQueryCriteria(DbGroupQueryImpl query) {
    return (Long) getDbSqlSession().selectOne("selectGroupCountByQueryCriteria", query);
  }
  
  public List<Group> findGroupByQueryCriteria(DbGroupQueryImpl query) {
    return getDbSqlSession().selectList("selectGroupByQueryCriteria", query);
  }
  
}
