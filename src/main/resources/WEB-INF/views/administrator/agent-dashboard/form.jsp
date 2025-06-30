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
	<acme:print code="administrator.agent-dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
	<tr>
		<th scope="row">
			<acme:print code="administrator.agent-dashboard.form.label.average-number-tracking-logs-agent"/>
		</th>
		<td>
			<acme:print value="${averageLogs}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="administrator.agent-dashboard.form.label.deviation-number-tracking-logs-agent"/>
		</th>
		<td>
			<acme:print value="${deviationLogs}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="administrator.agent-dashboard.form.label.max-number-tracking-logs-agent"/>
		</th>
		<td>
			<acme:print value="${maximumLogs}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="administrator.agent-dashboard.form.label.min-number-tracking-logs-agent"/>
		</th>
		<td>
			<acme:print value="${minimumLogs}"/>
		</td>
	</tr>	
</table>

<h2>
	<acme:print code="administrator.agent-dashboard.form.title.application-statuses"/>
</h2>

<div>
	<canvas id="canvas"></canvas>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		var data = {
			labels : [
					"RESOLVED", "REJECTED"
			],
			datasets : [
				{
					data : [
						<jstl:out value="${ratioOfResolvedClaims}"/>, 
						<jstl:out value="${ratioOfRejectedClaims}"/>
					]
				}
			]
		};
		var options = {
			scales : {
				yAxes : [
					{
						ticks : {
							suggestedMin : 0.0,
							suggestedMax : 1.0
						}
					}
				]
			},
			legend : {
				display : false
			}
		};
	
		var canvas, context;
	
		canvas = document.getElementById("canvas");
		context = canvas.getContext("2d");
		new Chart(context, {
			type : "bar",
			data : data,
			options : options
		});
	});
</script>

<acme:return/>

