package org.tynamo.conversations.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.http.Link;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONLiteral;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.tynamo.conversations.ConversationModeratorAware;
import org.tynamo.conversations.services.ConversationManager;

public class ConversationModerator {
	private static final String eventName = "checkidle";

	@Inject
	private ComponentResources componentResources;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Parameter("15")
	private int idleCheck;

	@Parameter("30")
	private int warnBefore;

	@Parameter(defaultPrefix = "literal")
	private String warnBeforeHandler;

	@Parameter(defaultPrefix = "literal")
	private String endedHandler;

	@Parameter("false")
	private boolean keepAlive;

	JSONObject onCheckidle() {
		// FIXME check if keepalive is set
		int nextCheckInSeconds = -1;
		JSONObject object = new JSONObject();
		String conversationId = conversationManager.getActiveConversation();
		// Conversation still exists
		if (conversationId != null) {
			nextCheckInSeconds = conversationManager.getSecondsBeforeActiveConversationBecomesIdle();
			// Shouldn't be negative.
			if (nextCheckInSeconds < 0) return null;
			if (componentResources.getContainer() instanceof ConversationModeratorAware) ((ConversationModeratorAware) componentResources.getContainer()).onConversationIdleCheck();
			// If keepalive is true, subtract 1 so conversation will be refreshed before end,
			if ("true".equals(request.getParameter(ConversationManager.Parameters.keepalive.name()))) nextCheckInSeconds--;
			else {
			}
			// Negative if warn is disabled
			int warnInSeconds = Integer.valueOf(request.getParameter("warn"));
			// add 1 , no keepalive
			if (warnInSeconds < 0) nextCheckInSeconds++;
			else {
				warnInSeconds = nextCheckInSeconds - warnInSeconds;
				// Change next check time for warn time or warn
				if (warnInSeconds > 0) nextCheckInSeconds = warnInSeconds;
				// limit how many times you trigger the warn
				else if (warnInSeconds > -nextCheckInSeconds) object.put("warn", nextCheckInSeconds);

			}
		}

		object.put("nextCheck", nextCheckInSeconds);
		return object;
	}

	JSONObject onRefresh() {
		return null;
	}

	JSONObject onEnd() {
		if (componentResources.getContainer() instanceof ConversationModeratorAware) ((ConversationModeratorAware) componentResources.getContainer()).onConversationEnded();
		conversationManager.endConversation(conversationManager.getActiveConversation());
		return new JSONObject();
	}

	@Inject
	private Request request;

	@Inject
	private ConversationManager conversationManager;

	@AfterRender
	public void afterRender() {
		Link link = componentResources.createEventLink(eventName);
		String baseURI = link.toAbsoluteURI();
		int index = baseURI.indexOf(":" + eventName);
		String defaultURIparameters = baseURI.substring(index + eventName.length() + 1);
		defaultURIparameters += "".equals(defaultURIparameters) ? "?" : "&";
		defaultURIparameters += ConversationManager.Parameters.keepalive.name() + "=";
		baseURI = baseURI.substring(0, index + 1);

		Object warnBeforeHandlerParam = warnBeforeHandler == null ? new JSONLiteral(null) : warnBeforeHandler;
		Object endedHandlerParam = endedHandler == null ? new JSONLiteral(null) : endedHandler;
		
		// System.out.println("Active conversation is " +  conversationManager.getActiveConversation());
		javaScriptSupport.require("conversation/ConversationModerator") //
		        .invoke("startConversation") //
		        .with(baseURI, defaultURIparameters, keepAlive, "", idleCheck, warnBefore, warnBeforeHandlerParam, endedHandlerParam);
	}

}
