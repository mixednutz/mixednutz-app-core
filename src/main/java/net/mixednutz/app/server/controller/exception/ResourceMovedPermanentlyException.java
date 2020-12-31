package net.mixednutz.app.server.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.MOVED_PERMANENTLY, reason="Resource moved")
public class ResourceMovedPermanentlyException extends RuntimeException {

	final String redirectUri;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceMovedPermanentlyException(String redirectUrl) {
		super();
		this.redirectUri = redirectUrl;
	}
	
	public ResourceMovedPermanentlyException(String message, String redirectUrl) {
		super(message);
		this.redirectUri = redirectUrl;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

}
