<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.passenger.list.label.name" path="name" width="33%"/>
	<acme:list-column code="customer.passenger.list.label.email" path="email" width="33%"/>
	<acme:list-column code="customer.passenger.list.label.passport" path="passport" width="33%"/>
</acme:list>
<jstl:choose>
	<jstl:when test="${_command == 'list'}">
		<acme:button code="customer.passenger.list.button.create" action="/customer/passenger/create"/>
	</jstl:when>
</jstl:choose>
