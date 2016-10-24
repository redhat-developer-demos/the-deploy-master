package com.redhat.developers.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/session")
@Api
public class SessionResource {

	private final String paramName = "array";

	@Context
	private HttpServletRequest request;

	@GET
	@Path("/health")
	@ApiOperation("Health check")
	public String health() {
		return "I'm ok";
	}

	@GET
	@Path("/add/{value}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	@ApiOperation("Add item to the session")
	public List<String> add(@PathParam("value") String value) {
		HttpSession session = request.getSession();
		List<String> values = (List<String>) session.getAttribute(paramName);
		if (values == null) {
			values = new ArrayList<>();
		}
		values.add(value);
		session.setAttribute(paramName, values);
		return values;
	}

	@GET
	@Path("/clear")
	@ApiOperation("Clear all values from the session")
	public void clear() {
		request.getSession().invalidate();
	}

	@GET
	@Path("/get")
	@SuppressWarnings("unchecked")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation("Get all values from the session as String")
	public String get() {
		HttpSession session = request.getSession();
		String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
		List<String> values = (List<String>) session.getAttribute(paramName);
		String printValue = (values == null ? "[EMPTY]" : Arrays.toString(values.toArray()));
		return "These are the values stored in the host [" + hostname + "] session: " + printValue;
	}

}
