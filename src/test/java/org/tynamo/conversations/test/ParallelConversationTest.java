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

import static org.testng.Assert.assertFalse;

import org.eclipse.jetty.webapp.WebAppContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ParallelConversationTest extends AbstractContainerTest {
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
	public void started2conversations() throws Exception
	{
		HtmlPage page1 = webClient.getPage(BASEURI + "parallelconversation");
		HtmlPage page2 = webClient.getPage(BASEURI + "parallelconversation");
		assertFalse(page1.getWebResponse().getWebRequest().getUrl().toString()
			.equals(page2.getWebResponse().getWebRequest().getUrl().toString()));
	}
}
