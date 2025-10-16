<%--
- menu.jsp
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
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-alevarmun1" action="https://www.kawasaki.es/es/products"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-pablobi" action="https://www.youtube.com/watch?v=stgdmw8PcRY"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-javclamar" action="https://www.chess.com/home"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-AngSanRui" action="https://matias.me/nsfw/"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link-RubLopExp" action="https://www.youtube.com/watch?v=5OVYmXVZ5ok&ab_channel=RecapGaming"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-airports" action="/administrator/airport/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-aircrafts" action="/administrator/aircraft/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-airlines" action="/administrator/airline/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-claims" action="/administrator/claim/list"/>
			<acme:menu-separator/>	
			<acme:menu-suboption code="master.menu.administrator.show-agent-dashboard" action="/administrator/agent-dashboard/show" />
			<acme:menu-separator />
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.technician.list-my-maintenance-records" action="/technician/maintenance-record/list"/>	
			<acme:menu-suboption code="master.menu.technician.list-my-tasks" action="/technician/task/list"/>			
		</acme:menu-option>
			
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">
			<acme:menu-suboption code="master.menu.customer.list-bookings" action="/customer/booking/list"/>
			<acme:menu-suboption code="master.menu.customer.list-passenger" action="/customer/passenger/list"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.flightCrewMember" access="hasRealm('FlightCrewMember')">
			<acme:menu-suboption code="master.menu.flightCrewMember.list-completedFlightAssignments" action="/flight-crew-member/flight-assignment/list-completed"/>
			<acme:menu-suboption code="master.menu.flightCrewMember.list-uncompletedFlightAssignments" action="/flight-crew-member/flight-assignment/list-uncompleted"/>
			<acme:menu-suboption code="master.menu.flightCrewMember.show-dashboard" action="/flight-crew-member/flight-crew-member-dashboard/show" />
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.manager" access="hasRealm('Manager')">
			<acme:menu-suboption code="master.menu.manager.list-flights" action="/manager/flight/list"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.agent" access="hasRealm('Agent')">
			<acme:menu-suboption code="master.menu.agent.list-completed" action="/agent/claim/list-completed"/>
			<acme:menu-suboption code="master.menu.agent.list-pending" action="/agent/claim/list-pending"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.authenticated" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.authenticated.list-flight-assignments" action="/authenticated/flight-assignment/list" />
		</acme:menu-option>

	</acme:menu-left>

	<acme:menu-right>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-manager" action="/authenticated/manager/create" access="!hasRealm('Manager')"/>
			<acme:menu-suboption code="master.menu.user-account.manager-profile" action="/authenticated/manager/update" access="hasRealm('Manager')"/>
			<acme:menu-suboption code="master.menu.user-account.become-agent" action="/authenticated/agent/create" access="!hasRealm('Agent')"/>
			<acme:menu-suboption code="master.menu.user-account.agent-profile" action="/authenticated/agent/update" access="hasRealm('Agent')"/>
			<acme:menu-suboption code="master.menu.user-account.become-flight-crew-member" action="/authenticated/flight-crew-member/create" access="!hasRealm('FlightCrewMember')"/>
			<acme:menu-suboption code="master.menu.user-account.flight-crew-member-profile" action="/authenticated/flight-crew-member/update" access="hasRealm('FlightCrewMember')"/>
		</acme:menu-option>
	</acme:menu-right>
</acme:menu-bar>

