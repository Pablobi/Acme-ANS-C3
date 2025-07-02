<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.aircraft.list.label.registrationNumber" path="registrationNumber" width="15%"/>
	<acme:list-column code="administrator.aircraft.list.label.capacity" path="capacity" width="15%"/>
	<acme:list-column code="administrator.aircraft.list.label.status" path="status" width="15%"/>
	<acme:list-column code="administrator.aircraft.list.label.airline" path="airline" width="15%"/>
</acme:list>

<acme:button code="administrator.aircraft.list.button.create" action="/administrator/aircraft/create"/>