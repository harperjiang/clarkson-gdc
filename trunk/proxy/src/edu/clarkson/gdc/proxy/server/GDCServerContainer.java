package edu.clarkson.gdc.proxy.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

import edu.clarkson.gdc.proxy.Server;
import edu.clarkson.gdc.proxy.ServerContainer;

public class GDCServerContainer implements ServerContainer {

	private Map<String, Server> servers;

	private Map<String, Server> availableServers;

	private GDCServerContainer() {
		super();
		servers = new ConcurrentHashMap<String, Server>();
		availableServers = new ConcurrentHashMap<String, Server>();
		init();
	}

	protected void init() {
		// Init with properties
		Properties prop = new Properties();
		try {
			prop.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("gdc.properties"));
			String[] serverList = prop.get("servers").toString().split(",");
			for (String serverId : serverList) {
				DefaultServer server = new DefaultServer();
				server.setId(serverId);
				server.setAddress(prop.get(server + ".address").toString());

				servers.put(serverId, server);
				availableServers.put(serverId, server);
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error(
					"Cannot load properties,GDCServerContainer cannot work", e);
		}
	}

	@Override
	public Server getServer() {
		Iterator<Server> available = availableServers.values().iterator();
		if (available.hasNext()) {
			return available.next();
		}
		return null;
	}

	public void mark(String serverId, boolean available) {
		if (servers.containsKey(serverId)) {
			synchronized (servers) {
				if (servers.containsKey(serverId)) {
					if (available && !availableServers.containsKey(serverId))
						availableServers.put(serverId, servers.get(serverId));
					if (!available && availableServers.containsKey(serverId))
						availableServers.remove(serverId);
				}
			}
		}
	}

	private static GDCServerContainer instance = new GDCServerContainer();

	public static GDCServerContainer getInstance() {
		return instance;
	}
}
