package com.clinch.crm.task.service;

import java.util.List;

import com.clinch.crm.task.BO.TaskBo;
import com.clinch.crm.task.BO.TaskResponseBO;

public interface TaskService {

	TaskResponseBO createTask(TaskBo request);

	List<TaskResponseBO> getAllTasks();

	TaskResponseBO getTaskById(Long id);

	TaskResponseBO updateTask(Long id, TaskBo request);

	void deleteTask(Long id);

}
