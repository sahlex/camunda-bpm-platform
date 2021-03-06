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
package org.camunda.bpm.engine.rest.dto.runtime;

import org.camunda.bpm.engine.runtime.VariableInstance;

/**
 * @author roman.smirnov
 */
public class VariableInstanceDto {
  
  private String name;
  private String type;
  private Object value;
  private String processInstanceId;
  private String executionId;
  private String taskId;
  private String activityInstanceId;
  
  public VariableInstanceDto() { }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getExecutionId() {
    return executionId;
  }

  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getActivityInstanceId() {
    return activityInstanceId;
  }

  public void setActivityInstanceId(String activityInstanceId) {
    this.activityInstanceId = activityInstanceId;
  }
  
  public static VariableInstanceDto fromVariableInstance(VariableInstance variableInstance) {
    VariableInstanceDto dto = new VariableInstanceDto();
    
    dto.setName(variableInstance.getName());
    dto.setType(variableInstance.getTypeName());
    dto.setValue(variableInstance.getValue());
    dto.setProcessInstanceId(variableInstance.getProcessInstanceId());
    dto.setExecutionId(variableInstance.getExecutionId());
    dto.setTaskId(variableInstance.getTaskId());
    dto.setActivityInstanceId(variableInstance.getActivityInstanceId());
    
    return dto;
  }
  
}
