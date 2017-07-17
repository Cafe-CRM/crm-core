package com.cafe.crm.repositories.worker;

import com.cafe.crm.models.worker.Boss;
import com.cafe.crm.models.worker.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface WorkerRepository extends JpaRepository<Worker, Long> {

	// for shift
	@Query("SELECT u FROM Worker u WHERE u.firstName =:name")
	Worker getWorkerByNameForShift(@Param("name") String name);

	@Query("SELECT c FROM Worker c where c.enabled = true")
	List<Worker> getAllActiveWorker();

	Worker findByEmail(String email);

	Worker findByPhone(String phone);


}
