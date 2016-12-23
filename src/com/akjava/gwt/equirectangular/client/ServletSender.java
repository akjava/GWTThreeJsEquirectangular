package com.akjava.gwt.equirectangular.client;

public interface ServletSender {
	public  void postToServlet(int size,int index,UploadImageDataUrls frame,PostListener listener);
	public String getClearPagePath();
}
