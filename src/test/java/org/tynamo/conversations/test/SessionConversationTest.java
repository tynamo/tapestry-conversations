/*
 * Created on Dec 13, 2004
 *
 * Copyright 2004 Chris Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.tynamo.conversations.test;

import static org.testng.Assert.assertTrue;

import org.eclipse.jetty.webapp.WebAppContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SessionConversationTest extends AbstractContainerTest {
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
	public void conversationStartedAndOngoing() throws Exception
	{
		// Should we reset the conversation to make sure this succeeds?
		assertTrue(getSecondsLeft() > 58);
		Thread.sleep(2000);
		assertTrue(getSecondsLeft() < 59);
		HtmlPage page = webClient.getPage(BASEURI + "sessionconversation");
		page = page.getAnchorByName("endconversation").click();
		assertTrue(getSecondsLeft(page) > 58);
	}

	private int getSecondsLeft() throws Exception {
		return getSecondsLeft((HtmlPage)webClient.getPage(BASEURI + "sessionconversation") );
	}

	private int getSecondsLeft(HtmlPage page) throws Exception {
		DomElement element = page.getElementById("secondsLeft");
		return Integer.parseInt(element.getTextContent());
	}

}
