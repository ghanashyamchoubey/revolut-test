package com.main.task.revolut.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.main.task.revolut.service.AccountService;
import com.main.task.revolut.service.TransactionService;
import com.main.task.revolut.serviceImpl.AccountServiceImpl;
import com.main.task.revolut.serviceImpl.TransactionServiceImpl;

public class App {
	public static void main(String[] args) throws Exception {
		startJetty();
	}
	
	public static void startJetty() throws Exception {
		Server jettyServer = new Server(7070);
        ServletContextHandler httpContext = new ServletContextHandler(jettyServer, "/");
        httpContext.addServlet(new ServletHolder(new ServletContainer(resource())), "/*");
        jettyServer.setHandler(httpContext);
        jettyServer.start();
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
	}
	
	private static ResourceConfig resource() {
		ResourceConfig resourceConfig = new ResourceConfig().packages("com.main.task.revolut.controller");

		return resourceConfig.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(AccountServiceImpl.class).to(AccountService.class);
				bind(TransactionServiceImpl.class).to(TransactionService.class);
			}
		});
	}
}
