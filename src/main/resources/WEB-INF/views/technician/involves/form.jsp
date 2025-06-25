<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	 <jstl:choose>
 		<jstl:when test="${_command == 'create'}">
	 		<acme:input-select code="technician.involves.form.label.task" path="task" choices="${task}"/>
 			<acme:submit code="technician.involves.form.button.create" action="/technician/involves/create?maintenanceRecordId=${maintenanceRecordId}"/>
 		</jstl:when>
 		<jstl:when test="${_command == 'delete'}">
	 		<acme:input-select code="technician.involves.form.label.task" path="task" choices="${task}"/>
 			<acme:submit code="technician.involves.form.button.delete" action="/technician/involves/delete?maintenanceRecordId=${maintenanceRecordId}"/>
 		</jstl:when>
 		<jstl:when test="${_command == 'show'}">
	 		<acme:input-select code="technician.task.form.label.taskType" path="task.taskType" choices="${types}" readonly="true"/>
	 		<acme:input-textbox code="technician.task.form.label.description" path="task.description" readonly="true"/>
	 		<acme:input-integer code="technician.task.form.label.priority" path="task.priority" readonly="true"/>
	 		<acme:input-integer code="technician.task.form.label.estimatedDuration" path="task.estimatedDuration" readonly="true"/>	
 		</jstl:when>
 	</jstl:choose>
</acme:form>