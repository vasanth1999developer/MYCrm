package com.fynxt.taskmanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fynxt.taskmanagement.BO.TaskBo;
import com.fynxt.taskmanagement.BO.TaskResponseBO;
import com.fynxt.taskmanagement.CustomGlobalException.ResourceNotFoundException;
import com.fynxt.taskmanagement.VO.TaskVo;
import com.fynxt.taskmanagement.repository.TaskRepository;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskRepository taskRepository;

	@Override
	public TaskResponseBO createTask(TaskBo taskBo) {
		TaskVo taskVo = new TaskVo();

		try {

			taskVo = taskRepository.save(mapToDao(taskBo));
			return mapToResponse(taskVo);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<TaskResponseBO> getAllTasks() {
		return taskRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public TaskResponseBO getTaskById(Long id) {
		TaskVo taskVo = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
		return mapToResponse(taskVo);
	}

	@Override
	public TaskResponseBO updateTask(Long id, TaskBo taskBo) {

//		try {
//			TaskVo taskVo = taskRepository.findById(id)
//					.orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
//
//			if (taskBo.getTitle() != null && !taskBo.getTitle().isBlank()) {
//
//				taskVo.setTitle(taskBo.getTitle());
//			} else if (taskBo.getTitle() != null) {
//				throw new IllegalArgumentException("Title cannot be blank");
//			}
//
//			taskVo.setDescription(taskBo.getDescription());
//			taskVo.setStatus(taskBo.getStatus());
//			taskVo.setDueDate(taskBo.getDueDate());
//
//			TaskVo updatedTaskVo = taskRepository.save(taskVo);
//			return mapToResponse(updatedTaskVo);
//
//		} catch (ResourceNotFoundException | IllegalArgumentException ex) {
//			throw ex;
//		} catch (Exception ex) {
//			throw new RuntimeException("Failed to update task", ex);
//		}

		return null;
	}

	@Override
	public void deleteTask(Long id) {

		if (!taskRepository.existsById(id)) {
			throw new ResourceNotFoundException("Task not found with id " + id);
		}
		taskRepository.deleteById(id);
	}

	private TaskResponseBO mapToResponse(TaskVo taskVo) {
		TaskResponseBO taskResponseBo = new TaskResponseBO();
		BeanUtils.copyProperties(taskVo, taskResponseBo);
		return taskResponseBo;
	}

	private TaskVo mapToDao(TaskBo taskBo) {
		TaskVo taskVo = new TaskVo();
		BeanUtils.copyProperties(taskBo, taskVo);
		return taskVo;

	}
}
