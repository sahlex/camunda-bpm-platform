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
package org.camunda.bpm.identity.impl.ldap;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.SortControl;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.AbstractQuery;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.UserQueryProperty;
import org.camunda.bpm.engine.impl.identity.IdentityProviderException;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;

/**
 * <p>LDAP {@link ReadOnlyIdentityProvider}.</p>
 * 
 * @author Daniel Meyer
 *
 */
public class LdapIdentityProvider implements ReadOnlyIdentityProvider {
  
  private final static Logger LOG = Logger.getLogger(LdapIdentityProvider.class.getName());
  
  protected LdapConfiguration ldapConfiguration;  
  protected LdapContext initialContext;

  public LdapIdentityProvider(LdapConfiguration ldapConfiguration) {
    this.ldapConfiguration = ldapConfiguration;
  }
  
  // Session Lifecycle //////////////////////////////////

  public void flush() {
    // nothing to do
  }

  public void close() {
    if (initialContext != null) {
      try {
        initialContext.close();
      } catch (Exception e) {
        // ignore
        LOG.log(Level.FINE, "exception while closing LDAP DIR CTX", e);
      }
    }
  }
  
  protected void open() {
    Hashtable<String, String> env = new Hashtable<String, String>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, ldapConfiguration.getInitialContextFactory());
    env.put(Context.SECURITY_AUTHENTICATION, ldapConfiguration.getSecurityAuthentication());
    env.put(Context.PROVIDER_URL, ldapConfiguration.getServerUrl());
    env.put(Context.SECURITY_PRINCIPAL, ldapConfiguration.getManagerDn());
    env.put(Context.SECURITY_CREDENTIALS, ldapConfiguration.getManagerPassword());

    // add additional properties
    Map<String, String> contextProperties = ldapConfiguration.getContextProperties();
    if(contextProperties != null) {
      env.putAll(contextProperties);
    }

