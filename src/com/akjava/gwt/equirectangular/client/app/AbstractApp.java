package com.akjava.gwt.equirectangular.client.app;

import com.akjava.gwt.equirectangular.client.EquirectangularApp;
import com.akjava.gwt.three.client.js.cameras.CubeCamera;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;

public abstract class AbstractApp implements EquirectangularApp{
	protected WebGLRenderer renderer;
	protected Scene scene;
	protected CubeCamera cubeCamera;
	
	
	@Override
	public Scene getScene() {
		return scene;
	}

	@Override
	public WebGLRenderer getRenderer() {
		return renderer;
	}

	@Override
	public CubeCamera getCubeCamera() {
		return cubeCamera;
	}
	
	@Override
	public void startExtract(int maxFrame) {
		started=true;
		currentFrame=0;
		this.maxFrame=maxFrame;
		onStartExtract();
	}
	//default behavior
	protected void onStartExtract() {
		
	}

	private boolean started;
	private int maxFrame;
	private int currentFrame;

	protected boolean isRecording(){
		return started && currentFrame<maxFrame;
	}
}
