package com.akjava.gwt.equirectangular.client;

import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.CubeCamera;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderTarget;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;

public class SixCubicImageExtractor implements EquirectangularImageExtractor{
	private int width;
	public int getWidth() {
		return width;
	}

	private int height;
	
	public SixCubicImageExtractor(int width, int height, CubeCamera cubeCamera) {
		super();
		this.width = width;
		this.height = height;
		this.cubeCamera = cubeCamera;
		
		target=THREE.WebGLRenderTarget(width, height);
		target.setGenerateMipmaps(false);
	}

	private WebGLRenderTarget target;
	private CubeCamera cubeCamera;
	
	//allow set server or store way
	public void update(WebGLRenderer renderer,Scene scene){
		if ( cubeCamera.getParent() == null ){
			//i'm not sure
			cubeCamera.updateMatrixWorld();
			}
		
		
		//create SixCubicImageDataUrl from cube camera
		PerspectiveCamera px=cubeCamera.getChildren().get(0).cast();
		renderer.render(scene, px, target, true);
		String pxDataUrl=target.gwtTextureToCanvas(renderer).toDataUrl();
		
		
		PerspectiveCamera nx=cubeCamera.getChildren().get(1).cast();
		renderer.render(scene, nx, target, true);
		String nxDataUrl=target.gwtTextureToCanvas(renderer).toDataUrl();

		PerspectiveCamera py=cubeCamera.getChildren().get(2).cast();
		renderer.render(scene, py, target, true);
		String pyDataUrl=target.gwtTextureToCanvas(renderer).toDataUrl();

		PerspectiveCamera ny=cubeCamera.getChildren().get(3).cast();
		renderer.render(scene, ny, target, true);
		String nyDataUrl=target.gwtTextureToCanvas(renderer).toDataUrl();

		PerspectiveCamera pz=cubeCamera.getChildren().get(4).cast();
		renderer.render(scene, pz, target, true);
		String pzDataUrl=target.gwtTextureToCanvas(renderer).toDataUrl();

		PerspectiveCamera nz=cubeCamera.getChildren().get(5).cast();
		renderer.render(scene, nz, target, true);
		String nzDataUrl=target.gwtTextureToCanvas(renderer).toDataUrl();


		imageDataUrls=new UploadImageDataUrls(pyDataUrl, nyDataUrl, pzDataUrl, nzDataUrl, pxDataUrl, nxDataUrl);
		
	}
	
	private UploadImageDataUrls imageDataUrls;

	public UploadImageDataUrls getImageDataUrls() {
		return imageDataUrls;
	}
}
