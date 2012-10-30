package edu.clarkson.gdc.proxy.server;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.clarkson.gdc.proxy.Server;
import edu.clarkson.gdc.proxy.ServerContainer;

public class GDCServerContainer implements ServerContainer {

	private Map<String, Server> servers;

	private Map<String, Server> availableServers;

	public GDCServerContainer() {
		super();
		servers = new ConcurrentHashMap<String, Server>();
		availableServers = new ConcurrentHashMap<String, Server>();
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
}
