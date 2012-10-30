package edu.clarkson.gdc.proxy;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyFilter implements Filter {

	Logger log = LoggerFactory.getLogger(getClass());

	Pattern excludePattern = null;

	ServerContainer container;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		if (null == container) {
			filterChain.doFilter(request, response);
			return;
		}
		// Get available server from container
		Server server = container.getServer();
		if (null == server) {
			filterChain.doFilter(request, response);
			return;
		}
		if (request instanceof HttpServletRequest
				&& null != excludePattern
				&& excludePattern.matcher(
						((HttpServletRequest) request).getContextPath())
						.matches()) {
			filterChain.doFilter(request, response);
			return;
		}

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
		// TODO Choose a server container based on configuration
		container = ServerContainerFactory.create(config
				.getInitParameter("CONTAINER_TYPE"));
		String excludeStr = config.getInitParameter("EXCLUDE_URL");
		if (!StringUtils.isEmpty(excludeStr)) {
			excludePattern = Pattern.compile(excludeStr);
		}
		if (null == container)
			container = ServerContainerFactory.createDefault();
	}

	@Override
	public void destroy() {
	}

}
