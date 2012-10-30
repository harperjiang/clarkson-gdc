package edu.clarkson.gdc.proxy;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyFilter implements Filter {

	Logger log = LoggerFactory.getLogger(getClass());

	// TODO Choose a server container based on configuration
	ServerContainer container = null;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		// Get available server from container
		Server server = container.getServer();

		RequestHandler requestHandler = server.getRequestHandler(request);

		try {
			requestHandler.handleRequest(request, response);
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error(
					"Error occurred while processing request", e);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
