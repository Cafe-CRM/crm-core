package com.cafe.crm.services.interfaces.checklist;


import com.cafe.crm.models.checklist.Checklist;

import java.util.List;

public interface ChecklistService {

	Checklist find(Long id);

	Checklist saveChecklistOnCloseShift(String text);

	Checklist saveChecklistOnOpenShift(String text);

	Checklist update(Checklist checklist);

	void delete(Long id);

	List<Checklist> getAll();

	List<Checklist> getAllForOpenShift();

	List<Checklist> getAllForCloseShift();
}
