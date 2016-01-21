package org.tynamo.conversations.test.services;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.tynamo.conversations.services.ConversationModule;

@ImportModule(ConversationModule.class)
public class AppModule {
	public static void bind(ServiceBinder binder) {
	}

}
