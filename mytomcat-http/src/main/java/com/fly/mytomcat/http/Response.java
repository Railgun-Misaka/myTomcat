package com.fly.mytomcat.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.fly.mytomcat.enums.Content_Type;
import com.fly.mytomcat.util.HttpAnalysis;

public class Response {
	
	private String protocol = "HTTP/1.1";
	private String statuscode = "200";
	private String status = "OK";
	private String type = Content_Type.HTML.getType() + "; charset=utf-8";
	private String encoding ;
	private String charset = "utf-8" ;
	private String content = "";
	private final OutputStream os;	

	public Response(OutputStream os) {
		this.os = os;
	}

	public String getProtocol() {
		return protocol;
	}

	public OutputStream getOutputStream() {
		return os;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(Content_Type type) {
		this.type = type.getType();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void send(String content) {
		this.content = content;
		execute();
	}
	
	public void staticresourse(String path) {
		File file = new File(path);
		staticresourse(file);
	}
	
	public void staticresourse(File file) {
		try(FileInputStream fis = new FileInputStream(file)){
			StringBuffer filename = new StringBuffer(file.getName());
			filename.delete(0, filename.lastIndexOf(".")+1);
			type = Content_Type.valueOf(filename.toString().toUpperCase()).getType();
			execute(fis);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void execute() {

		Map<String, String> rmap = new HashMap<String, String>();
		editresponse(rmap);
		try {
			os.write(HttpAnalysis.responseEdit(rmap).replaceFirst("type", "Content-Type").getBytes("utf-8"));
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void editresponse(Map<String, String> rmap) {
		Class<? extends Response> clazz = this.getClass();
		Field[] fields = clazz.getDeclaredFields();
		String key = null;
		String value = null;
		for(Field field : fields) {
			field.setAccessible(true);
			key = field.getName();
			try {
				value = (String) clazz.getDeclaredField(key).get(this);
			} catch (Exception e) {
				//e.printStackTrace();
			} 
			if(value == null)
				continue ;
			rmap.put(field.getName(), value);
		}
		rmap.remove("os");
	}

	private void execute(FileInputStream fis) {
		byte[] b = new byte[1024];
		int len ;
		StringBuffer sb = new StringBuffer();
		sb.append(protocol + " ");
		sb.append(statuscode + " ");
		sb.append(status + "\r\n");
		sb.append("Content-Type: " + type + "\r\n\r\n");
		try {
			os.write(sb.toString().getBytes("utf-8"));
			while ((len=fis.read(b))!= -1) {
	            os.write(b,0,len);
	        }
			
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
