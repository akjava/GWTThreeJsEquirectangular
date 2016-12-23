package com.akjava.gwt.equirectangular.client;

import com.akjava.gwt.three.client.js.cameras.CubeCamera;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.google.gwt.canvas.client.Canvas;

public class SingleFileImageExtractor implements EquirectangularImageExtractor{
private CubeCamera cubeCamera;
private int width;
/**
 * cube-camera must be created with same width
 * 
 * size/width not working yet
 */
private CubemapToEquirectangular cubemapToEquirectangular;
	public SingleFileImageExtractor(int width, CubeCamera cubeCamera) {
		super();
		this.width = width;
		this.cubeCamera = cubeCamera;
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	//update first
	@Override
	public UploadImageDataUrls getImageDataUrls() {
		return new UploadImageDataUrls(lastDataUrl);
	}

	private String lastDataUrl;
	@Override
	public void update(WebGLRenderer renderer, Scene scene) {
		if(this.cubemapToEquirectangular==null){
			this.cubemapToEquirectangular=CubemapToEquirectangular.create(renderer);//init here
		}
		cubeCamera.updateCubeMap( renderer, scene );

		Canvas canvas=Canvas.wrap(cubemapToEquirectangular.convert(cubeCamera));
		lastDataUrl=canvas.toDataUrl();
	}

}
