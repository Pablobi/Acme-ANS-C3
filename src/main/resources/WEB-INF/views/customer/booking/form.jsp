<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>	
	<acme:input-textbox code="customer.booking.form.label.locatorCode" path="locatorCode" placeholder="12345678 / ABCDEFG"/>
	<acme:input-select code="customer.booking.form.label.travelClass" path="travelClass" choices="${classes}"/>
	<acme:input-integer code="customer.booking.form.label.lastCreditCardDigits" path="lastCreditCardDigits" placeholder="1234"/>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == false}">
			<acme:input-select code="customer.booking.form.label.flight" path="flight" choices="${flights}" readonly="true"/>
			<acme:input-money code="customer.booking.form.label.price" path="price" readonly="true"/>
			<acme:input-moment code="customer.booking.form.label.purchaseMoment" path="purchaseMoment" readonly="true"/>
			<acme:button code="customer.booking.form.button.passengers" action="/customer/passenger/listFromBooking?masterId=${id}"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
		<acme:input-select code="customer.booking.form.label.flight" path="flight" choices="${flights}"/>
			<acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
			<acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
			<acme:submit code="customer.booking.form.button.delete" action="/customer/booking/delete"/>
			<acme:button code="customer.booking.form.button.passengers" action="/customer/passenger/listFromBooking?masterId=${id}"/>
			<acme:button code="customer.booking.form.button.passengers.add" action="/customer/passenger-record/create?masterId=${id}"/>
			<acme:button code="customer.booking.form.button.passengers.remove" action="/customer/passenger-record/delete?masterId=${id}"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code="customer.booking.form.label.flight" path="flight" choices="${flights}"/>
			<acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
		</jstl:when>
	</jstl:choose>			
</acme:form>