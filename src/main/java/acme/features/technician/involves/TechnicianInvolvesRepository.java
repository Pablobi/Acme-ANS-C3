
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.involves.Involves;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.task.Task;

@Repository
public interface TechnicianInvolvesRepository extends AbstractRepository {

	@Query("select i from Involves i where i.maintenanceRecord.id = :id")
	Collection<Involves> findInvolvesByMaintenanceRecordId(int id);

	@Query("select i from Involves i where i.task.id = :id and i.maintenanceRecord.id = :id2")
	Involves findInvolvesByTaskAndMaintenanceRecord(int id, int id2);

	@Query("select i from Involves i where i.id = :id")
	Involves findInvolvesById(int id);

	@Query("select i from Involves i")
	Collection<Involves> findAllInvolves();

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("select t from Task t")
	Collection<Task> findAllTasks();

	@Query("select i.task from Involves i where i.maintenanceRecord.id = :id")
	Collection<Task> findTasksInMaintenanceRecord(int id);

}
