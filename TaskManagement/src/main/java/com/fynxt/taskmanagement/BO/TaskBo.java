package com.fynxt.taskmanagement.BO;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

public class TaskBo {
	
    @NotBlank(message = "Title is mandatory")
    private String title;
    
    private String description;
    private String status;
    private LocalDate dueDate;
    
    
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDate getDueDate() {
		return dueDate;
	}
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

    
    
    
}
