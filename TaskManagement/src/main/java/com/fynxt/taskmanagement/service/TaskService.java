package com.fynxt.taskmanagement.service;

import java.util.List;

import com.fynxt.taskmanagement.BO.TaskBo;
import com.fynxt.taskmanagement.BO.TaskResponseBO;

public interface TaskService {

	TaskResponseBO createTask(TaskBo request);

	List<TaskResponseBO> getAllTasks();

	TaskResponseBO getTaskById(Long id);

	TaskResponseBO updateTask(Long id, TaskBo request);

	void deleteTask(Long id);

}
