package org.tynamo.conversations.services;

import java.lang.reflect.Method;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.commons.Configuration;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.http.services.RequestGlobals;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.Order;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.PersistentFieldStrategy;

public class ConversationModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(ConversationManager.class);
	}

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void setApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.JAVASCRIPT_INFRASTRUCTURE_PROVIDER, "jquery");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("conversation", "org.tynamo.conversations"));
	}

	public static void contributePersistentFieldManager(MappedConfiguration<String, PersistentFieldStrategy> configuration, RequestGlobals requestGlobals,
			Request request, ConversationManager conversationManager) {
		configuration.add("conversation", new ConversationalPersistentFieldStrategy(request, conversationManager));
	}

	// public static <T> T decorateComponentRequestHandler(Class<T> serviceInterface, T delegate, RequestHandlerDecorator decorator) {
	// return decorator.build(serviceInterface, delegate);
	// }

	@Match("ComponentRequestHandler")
	@Order("before:*")
	public static void adviseComponentRequestHandler(final MethodAdviceReceiver receiver,
		final ConversationManager conversationManager) {
		MethodAdvice advice = new MethodAdvice() {
			public void advise(MethodInvocation invocation) {
				conversationManager.activateConversation(invocation.getParameter(0));
				invocation.proceed();
			}
		};

		for (Method method : receiver.getInterface().getMethods())
			receiver.adviseMethod(method, advice);

	}

}