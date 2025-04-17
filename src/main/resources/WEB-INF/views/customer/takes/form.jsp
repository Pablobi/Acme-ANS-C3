<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	<acme:input-select code="customer.takes.form.passenger" path="passenger" choices="${passengers}"/>
	<acme:input-textbox code="customer.takes.form.booking" path="locatorCode" readonly="true"/>
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.takes.form.button.create" action="/customer/takes/create"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>
