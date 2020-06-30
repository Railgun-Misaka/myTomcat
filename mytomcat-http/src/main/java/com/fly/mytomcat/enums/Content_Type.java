package com.fly.mytomcat.enums;

public enum Content_Type {
	
	HTML("text/html"),
	TEXT("text/plain"),
	XML("text/xml"),
	GIF("image/gif"),
	JPG("image/jpeg"),
	PNG("image/png"),
	JS("text/plain"),
	CSS("text/plain");
	
	private String type;
	
	Content_Type(String type){
		this.setType(type);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
