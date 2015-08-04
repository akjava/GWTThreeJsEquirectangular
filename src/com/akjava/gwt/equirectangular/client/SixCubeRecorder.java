package com.akjava.gwt.equirectangular.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.math.Euler;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderTarget;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.google.gwt.core.client.JavaScriptObject;

public class SixCubeRecorder {
	private PerspectiveCamera camera;
	private int size;
	
	WebGLRenderTarget targetUp;
	WebGLRenderTarget targetDown;
	WebGLRenderTarget targetFront;
	WebGLRenderTarget targetBack;
	WebGLRenderTarget targetRight;
	WebGLRenderTarget targetLeft;
	public SixCubeRecorder(int size,PerspectiveCamera camera,int maxFrame) {
		super();
		this.size=size;
		this.camera=camera;
		this.maxFrame = maxFrame;
		
		//create target
		JavaScriptObject options=null;//TODO here,size must be pow of 2,todo disable mipmap
		
		targetUp=THREE.WebGLRenderTarget(size,size, options);
		targetDown=THREE.WebGLRenderTarget(size,size, options);
		targetFront=THREE.WebGLRenderTarget(size,size, options);
		targetBack=THREE.WebGLRenderTarget(size,size, options);
		targetRight=THREE.WebGLRenderTarget(size,size, options);
		targetLeft=THREE.WebGLRenderTarget(size,size, options);
		
		//LogUtils.log(size);
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
		
		//update all images
		String up=toDataUrl(renderer.getContext(), targetUp.getWebglTexture(), size, size);
		String down=toDataUrl(renderer.getContext(), targetDown.getWebglTexture(), size, size);
		String front=toDataUrl(renderer.getContext(), targetFront.getWebglTexture(), size, size);
		String back=toDataUrl(renderer.getContext(), targetBack.getWebglTexture(), size, size);
		String right=toDataUrl(renderer.getContext(), targetRight.getWebglTexture(), size, size);
		String left=toDataUrl(renderer.getContext(), targetLeft.getWebglTexture(), size, size);
		
		frames.add(new SixCubeFrame(up, down, front, back, right, left));
		
		camera.getRotation().copy(original);//reback
	}
	
	
	//i forget,found code in stackoverflow.com
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
	private List<SixCubeFrame> frames=new ArrayList<SixCubeRecorder.SixCubeFrame>();
	
	public List<SixCubeFrame> getFrames() {
		return frames;
	}
	
	public void clear(){
		frames.clear();
	}

	public static class SixCubeFrame{
		List<String> all=new ArrayList<String>();
		public SixCubeFrame(String up, String down, String front, String back, String right, String left) {
			super();
			all.add(up);
			all.add(down);
			all.add(front);
			all.add(back);
			all.add(right);
			all.add(left);
		}
		
		public String getUp() {
			return all.get(0);
		}
		public String getDown() {
			return all.get(1);
		}
		public String getFront() {
			return all.get(2);
		}
		public String getBack() {
			return all.get(3);
		}
		public String getRight() {
			return all.get(4);
		}
		public String getLeft() {
			return all.get(5);
		}
		public List<String> getAll(){
			return all;
		}
		
		
	}
}
