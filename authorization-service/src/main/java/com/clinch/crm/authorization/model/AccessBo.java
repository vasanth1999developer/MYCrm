package com.clinch.crm.authorization.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * API representation of an access entry: {@code {"accessId": 1, "accessName": "VIEW_LEADS"}}.
 *
 * <p>Used as request body and in responses. Lombok {@code @Data} generates getters/setters.
 */
@Data
public class AccessBo {


	/** The id; 0 when creating. */
	private int accessId ;

	/** The name; required ({@code @NotNull} + {@code @NotEmpty}, checked where {@code @Valid} is used). */
	@NotNull(message = "Access name must not be null")
    @NotEmpty(message = "Access name must not be empty")
	private String accessName ;


	/**
	 * @param accessId   the id
	 * @param accessName the name
	 */
	public AccessBo(int accessId, String accessName) {
		this.accessId=accessId;
		this.accessName=accessName;
	}

	/** Empty constructor, needed by Jackson and {@code BeanUtils}. */
	public AccessBo() {

	}
}
