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

<acme:form readonly="${readonly}">
	<acme:input-moment code="agent.claim.form.label.moment" path="moment"/>
	<acme:input-select code="agent.claim.form.label.type" path="type" choices="${types}"/>	
	<acme:input-textbox code="agent.claim.form.label.email" path="email"/>
	<acme:input-textarea code="agent.claim.form.label.description" path="description"/>
	<acme:input-select code="agent.claim.form.label.leg" path="leg" choices="${legs}"/>
	<acme:input-textbox code="agent.claim.form.label.status" path="status"/>	

	
</acme:form>
