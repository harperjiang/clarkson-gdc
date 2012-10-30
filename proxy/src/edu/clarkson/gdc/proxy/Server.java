package edu.clarkson.gdc.proxy;

import javax.servlet.ServletRequest;

public interface Server {

	RequestHandler getRequestHandler(ServletRequest request);

}
