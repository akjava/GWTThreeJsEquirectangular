package com.akjava.gwt.equirectangular.client;

public class SingleFileServletSender implements ServletSender {
	@Override
	public void postToServlet(int size, int index, SixCubicImageDataUrl frame, PostListener listener) {
		SixCubeFrameIO.postToSingleFile(size,SixCubeFrameIO.toIndex(index)+".png",frame.getAll(),listener);
	}
}
