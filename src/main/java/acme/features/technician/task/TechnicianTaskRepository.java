
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.involves.Involves;
import acme.entities.task.Task;
import acme.realms.technicians.Technician;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("select i.task from Involves i where i.maintenanceRecord.id = :id")
	Collection<Task> findTaskByMaintenanceRecordId(int id);

	@Query("select t from Task t where t.technician.id = :id")
	Collection<Task> findTaskByTechnicianId(int id);

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("select t from Technician t where t.id = :id")
	Technician findTechnicianById(int id);

	@Query("select i from Involves i where i.task.id = :id")
	Collection<Involves> findInvolvesByTask(int id);

}
