package com.akjava.gwt.equirectangular.client;

import com.akjava.gwt.three.client.js.cameras.CubeCamera;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;

/*
 * must include modified CubemapToEquirectangular.js
 */
public class CubemapToEquirectangular extends JavaScriptObject{
protected CubemapToEquirectangular(){}

//only support unmanaged version
public final static native CubemapToEquirectangular create(WebGLRenderer renderer)/*-{
	return new $wnd.CubemapToEquirectangular( renderer, false );
}-*/;


public final native CanvasElement convert(CubeCamera cubeCamera)/*-{
return this.convert(cubeCamera);
}-*/;

}

