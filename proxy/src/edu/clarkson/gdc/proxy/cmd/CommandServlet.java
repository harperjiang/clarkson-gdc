package edu.clarkson.gdc.proxy.cmd;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import edu.clarkson.gdc.proxy.server.GDCServerContainer;

/**
 * Servlet implementation class CommandServlet
 */
public class CommandServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommandServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String serverId = request.getParameter("server_id");
		String status = request.getParameter("status");
		if (!StringUtils.isEmpty(serverId) || !StringUtils.isEmpty(status)) {
			GDCServerContainer.getInstance().mark(serverId,
					"true".equals(status));
		}
	}
}
