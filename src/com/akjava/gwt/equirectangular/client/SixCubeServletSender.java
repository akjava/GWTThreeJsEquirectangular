package com.akjava.gwt.equirectangular.client;


public class SixCubeServletSender implements ServletSender {

	@Override
	public void postToServlet(int size, int index, UploadImageDataUrls frame, PostListener listener) {
		
		SixCubeFrameIO.postToSixCube(size,SixCubeFrameIO.toIndex(index)+".png",frame.getAll(),listener);
	}

	@Override
	public String getClearPagePath() {
		return "sixcube";
	}

}
