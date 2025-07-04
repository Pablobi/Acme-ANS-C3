<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="agent.tracking-log.form.label.step" path="step"/>
	<acme:input-moment code="agent.tracking-log.form.label.updateMoment" path="updateMoment" readonly="true"/>
	<jstl:if test="${isLastOne == false}">
		<acme:input-double code="agent.tracking-log.form.label.percentage" path="percentage"/>
		<acme:input-select code="agent.tracking-log.form.label.status" path="status"  choices="${statuses}"/>
	</jstl:if>	
	<jstl:if test="${isLastOne == true}">
		<acme:input-double code="agent.tracking-log.form.label.percentage" path="percentage" readonly="true"/>
		<acme:input-select code="agent.tracking-log.form.label.status" path="status"  choices="${statuses}" readonly="true"/>	
	</jstl:if>	
	<acme:input-textarea code="agent.tracking-log.form.label.resolution" path="resolution"/>
	
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true && draftModeMaster == false}">
			<acme:submit code="agent.tracking-log.form.button.update" action="/agent/tracking-log/update"/>
			<acme:submit code="agent.tracking-log.form.button.delete" action="/agent/tracking-log/delete"/>
			<acme:submit code="agent.tracking-log.form.button.publish" action="/agent/tracking-log/publish"/>
		</jstl:when>
				<jstl:when test="${acme:anyOf(_command, 'show|update|delete') && draftMode == true && draftModeMaster == true}">
			<acme:submit code="agent.tracking-log.form.button.update" action="/agent/tracking-log/update"/>
			<acme:submit code="agent.tracking-log.form.button.delete" action="/agent/tracking-log/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="agent.tracking-log.form.button.create" action="/agent/tracking-log/create?masterId=${masterId}"/>
		</jstl:when>		
	</jstl:choose>		
</acme:form>

