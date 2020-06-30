package com.fly.mytomcat.interceptor;

import com.fly.mytomcat.http.Request;
import com.fly.mytomcat.http.Response;
import com.fly.mytomcat.http.server.HttpServer;



public interface HandlerInterceptor {
	
	public default void execute(HttpServer httpserver, Request request, Response response) {
		
		if(!preHandle(request, response)) {
			response.send(new RuntimeException("被拦截器拦截").toString());
			return ;
		}
		httpserver.server(request, response);
		postHandle(request, response);
		afterCompletion(request, response);
	}
	
	public boolean preHandle(Request request, Response response);
	
	public void postHandle(Request request, Response response);
	
	public void afterCompletion(Request request, Response response);
	
}
