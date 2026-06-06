package com.fynxt.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fynxt.taskmanagement.VO.TaskVo;


@Repository
public interface TaskRepository extends JpaRepository<TaskVo, Long> {

}
