package org.tynamo.conversations.test;

import static org.testng.Assert.assertFalse;

import org.eclipse.jetty.webapp.WebAppContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class NoConversationTest extends AbstractContainerTest {
	@BeforeClass
	public void configureWebClient() {
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
	}
	
	@Override
	public WebAppContext buildContext() {
		WebAppContext context = new WebAppContext("src/test/webapp", "/");
		/*
		 * Sets the classloading model for the context to avoid an strange "ClassNotFoundException: org.slf4j.Logger"
		 */
		context.setParentLoaderPriority(true);
		return context;
	}
	
	@Test
	public void conversationNotStarted() throws Exception {
		HtmlPage page = webClient.getPage(BASEURI + "noconversation");
		boolean sessionCreated = Boolean.parseBoolean(page.getElementById("sessionCreated").getTextContent());
		assertFalse(sessionCreated);
	}
}
