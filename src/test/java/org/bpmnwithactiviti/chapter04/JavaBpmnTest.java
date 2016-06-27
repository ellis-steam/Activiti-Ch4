package org.bpmnwithactiviti.chapter04;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

public class JavaBpmnTest {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem.xml");
	
	private ProcessInstance startProcessInstance(){
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("isbn", 123456L);
		return runtimeService.startProcessInstanceByKey("bookorder", variableMap);
	}
	
	@Test
	@Deployment(resources = {"chapter04/bookorder.java.bpmn20.xml"})
	public void executeJavaService(){
		ProcessInstance processInstance = startProcessInstance();
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Date validateTime = (Date) runtimeService.getVariable(processInstance.getId(), "validatetime");
		assertNotNull(validateTime);
		System.out.println("validatetime is " + validateTime);
	}
}
