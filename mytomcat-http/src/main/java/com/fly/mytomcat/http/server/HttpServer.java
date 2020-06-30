package com.fly.mytomcat.http.server;

import com.fly.mytomcat.http.Request;
import com.fly.mytomcat.http.Response;

public interface HttpServer {
	
	public default void server(Request request, Response response) {
		if(request.getMethod().equalsIgnoreCase("get"))
			doget(request, response);
		else
			dopost(request, response);
	}
	
	public void doget(Request request, Response response);
	
	public void dopost(Request request, Response response);
	
}
