package com.mytomcat.controller;

import com.fly.mytomcat.annotation.RequestMap;
import com.fly.mytomcat.http.Request;
import com.fly.mytomcat.http.Response;
import com.fly.mytomcat.http.server.HttpServer;

@RequestMap("/haha")
public class TestController implements HttpServer {

	public void doget(Request request, Response response) {
		response.send("bilibili干杯！！！");
	}

	public void dopost(Request request, Response response) {
		doget(request, response);
	}

}
