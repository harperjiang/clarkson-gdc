package edu.clarkson.gdc.proxy.server;

import javax.servlet.ServletRequest;

import edu.clarkson.gdc.proxy.RequestHandler;
import edu.clarkson.gdc.proxy.Server;
import edu.clarkson.gdc.proxy.requesthandler.ProxyRequestHandler;

public class DefaultServer implements Server {

	private String id;

	private String address;

	private int priority;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public RequestHandler getRequestHandler(ServletRequest request) {
		return new ProxyRequestHandler(this);
	}

}
