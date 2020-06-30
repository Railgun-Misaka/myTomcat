package com.fly.mytomcat.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 *http解析 
 * 
 */
public class HttpAnalysis {
	
	/**
	 * http请求解析
	 */
	public static Map<String, String> requestAnalysis(String http) {
		
		Map<String, String> map = new HashMap<String, String>();
//		http = "GET /ss HTTP/1.1\r\n" + 
//				"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" + 
//				"Accept-Language: zh-CN\r\n" + 
//				"Upgrade-Insecure-Requests: 1\r\n" + 
//				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362\r\n" + 
//				"Accept-Encoding: gzip, deflate\r\n" + 
//				"Host: 127.0.0.1\r\n" + 
//				"Connection: Keep-Alive\r\n" +
//				"";
		
		String[] pers = http.split("\r\n");
		String[] line = pers[0].split(" ");
		if(line.length != 3)
			return map;
		map.put("method", line[0].trim());
		map.put("requestURI", line[1].trim());
		map.put("protocol", line[2].trim());
		
		
		String[] s = map.get("requestURI").split("\\?", 2);//因为+、*、|、\等符号在正则表达示中有相应的不同意义，所以在使用时要进行转义处理。

		map.put("reqeustpath", s[0].trim());
		if(s.length == 2) {
			map.put("parameters", s[1].trim());
		}
		
		for(int i = 1; i < pers.length; ++i) {
			line = pers[i].split(": ", 2);
			if(line.length == 2) {
				map.put(line[0].trim(), line[1].trim());
			}else {
				map.put("content", line[0].trim());
			}
			
		}
		return map;
	}
	
	public static Map<String, String> responseAnalysis(String http) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		return map;
	}
	
	public static String responseEdit(Map<String, String> map) {
		
		StringBuffer sb = new StringBuffer();
		
		String content = map.remove("content");
		sb.append(map.remove("protocol") + " " + map.remove("statuscode") + " " + map.remove("status") + "\r\n");
		Set<String> keys = map.keySet();
		for(String key : keys) {
			sb.append(key + ": " + map.get(key) + "\r\n");
		}
		sb.append("\r\n\r\n");
		sb.append(content);
		
		return sb.toString();
	}
	
}
