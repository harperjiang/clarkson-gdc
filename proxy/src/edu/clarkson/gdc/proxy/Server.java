package edu.clarkson.gdc.proxy;

import javax.servlet.ServletRequest;

public interface Server {

	String getId();

	String getAddress();

	int getPriority();

	RequestHandler getRequestHandler(ServletRequest request);

}
