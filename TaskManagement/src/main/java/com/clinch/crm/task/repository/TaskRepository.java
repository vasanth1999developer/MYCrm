package com.clinch.crm.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinch.crm.task.VO.TaskVo;


@Repository
public interface TaskRepository extends JpaRepository<TaskVo, Long> {

}
