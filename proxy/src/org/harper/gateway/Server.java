package org.harper.gateway;

import javax.servlet.ServletRequest;

public interface Server {

	RequestHandler getRequestHandler(ServletRequest request);

}
