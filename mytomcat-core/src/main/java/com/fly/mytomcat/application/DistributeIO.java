package com.fly.mytomcat.application;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fly.mytomcat.http.Request;
import com.fly.mytomcat.http.Response;
import com.fly.mytomcat.http.server.HttpServer;
import com.fly.mytomcat.interceptor.HandlerInterceptor;

final class DistributeIO {

	private final static Map<String, ClazzInfo> httpmap = Application.getHttpmap() ;
	
	private final static HandlerInterceptor interceptor = Application.getInterceptor();
	
	//可访问静态资源后缀
	private final static String[] staticsuffix = {".html", ".css", ".jpg", ".png", ".js", ".gif"};
	
	//可访问静态资源路径
	private final static String staticresoursepath = "webapps/root";
	
	private final static Logger log = Logger.getLogger(DistributeIO.class);
	
	public static void execute(Request request, Response response) {
		
		String requestpath = request.getReqeustpath();
		requestpath = (requestpath == null)? "" : requestpath;
		String staticresoursepath = StaticResourse(requestpath);
		//判断是否是静态资源
		if(null == staticresoursepath) {
			ClazzInfo clazzInfo = httpmap.get(requestpath);
			if(null != clazzInfo) {
				if(null == clazzInfo.getBean()) {
					try {
						clazzInfo.setBean((HttpServer) clazzInfo.getClazz().getConstructor().newInstance());
						log.info("已初始化实例：" + clazzInfo.getClazz().getName());
					} catch (Exception e) {
						log.error("初始化实例：" + clazzInfo.getClazz().getName() + " 异常");
						e.printStackTrace();
					} 
					httpmap.put(requestpath, clazzInfo);
				}
				if(interceptor != null)
					interceptor.execute(clazzInfo.getBean(), request, response);
				else
					clazzInfo.getBean().server(request, response);
			}else
				response.send("<h1>404 NOT FOUND</h1>");
		}else {
			File staticfile = new File(staticresoursepath);
			if(staticfile.exists())
				response.staticresourse(staticfile);
			else {
				response.send("<h1>404 NOT FOUND</h1>");
				log.info("无资源：" + staticresoursepath);
			}
		}
		
	}
	
	/**
	 * 判断是否为静态资源路径
	 * @param requestpath 访问路径
	 * @return 是返回路径，不是返回null
	 */
	private static String StaticResourse(String requestpath) {
		for(String suffix : staticsuffix) {
			if(requestpath.toLowerCase().endsWith(suffix)) {
				return staticresoursepath + requestpath;
			}
		}
		return null;
	}
	
}
