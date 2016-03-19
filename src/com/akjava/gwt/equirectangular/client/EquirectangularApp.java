package com.akjava.gwt.equirectangular.client;

import com.akjava.gwt.three.client.js.cameras.CubeCamera;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.google.gwt.user.client.ui.FocusPanel;

public interface EquirectangularApp {

	
	
	/**
	 * called first
	 */
	public  void init(FocusPanel panel,double w,double h);
	
	/**
	 * called after init();
	 * @return
	 */
	public  Scene getScene();
	public  WebGLRenderer getRenderer();
	public  CubeCamera getCubeCamera();
	
	
	/*
	 * called when recording start.
	 * usually for reset
	 */
	public  void onStartExtract(int maxFrame);
	
	
	/*
	 * every frame action,millisecond
	 */
	public  void update();
	
	
	public void onWindowResize(double w,double h);
	
}
