package org.tynamo.conversations.test.pages;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class NoConversation {
	
	@Inject
	private Request request;
	
	public boolean isSessionCreated() {
		return request.getSession(false) != null;
	}
	
}
