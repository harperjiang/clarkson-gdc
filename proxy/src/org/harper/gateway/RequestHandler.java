package org.harper.gateway;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface RequestHandler {

	void handleRequest(ServletRequest request, ServletResponse response);

}
