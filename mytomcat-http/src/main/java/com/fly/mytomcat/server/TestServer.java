package com.fly.mytomcat.server;

import com.fly.mytomcat.annotation.RequestMap;
import com.fly.mytomcat.http.Request;
import com.fly.mytomcat.http.Response;
import com.fly.mytomcat.http.server.HttpServer;

@RequestMap("/test")
public class TestServer implements HttpServer {

	@Override
	public void doget(Request request, Response response) {
		System.out.println(request.getRequestURL());
		response.send("bilibili！！！");
	}

	@Override
	public void dopost(Request request, Response response) {
		doget(request, response);
	}

}
