package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.models.note.Note;
import com.cafe.crm.services.interfaces.note.NoteService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@Controller
public class NoteController {

	private final NoteService noteService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(NoteController.class);

	@Autowired
	public NoteController(NoteService noteService) {
		this.noteService = noteService;
	}

	@RequestMapping(path = "/boss/settings/notes", method = RequestMethod.GET)
	public String showNoteSettingPage(Model model) {
		List<Note> orderedNotes = noteService.findAllOrderingByEnableDescNameAsc();
		model.addAttribute("notes", orderedNotes);
		return "settingPages/notesSettingPage";
	}

	@RequestMapping(path = "/boss/settings/note/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addNote(@Valid Note note, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			return ResponseEntity.badRequest().body(fieldError);
		}
		noteService.save(note);

		logger.info("Добавлена заметка \"" + note.getName() + "\"");

		return ResponseEntity.ok("");
	}

	@RequestMapping(path = "/boss/settings/note/delete", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteNote(@RequestParam("id") Long id) {
		Note note = noteService.getOne(id);
		noteService.delete(id);

		logger.info("Удалена заметка \"" + note.getName() + "\"");

		return ResponseEntity.ok("");
	}

	@RequestMapping(path = "/boss/settings/note/changeStatus", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> changeStatusNote(@RequestParam("id") Long id,
											  @RequestParam("enable") String enable) {
		boolean status = !Boolean.valueOf(enable);
		String state = status ? "enabled" : "disabled";
		Note note = noteService.changeStatus(id, status);

		logger.info("Изменён статус заметки с названием: \"" + note.getName() +
				"\" изменён на " + state);

		return ResponseEntity.ok("");
	}
}
