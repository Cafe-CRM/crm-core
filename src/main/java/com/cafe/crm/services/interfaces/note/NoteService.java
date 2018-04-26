package com.cafe.crm.services.interfaces.note;

import com.cafe.crm.models.note.Note;

import java.util.List;

public interface NoteService {
	Note save(Note note);

	List<Note> findAllOrderingByEnableDescNameAsc();

	List<Note> findAllByEnableIsTrue();

	void delete(Long id);

	Note changeStatus(Long id, boolean enable);

	Note getOne(Long id);
}
