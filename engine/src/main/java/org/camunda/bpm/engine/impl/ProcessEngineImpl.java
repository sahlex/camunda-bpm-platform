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
package org.camunda.bpm.engine.impl;

import java.util.Map;
import java.util.logging.Logger;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.TransactionContextFactory;
import org.camunda.bpm.engine.impl.db.DbSqlSession;
import org.camunda.bpm.engine.impl.db.DbSqlSessionFactory;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;

/**
 * @author Tom Baeyens
 */
public class ProcessEngineImpl implements ProcessEngine {

  private static Logger log = Logger.getLogger(ProcessEngineImpl.class.getName());

  protected String name;
  protected RepositoryService repositoryService;
  protected RuntimeService runtimeService;
  protected HistoryService historicDataService;
  protected IdentityService identityService;
  protected TaskService taskService;
  protected FormService formService;
  protected ManagementService managementService;
  protected String databaseSchemaUpdate;
  protected JobExecutor jobExecutor;
  protected CommandExecutor commandExecutor;
  protected CommandExecutor commandExecutorSchemaOperations;
  protected Map<Class<?>, SessionFactory> sessionFactories;
  protected ExpressionManager expressionManager;
  protected int historyLevel;
  protected TransactionContextFactory transactionContextFactory;
  protected ProcessEngineConfigurationImpl processEngineConfiguration;


  public ProcessEngineImpl(ProcessEngineConfigurationImpl processEngineConfiguration) {
    this.processEngineConfiguration = processEngineConfiguration;
    this.name = processEngineConfiguration.getProcessEngineName();
    this.repositoryService = processEngineConfiguration.getRepositoryService();
    this.runtimeService = processEngineConfiguration.getRuntimeService();
    this.historicDataService = processEngineConfiguration.getHistoryService();
    this.identityService = processEngineConfiguration.getIdentityService();
    this.taskService = processEngineConfiguration.getTaskService();
    this.formService = processEngineConfiguration.getFormService();
    this.managementService = processEngineConfiguration.getManagementService();
    this.databaseSchemaUpdate = processEngineConfiguration.getDatabaseSchemaUpdate();
    this.jobExecutor = processEngineConfiguration.getJobExecutor();
    this.commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();
    commandExecutorSchemaOperations = processEngineConfiguration.getCommandExecutorSchemaOperations();
    this.sessionFactories = processEngineConfiguration.getSessionFactories();
    this.historyLevel = processEngineConfiguration.getHistoryLevel();
    this.transactionContextFactory = processEngineConfiguration.getTransactionContextFactory();
    
    executeSchemaOperations();

    if (name == null) {
      log.info("default activiti ProcessEngine created");
    } else {
      log.info("ProcessEngine " + name + " created");
    }
    
    ProcessEngines.registerProcessEngine(this);
    
    if ((jobExecutor != null)) {      
      // register process engine with Job Executor
      jobExecutor.registerProcessEngine(this);      
    }
  }
  
  protected void executeSchemaOperations() {
    commandExecutorSchemaOperations.execute(new SchemaOperationsProcessEngineBuild());
  }
  
  public void close() {
    
    ProcessEngines.unregister(this);
    
    if ((jobExecutor != null)) {      
      // unregister process engine with Job Executor
      jobExecutor.unregisterProcessEngine(this);      
    }

    commandExecutorSchemaOperations.execute(new SchemaOperationProcessEngineClose());
  }

  public DbSqlSessionFactory getDbSqlSessionFactory() {
    return (DbSqlSessionFactory) sessionFactories.get(DbSqlSession.class);
  }
  
  // getters and setters //////////////////////////////////////////////////////

  public String getName() {
    return name;
  }

  public IdentityService getIdentityService() {
    return identityService;
  }

  public ManagementService getManagementService() {
    return managementService;
  }

  public TaskService getTaskService() {
    return taskService;
  }

  public HistoryService getHistoryService() {
    return historicDataService;
  }

  public RuntimeService getRuntimeService() {
    return runtimeService;
  }
  
  public RepositoryService getRepositoryService() {
    return repositoryService;
  }
  
  public FormService getFormService() {
    return formService;
  }

  public ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
    return processEngineConfiguration;
  }
}
