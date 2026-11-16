package com.clinch.crm.authorization.model;

import java.util.Set;

import lombok.Data;

/**
 * API representation of a privilege:
 * {@code {"privilegeId": 1, "privilegeName": "LEAD_READ", "accesses": [{"accessId": 1}]}}.
 *
 * <p>Has no validation annotations, so {@code @Valid} on it checks nothing. {@code accesses} is
 * {@code null} in list responses and filled in single-privilege responses.
 */
@Data
public class PrivilegeBo {


	/** The id; 0 when creating. */
	private int privilegeId;

	/** The name that ends up in the JWT. */
	private String privilegeName;


	/** Access entries of this privilege. */
	private Set<AccessBo> accesses;



}
