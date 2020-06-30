package com.fly.mytomcat.http;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fly.mytomcat.exception.TcontinueException;
import com.fly.mytomcat.util.HttpAnalysis;

public class Request {
	
	private String method;// 请求方法
	private String protocol;// 协议版本
	private String requestURL;
	private String requestURI;// 请求的URI地址 在HTTP请求的第一行的请求方法后面
	private String host;// 请求的主机信息
	private String connection;// Http请求连接状态信息 对应HTTP请求中的Connection
	private String agent;// 代理，用来标识代理的浏览器信息 ,对应HTTP请求中的User-Agent:
	private String language;// 对应Accept-Language
	private String encoding;// 请求的编码方式 对应HTTP请求中的Accept-Encoding
	private String charset;// 请求的字符编码 对应HTTP请求中的Accept-Charset
	private String accept;// 对应HTTP请求中的Accept;
	private String content;//请求正文
	private String origin;
	private String reqeustpath;
	private final InputStream is;
	
	private Map<String, String> parameters ;//参数
	
	private Map<String, String> map = new HashMap<String, String>();
	
	private final static Logger log = Logger.getLogger(Request.class);
	
	public Request(InputStream is) throws TcontinueException {
		this.is = is;
		byte[] b = new byte[1024];
		String str = "";
		try {
			is.read(b);
			str = new String(b, "utf-8");
			map = HttpAnalysis.requestAnalysis(str);
		} catch (Exception e) {
			log.error("输入流读取错误，当前线程：" + Thread.currentThread().getName());
			e.printStackTrace();
			throw new TcontinueException();
		}
	}

	public String getReqeustpath() {
		if (null == reqeustpath)
			reqeustpath = map.get("reqeustpath");
		return reqeustpath;
	}

	public String getMethod() {
		if (null == method)
			method = map.get("method");
		return method;
	}

	public String getProtocol() {
		if (null == protocol)
			protocol = map.get("protocol");
		return protocol;
	}

	public String getRequestURL() {
		if (null == requestURL)
			requestURL = getHost() + getRequestURI();
		return requestURL;
	}

	public String getRequestURI() {
		if (null == requestURI)
			requestURI = map.get("requestURI");
		return requestURI;
	}

	public String getHost() {
		if (null == host)
			host = map.get("Host");
		return host;
	}

	public String getConnection() {
		if (null == connection)
			connection = map.get("Connection");
		return connection;
	}

	public String getAgent() {
		if (null == agent)
			agent = map.get("User-Agent");
		return agent;
	}

	public String getLanguage() {
		if (null == language)
			language = map.get("Accept-Language");
		return language;
	}

	public String getEncoding() {
		if (null == encoding)
			encoding = map.get("Accept-Encoding");
		return encoding;
	}

	public String getCharset() {
		if (null == charset)
			charset = map.get("Accept-Charset");
		return charset;
	}

	public String getAccept() {
		if (null == reqeustpath)
			accept = map.get("Accept");
		return accept;
	}

	public InputStream getInputStream() {
		return is;
	}

	public String getContent() {
		if (null == content)
			content = map.get("content");
		return content;
	}

	public Map<String, String> getParameters() {
		if(parameters == null) {
			parameters = new HashMap<String, String>();
			String params = map.get("parameters");
			if(null != params) {
				String[] sss = params.split("&");
				for(String ss : sss) {
					String[] s = ss.split("=");
					parameters.put(s[0].trim(), s[1].trim());
				}
			}
			
		}
		return parameters;
	}	
	
	public String getParameter(String key) {
		return getParameters().get(key);
	}

	public String getOrigin() {
		if(null == origin)
			origin = map.get("Origin");
		return origin;
	}
}
