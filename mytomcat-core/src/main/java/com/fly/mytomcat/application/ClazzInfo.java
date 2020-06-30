package com.fly.mytomcat.application;

import com.fly.mytomcat.http.server.HttpServer;

final class ClazzInfo {

	private Class<?> clazz = null;

	private HttpServer bean = null;
	
	public ClazzInfo() {
		
	}
	
	public ClazzInfo(Class<?> clazz, HttpServer bean) {
		this.clazz = clazz;
		this.bean = bean;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public HttpServer getBean() {
		return bean;
	}

	public void setBean(HttpServer bean) {
		this.bean = bean;
	}
	
}
