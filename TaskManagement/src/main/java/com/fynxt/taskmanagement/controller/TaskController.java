package com.fynxt.taskmanagement.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fynxt.taskmanagement.BO.TaskBo;
import com.fynxt.taskmanagement.BO.TaskResponseBO;
import com.fynxt.taskmanagement.service.TaskService;

@RestController
@RequestMapping("/task")
public class TaskController {

	@Autowired
	private TaskService taskService;

	@PostMapping("/create-task")
	public ResponseEntity<TaskResponseBO> createTask(@Valid @RequestBody TaskBo request) {
		TaskResponseBO response = taskService.createTask(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/get-all-task")
	public ResponseEntity<List<TaskResponseBO>> getAllTasks() {
		return ResponseEntity.ok(taskService.getAllTasks());
	}

	@GetMapping("/get-task/{id}")
	public ResponseEntity<TaskResponseBO> getTaskById(@PathVariable Long id) {
		return ResponseEntity.ok(taskService.getTaskById(id));
	}

	@PutMapping("/update-task/{id}")
	public ResponseEntity<TaskResponseBO> updateTask(@PathVariable Long id, @RequestBody TaskBo request) {
		return ResponseEntity.ok(taskService.updateTask(id, request));
	}

	@DeleteMapping("/delete-task/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
		taskService.deleteTask(id);
		return ResponseEntity.noContent().build();
	}

}
