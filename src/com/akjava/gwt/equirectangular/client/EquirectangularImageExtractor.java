package com.akjava.gwt.equirectangular.client;

import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;

public interface EquirectangularImageExtractor {
	public int getWidth();
	public UploadImageDataUrls getImageDataUrls();
	public void update(WebGLRenderer renderer,Scene scene);
}
