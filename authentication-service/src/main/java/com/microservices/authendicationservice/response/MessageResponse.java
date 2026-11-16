package com.microservices.authendicationservice.response;

/**
 * Legacy single-message response ({@code {"message": "..."}}). Unused; {@link ApiResponse}
 * replaced it.
 */
public class MessageResponse {
	/** The message text. */
	private String message;

	/** @param message the message text */
	public MessageResponse(String message) {
	    this.message = message;
	  }

	/** @return the message text */
	public String getMessage() {
		return message;
	}

	/** @param message the message text */
	public void setMessage(String message) {
		this.message = message;
	}
}
