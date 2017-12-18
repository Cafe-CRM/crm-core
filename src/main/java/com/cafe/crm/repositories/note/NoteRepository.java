package com.cafe.crm.repositories.note;

import com.cafe.crm.models.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
	List<Note> findByCompanyIdOrderByEnableDescNameAsc(Long companyId);

	List<Note> findByCompanyIdAndEnableIsTrue(Long companyId);
}
