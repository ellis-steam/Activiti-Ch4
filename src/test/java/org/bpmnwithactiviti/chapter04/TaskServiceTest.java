package org.bpmnwithactiviti.chapter04;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

public class TaskServiceTest {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem.xml");
	
	private void startProcessInstance(){
		
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("isbn", "123456");
		runtimeService.startProcessInstanceByKey("bookorder", variableMap);
	}
	
	@Test
	@Deployment(resources={"chapter04/bookorder.bpmn20.xml"})
	public void queryTask(){
		startProcessInstance();
		TaskService taskService = activitiRule.getTaskService();
		Task task = taskService.createTaskQuery().taskCandidateGroup("sales").singleResult();
		assertEquals("Complete order", task.getName());
		System.out.println("task id " + task.getId() + ", name " + task.getName() + ", def key " + task.getTaskDefinitionKey());
	}
	
	@Test
	public void createTask(){
		TaskService taskService = activitiRule.getTaskService();
		Task task = taskService.newTask();
		task.setName("Test task");
		task.setPriority(100);
		taskService.saveTask(task);
		assertNull(task.getAssignee());
		
		IdentityService identityService = activitiRule.getIdentityService();
		User user = identityService.newUser("JohnDoe");
		identityService.saveUser(user);
		
		taskService.addCandidateUser(task.getId(), "JohnDoe");
		task = taskService.createTaskQuery().taskCandidateUser("JohnDoe").singleResult();
		assertNotNull(task);
		assertEquals("Test task", task.getName());
		assertNull(task.getAssignee());
		
		taskService.claim(task.getId(), "JohnDoe");
		task = taskService.createTaskQuery().taskAssignee("JohnDoe").singleResult();
		assertEquals("JohnDoe", task.getAssignee());
		
		taskService.complete(task.getId());
		task = taskService.createTaskQuery().taskAssignee("JohnDoe").singleResult();
		assertNull(task);
		
	}
	
}
