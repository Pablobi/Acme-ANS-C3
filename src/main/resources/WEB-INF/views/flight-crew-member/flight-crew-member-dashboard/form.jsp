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

<h2>
	<acme:print code="flight-crew-member.dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.last-five-destinations"/>
		</th>
		<td>
			<acme:print value="${lastFiveDestinations}"/>
		</td>
	</tr>
	
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.legs-severity-level-0-3"/>
		</th>
		<td>
			<acme:print value="${legsSeverityLevel03}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.legs-severity-level-4-7"/>
		</th>
		<td>
			<acme:print value="${legsSeverityLevel47}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.legs-severity-level-8-10"/>
		</th>
		<td>
			<acme:print value="${legsSeverityLevel810}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.crew-members-last-leg"/>
		</th>
		<td>
			<acme:print value="${crewMembersLastLeg}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.confirmed-flight-assignments"/>
		</th>
		<td>
			<acme:print value="${confirmedFlightAssignments}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.pending-flight-assignments"/>
		</th>
		<td>
			<acme:print value="${pendingFlightAssignments}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.cancelled-flight-assignments"/>
		</th>
		<td>
			<acme:print value="${cancelledFlightAssignments}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.average-last-month-assignments"/>
		</th>
		<td>
			<acme:print value="${averageLast10MonthsAssignments}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.minimum-last-month-assignments"/>
		</th>
		<td>
			<acme:print value="${minimumLast10MonthsAssignments}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.maximum-last-month-assignments"/>
		</th>
		<td>
			<acme:print value="${maximumLast10MonthsAssignments}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="flight-crew-member.dashboard.form.label.standard-deviation-last-month-assignments"/>
		</th>
		<td>
			<acme:print value="${standardDeviationLast10MonthsAssignments}"/>
		</td>
	</tr>
</table>

<acme:return/>