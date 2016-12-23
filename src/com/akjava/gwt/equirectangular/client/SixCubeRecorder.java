package com.akjava.gwt.equirectangular.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.math.Euler;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderTarget;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.google.gwt.core.client.JavaScriptObject;

//TODO make cubecamera version
public class SixCubeRecorder {
	private PerspectiveCamera camera;
	private int imageSize;//final image size is width(4xsize),height(2xsize)
	
	WebGLRenderTarget targetUp;
	WebGLRenderTarget targetDown;
	WebGLRenderTarget targetFront;
	WebGLRenderTarget targetBack;
	WebGLRenderTarget targetRight;
	WebGLRenderTarget targetLeft;
	public SixCubeRecorder(int size,PerspectiveCamera camera,int maxFrame) {
		super();
		this.imageSize=size;
		this.camera=camera;
		this.maxFrame = maxFrame;
		
		//create target
		JavaScriptObject options=null;//TODO here,size must be pow of 2,todo disable mipmap
		
		//todo merge single target
		targetUp=THREE.WebGLRenderTarget(size,size, options);
		targetDown=THREE.WebGLRenderTarget(size,size, options);
		targetFront=THREE.WebGLRenderTarget(size,size, options);
		targetBack=THREE.WebGLRenderTarget(size,size, options);
		targetRight=THREE.WebGLRenderTarget(size,size, options);
		targetLeft=THREE.WebGLRenderTarget(size,size, options);
		
		//LogUtils.log(size);
	}
	public int getImageSize() {
		return imageSize;
	}
	public boolean hasFrames(){
		return frames.size()>0;
	}
	
	
	private int maxFrame;
	
	private boolean recording;
	
	public boolean isRecording(){
		return recording;
	}

	public void start(){
		recording=true;

	}
	public void stop(){
		recording=false;
	}
	
	public void update(WebGLRenderer renderer,Scene scene){
		if(!recording){
			return;
		}
		
		if(maxFrame>0 && frames.size()>=maxFrame){
			return;
		}
		//TODO use cube camera
		
		Euler original=camera.getRotation().clone();
		Euler rotate=camera.getRotation();
		
		//front
		rotate.set(original.getX(), original.getY(), original.getZ()+Math.PI);//flip
		camera.updateMatrix();
		renderer.render(scene, camera, targetFront, true);
		
		//back
		rotate.set(original.getX(), original.getY()+Math.PI, original.getZ()+Math.PI);
		camera.updateMatrix();
		renderer.render(scene, camera, targetBack, true);
		
		
		rotate.set(original.getX(), original.getY()+Math.PI/2, original.getZ()+Math.PI);
		camera.updateMatrix();
		renderer.render(scene, camera, targetRight, true);
		
		rotate.set(original.getX(), original.getY()-Math.PI/2, original.getZ()+Math.PI);
		camera.updateMatrix();
		renderer.render(scene, camera, targetLeft, true);
		
		rotate.set(original.getX()+Math.PI/2, original.getY(), original.getZ()+Math.PI);
		camera.updateMatrix();
		renderer.render(scene, camera, targetUp, true);
		
		rotate.set(original.getX()-Math.PI/2, original.getY(), original.getZ()+Math.PI);
		camera.updateMatrix();
		renderer.render(scene, camera, targetDown, true);
		
		//TODO use single target
		
		//update all images
		String up=targetUp.gwtTextureToCanvas(renderer).toDataUrl();
		String down=targetDown.gwtTextureToCanvas(renderer).toDataUrl();
		String front=targetFront.gwtTextureToCanvas(renderer).toDataUrl();
		String back=targetBack.gwtTextureToCanvas(renderer).toDataUrl();
		String right=targetRight.gwtTextureToCanvas(renderer).toDataUrl();
		String left=targetLeft.gwtTextureToCanvas(renderer).toDataUrl();
		
		frames.add(new UploadImageDataUrls(up, down, front, back, right, left));
		
		camera.getRotation().copy(original);//reback
	}
	
	
	//i forget,found code in stackoverflow.com
	/*
	 * this way  work until three.js r71
	 * after that these error would happen
	 * WebGL: INVALID_FRAMEBUFFER_OPERATION: readPixels: no attachments
	 */
	public static final native String toDataUrl(JavaScriptObject gl, JavaScriptObject texture, int width, int height)/*-{
	  // Create a framebuffer backed by the texture
				    var framebuffer = gl.createFramebuffer();
				    gl.bindFramebuffer(gl.FRAMEBUFFER, framebuffer);
				    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_2D, texture, 0);

				    // Read the contents of the framebuffer
				    var data = new Uint8Array(width * height * 4);
				    gl.readPixels(0, 0, width, height, gl.RGBA, gl.UNSIGNED_BYTE, data);

				    gl.deleteFramebuffer(framebuffer);

				    // Create a 2D canvas to store the result 
				    var canvas = document.createElement('canvas');
				    canvas.width = width;
				    canvas.height = height;
				    var context = canvas.getContext('2d');

				    // Copy the pixels to a 2D canvas
				    var imageData = context.createImageData(width, height);
				    imageData.data.set(data);
				    context.putImageData(imageData, 0, 0);

				    return canvas.toDataURL();
	}-*/;
	private List<UploadImageDataUrls> frames=new ArrayList<UploadImageDataUrls>();
	
	public List<UploadImageDataUrls> getFrames() {
		return frames;
	}
	
	public void clear(){
		frames.clear();
	}


}
