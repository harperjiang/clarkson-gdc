package edu.clarkson.gdc.proxy;

import java.util.HashMap;
import java.util.Map;

import edu.clarkson.gdc.proxy.server.GDCServerContainer;

public class ServerContainerFactory {

	static final Map<String, ServerContainer> containers = new HashMap<String, ServerContainer>();

	static {
		containers.put("GDC", GDCServerContainer.getInstance());
	}

	public static ServerContainer create(String initParameter) {
		return containers.get(initParameter);
	}

	public static ServerContainer createDefault() {
		return null;
	}

}
