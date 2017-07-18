package com.cafe.crm.services.interfaces.worker;


import com.cafe.crm.models.worker.Boss;
import com.cafe.crm.models.worker.Manager;
import com.cafe.crm.models.worker.Worker;

import java.util.List;

public interface WorkerService {

    List<Worker> listAllWorker();

    List<Manager> listAllManager();

    List<Boss> listAllBoss();

    void addWorker(Worker worker);

    void addManager(Manager manager);

    void addBoss(Boss boss);

    void editWorker(Worker worker, Long adminId, Long bossId, String password, String submitPassword);

    void editManager(Manager manager, Long adminId, Long bossId);

    void editBoss(Boss boss, Long bossId, Long adminId, Long shiftSalary);

    Worker findWorkerById(Long id);

    void deleteWorker(Worker worker);

    void castWorkerToManager(Worker worker, String password, Long adminPositionId);

    void castWorkerToBoss(Worker worker, String password, Long bossPositionId);

    void castWorkerToBossAndAdmin(Worker worker, String password, Long bossPositionId, Long adminPositionId);

    void castManagerToBoss(Manager manager, Long bossPositionId);

    void castManagerToWorker(Manager manager);

    void castBossToWorker(Boss boss);

    void castBossToManager(Boss boss, Long adminPositionId, Long shiftSalary);

    String parsePhoneNumber(String phoneNumber);

}
