package com.akjava.gwt.equirectangular.client;

public class SixCubeServletSender implements ServletSender {

	@Override
	public void postToServlet(int size, int index, SixCubicImageDataUrl frame, PostListener listener) {
		SixCubeFrameIO.postToSixCube(size,SixCubeFrameIO.toIndex(index)+".png",frame.getAll(),listener);
	}

}
