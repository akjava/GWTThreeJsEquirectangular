package com.akjava.gwt.equirectangular.client;

public interface ServletSender {
	public  void postToServlet(int size,int index,SixCubicImageDataUrl frame,PostListener listener);
}
