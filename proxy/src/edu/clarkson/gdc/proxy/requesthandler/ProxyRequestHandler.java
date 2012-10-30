package edu.clarkson.gdc.proxy.requesthandler;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.LoggerFactory;

import edu.clarkson.gdc.proxy.RequestHandler;
import edu.clarkson.gdc.proxy.Server;

public class ProxyRequestHandler implements RequestHandler {

	private Server server;

	public ProxyRequestHandler(Server server) {
		this.server = server;
	}

	protected Server getServer() {
		return server;
	}

	@Override
	public void handleRequest(ServletRequest request, ServletResponse response) {
		if (response.isCommitted())
			throw new IllegalStateException("Response cannot be rendered");
		// Create HttpRequest based on ServletRequest
		if (!(request instanceof HttpServletRequest)
				|| !(response instanceof HttpServletResponse)) {
			throw new IllegalArgumentException(
					"Cannot handle non-Http requests");
		}
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpUriRequest httpRequest = createHttpRequest(server,
				httpServletRequest);

		// Construct HttpClient and make connections
		// TODO Use cached httpclient
		HttpClient client = new DefaultHttpClient();

		// TODO Handle Cookie

		HttpContext context = new BasicHttpContext();
		HttpResponse httpResponse = null;
		try {
			httpResponse = client.execute(httpRequest, context);
		} catch (Exception e) {
			throw new RuntimeException("Error while accessing remote server", e);
		}
		composeServletResponse(httpResponse, (HttpServletResponse) response);
	}

	private HttpUriRequest createHttpRequest(Server server,
			HttpServletRequest httpServletRequest) {
		HttpUriRequest httpRequest = null;

		String url = MessageFormat.format("{0}://{1}{2}",
				httpServletRequest.getScheme(), server.getAddress(),
				httpServletRequest.getContextPath());

		if ("GET".equals(httpServletRequest.getMethod())) {
			httpRequest = new HttpGet(url);
		} else if ("POST".equals(httpServletRequest.getMethod())) {
			httpRequest = new HttpPost(url);
		} else {
			throw new UnsupportedOperationException("Unsupported Http Method:"
					+ httpServletRequest.getMethod());
		}

		// Copy headers
		@SuppressWarnings("rawtypes")
		Enumeration headerNames = httpServletRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			String headerValue = httpServletRequest.getHeader(headerName);
			// Specially dealing with host
			if ("host".equals(headerName)) {
				httpRequest.setHeader("host", server.getAddress());
			} else
				httpRequest.setHeader(headerName, headerValue);
		}

		// Copy Parameters
		@SuppressWarnings("rawtypes")
		Enumeration parameterNames = httpServletRequest.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = (String) parameterNames.nextElement();
			String parameterValue = httpServletRequest
					.getParameter(parameterName);
			httpRequest.getParams().setParameter(parameterName, parameterValue);
		}
		// TODO Auto-generated method stub
		return httpRequest;
	}

	private void composeServletResponse(HttpResponse httpResponse,
			HttpServletResponse response) {
		// Copy Headers
		for (Header header : httpResponse.getAllHeaders()) {
			response.setHeader(header.getName(), header.getValue());
		}
		// Copy Status
		response.setStatus(httpResponse.getStatusLine().getStatusCode());

		// TODO Copy params

		if (null != httpResponse.getEntity()) {
			response.setContentLength((int) httpResponse.getEntity()
					.getContentLength());
			if (null != httpResponse.getEntity().getContentType())
				response.setContentType(httpResponse.getEntity()
						.getContentType().getValue());
			if (null != httpResponse.getEntity().getContentEncoding())
				response.setCharacterEncoding(httpResponse.getEntity()
						.getContentEncoding().getValue());
		}
		// Copy Stream content
		try {
			IOUtils.copy(httpResponse.getEntity().getContent(),
					response.getOutputStream());
		} catch (Exception e) {
			if (!response.isCommitted())
				response.setStatus(HttpStatus.SC_NOT_FOUND);
			throw new RuntimeException(
					"IOException while dealing with Stream Content", e);
		} finally {
			try {
				httpResponse.getEntity().getContent().close();
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} catch (IOException ioe) {
				LoggerFactory.getLogger(getClass()).error(
						"IOException while closing Stream ", ioe);
			}
		}
	}
}
