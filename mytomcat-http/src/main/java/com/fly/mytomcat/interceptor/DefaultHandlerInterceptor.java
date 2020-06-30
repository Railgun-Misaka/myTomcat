package com.fly.mytomcat.interceptor;

import com.fly.mytomcat.http.Request;
import com.fly.mytomcat.http.Response;

public final class DefaultHandlerInterceptor implements HandlerInterceptor {
	
	
	@Override
	public boolean preHandle(Request request, Response response) {
		return true;
	}

	@Override
	public void postHandle(Request request, Response response) {

	}

	@Override
	public void afterCompletion(Request request, Response response) {

	}

}