    try {
      initialContext = new InitialLdapContext(env, null);
    } catch(Exception e) {
      throw new IdentityProviderException("Could not connect to LDAP server", e);
    }
  }
  
  protected void ensureContextInitialized() {
    if(initialContext == null) {
      open();
    }
  }
  
  // Users /////////////////////////////////////////////////

  public User findUserById(String userId) {
    ensureContextInitialized();
    
    return createUserQuery(org.camunda.bpm.engine.impl.context.Context.getCommandContext())
      .userId(userId)
      .singleResult();
  }

  public UserQuery createUserQuery() {
    return new LdapUserQueryImpl(org.camunda.bpm.engine.impl.context.Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
  }

  public UserQueryImpl createUserQuery(CommandContext commandContext) {
    return new LdapUserQueryImpl(commandContext);
  }
  
  public long findUserCountByQueryCriteria(LdapUserQueryImpl query) {
    ensureContextInitialized();
    return findUserByQueryCriteria(query).size();
  }
  
  public List<User> findUserByQueryCriteria(LdapUserQueryImpl query) {
    ensureContextInitialized();

    String userBaseDn = ldapConfiguration.getUserSearchBase() + ldapConfiguration.getBaseDn();

    if(ldapConfiguration.isSortControlSupported()) {
      applyRequestControls(query);
    }
    
    try {
      
      String filter = getUserSearchFilter(query);
      NamingEnumeration<SearchResult> enumeration = initialContext.search(userBaseDn, filter, ldapConfiguration.getSearchControls());
      
      // perform client-side paging
      int resultCount = 0;
      List<User> userList = new ArrayList<User>();
      while (enumeration.hasMoreElements() && userList.size() < query.getMaxResults()) {
        SearchResult result = (SearchResult) enumeration.nextElement();
        
        if(resultCount >= query.getFirstResult()) {
          UserEntity user = transformUser(result);         
          userList.add(user);
        }
        
        resultCount ++;
      }
      
      return userList;
      
    } catch (NamingException e) {
      throw new IdentityProviderException("Could not query for users", e);
    }
  }
  
  public Boolean checkPassword(String userId, String password) {
    ensureContextInitialized();
    return null;
  }
  
  protected String getUserSearchFilter(LdapUserQueryImpl query) {

    StringWriter search = new StringWriter();
    search.write("(&"); 
    
    // restrict to users
    search.write(ldapConfiguration.getUserSearchFilter());
    
    // add additional filters from query
    if(query.getId() != null) {
      addFilter(ldapConfiguration.getUserIdAttribute(), query.getId(), search);
    }    
    if(query.getEmail() != null) {
      addFilter(ldapConfiguration.getUserEmailAttribute(), query.getEmail(), search);
    }
    if(query.getEmailLike() != null) {
      addFilter(ldapConfiguration.getUserEmailAttribute(), query.getEmailLike(), search);
    }    
    if(query.getFirstName() != null) {
      addFilter(ldapConfiguration.getUserFirstnameAttribute(), query.getFirstName(), search);
    }
    if(query.getFirstNameLike() != null) {
      addFilter(ldapConfiguration.getUserFirstnameAttribute(), query.getFirstNameLike(), search);
    }    
    if(query.getLastName() != null) {
      addFilter(ldapConfiguration.getUserLastnameAttribute(), query.getLastName(), search);
    }
    if(query.getLastNameLike() != null) {
      addFilter(ldapConfiguration.getUserLastnameAttribute(), query.getLastNameLike(), search);
    }    
    if(query.getGroupId() != null) {
      addFilter(ldapConfiguration.getGroupMemberAttribute(), getDnForGroup(query.getGroupId()), search);
    }
   
    search.write(")");
    
    return search.toString();
  }
  
  // Groups ///////////////////////////////////////////////

  public Group findGroupById(String groupId) {
    ensureContextInitialized();    
    return createGroupQuery(org.camunda.bpm.engine.impl.context.Context.getCommandContext())
      .groupId(groupId)
      .singleResult();
  }

  public GroupQuery createGroupQuery() {
    return new LdapGroupQuery(org.camunda.bpm.engine.impl.context.Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
  }

  public GroupQuery createGroupQuery(CommandContext commandContext) {
    return new LdapGroupQuery(commandContext);
  }

  public long findGroupCountByQueryCriteria(LdapGroupQuery ldapGroupQuery) {
    ensureContextInitialized();
    return findGroupByQueryCriteria(ldapGroupQuery).size();
  }

  public List<Group> findGroupByQueryCriteria(LdapGroupQuery query) {
    ensureContextInitialized();

    String groupBaseDn = ldapConfiguration.getGroupSearchBase() + ldapConfiguration.getBaseDn();

    if(ldapConfiguration.isSortControlSupported()) {
      applyRequestControls(query);
    }
    
    try {
      
      String filter = getGroupSearchFilter(query);
      NamingEnumeration<SearchResult> enumeration = initialContext.search(groupBaseDn, filter, ldapConfiguration.getSearchControls());
      
      // perform client-side paging
      int resultCount = 0;
      List<Group> groupList = new ArrayList<Group>();
      while (enumeration.hasMoreElements() && groupList.size() < query.getMaxResults()) {
        SearchResult result = (SearchResult) enumeration.nextElement();
        
        if(resultCount >= query.getFirstResult()) {
          GroupEntity group = transformGroup(result);          
          groupList.add(group);
        }
        
        resultCount ++;
      }
      
      return groupList;
      
    } catch (NamingException e) {
      throw new IdentityProviderException("Could not query for users", e);
    }
  }
  
  protected String getGroupSearchFilter(LdapGroupQuery query) {

    StringWriter search = new StringWriter();
    search.write("(&"); 
    
    // restrict to groups
    search.write(ldapConfiguration.getGroupSearchFilter());
    
    // add additional filters from query
    if(query.getId() != null) {
      addFilter(ldapConfiguration.getGroupIdAttribute(), query.getId(), search);
    }
    if(query.getName() != null) {
      addFilter(ldapConfiguration.getGroupNameAttribute(), query.getName(), search);
    }    
    if(query.getNameLike() != null) {
      addFilter(ldapConfiguration.getGroupNameAttribute(), query.getNameLike(), search);
    } 
    if(query.getUserId() != null) {    
      addFilter(ldapConfiguration.getGroupMemberAttribute(), getDnForUser(query.getUserId()), search);
    }
    search.write(")");
    
    return search.toString();
  }

  // Utils ////////////////////////////////////////////

  protected String getDnForUser(String userId) {
    LdapUserEntity user = (LdapUserEntity) createUserQuery(org.camunda.bpm.engine.impl.context.Context.getCommandContext())
      .userId(userId)
      .singleResult();
    if(user == null) {
      return "";
    } else {
      return user.getDn();
    }
  }
  
  protected String getDnForGroup(String groupId) {
    LdapGroupEntity group = (LdapGroupEntity) createGroupQuery(org.camunda.bpm.engine.impl.context.Context.getCommandContext())
      .groupId(groupId)
      .singleResult();
    if(group == null) {
      return "";
    } else {
      return group.getDn();
    }
  }
  
  protected String getStringAttributeValue(String attrName, Attributes attributes) throws NamingException {
    Attribute attribute = attributes.get(attrName);
    if(attribute != null){
      return (String) attribute.get();      
    } else {
      return null;
    }
  }

  protected void addFilter(String attributeName, String attributeValue, StringWriter writer) {
    writer.write("(");
    writer.write(attributeName);
    writer.write("=");
    writer.write(attributeValue);
    writer.write(")");
  }
  
  protected LdapUserEntity transformUser(SearchResult result) throws NamingException {    
    final Attributes attributes = result.getAttributes();    
    LdapUserEntity user = new LdapUserEntity();
    user.setDn(result.getNameInNamespace());    
    user.setId(getStringAttributeValue(ldapConfiguration.getUserIdAttribute(), attributes));
    user.setFirstName(getStringAttributeValue(ldapConfiguration.getUserFirstnameAttribute(), attributes));
    user.setLastName(getStringAttributeValue(ldapConfiguration.getUserLastnameAttribute(), attributes));
    user.setEmail(getStringAttributeValue(ldapConfiguration.getUserEmailAttribute(), attributes));
    return user;
  }
  
  protected GroupEntity transformGroup(SearchResult result) throws NamingException {
    final Attributes attributes = result.getAttributes();    
    LdapGroupEntity group = new LdapGroupEntity();
    group.setDn(result.getNameInNamespace());
    group.setId(getStringAttributeValue(ldapConfiguration.getGroupIdAttribute(), attributes));
    group.setName(getStringAttributeValue(ldapConfiguration.getGroupNameAttribute(), attributes));
    return group;
  }
  
  @SuppressWarnings("rawtypes")
  protected void applyRequestControls(AbstractQuery query) {

    try {
      List<Control> controls = new ArrayList<Control>();
      
      String orderBy = query.getOrderBy();
      if(orderBy != null) {
        orderBy = orderBy.substring(0, orderBy.length()-4);
        if(UserQueryProperty.USER_ID.getName().equals(orderBy)) {
          controls.add(new SortControl(ldapConfiguration.getUserIdAttribute(), Control.CRITICAL));
          
        } else if(UserQueryProperty.EMAIL.getName().equals(orderBy)) {
          controls.add(new SortControl(ldapConfiguration.getUserEmailAttribute(), Control.CRITICAL));
          
        } else if(UserQueryProperty.FIRST_NAME.getName().equals(orderBy)) {
          controls.add(new SortControl(ldapConfiguration.getUserFirstnameAttribute(), Control.CRITICAL));
          
        } else if(UserQueryProperty.LAST_NAME.getName().equals(orderBy)) {
          controls.add(new SortControl(ldapConfiguration.getUserLastnameAttribute(), Control.CRITICAL));
        } 
      }

      initialContext.setRequestControls(controls.toArray(new Control[0]));
      
    } catch (Exception e) {
      throw new IdentityProviderException("Execption while setting paging settings", e);
    }
  }
  
}
