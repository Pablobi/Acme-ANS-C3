<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.maintenance-record.list.label.maintenanceMoment" path="maintenanceMoment" width="35%"/>
	<acme:list-column code="technician.maintenance-record.list.label.maintenanceStatus" path="maintenanceStatus" width="30%"/>
	<acme:list-column code="technician.maintenance-record.list.label.nextInspectionDate" path="nextInspectionDate" width="35%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<acme:button code="technician.maintenance-record.list.button.create" action="/technician/maintenance-record/create"/>