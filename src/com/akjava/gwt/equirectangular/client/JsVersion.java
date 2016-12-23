package com.akjava.gwt.equirectangular.client;

import com.akjava.gwt.equirectangular.client.app.CharacterApp;
import com.akjava.gwt.three.client.js.cameras.CubeCamera;
import com.google.gwt.core.client.EntryPoint;

/**
 * Test using CubemapToEquirectangular.js
 * @author aki
 *
 */
public class JsVersion extends GWTThreeJsEquirectangularBase {
	
	
	private EquirectangularApp app;


	@Override
	protected EquirectangularApp getEquirectangularApp() {
		if(app==null){
			//modify by yourself
			//app=new HorizontalApp();
			//app=new TurnSkyboxApp();
			//app=new HorizontalApp();
			app=new CharacterApp();
		}
		return app;
	}
	
	public EquirectangularImageExtractor getEquirectangularImageExtractor(int size,CubeCamera camera){
		return new SingleFileImageExtractor(size, camera);
	}
	
	@Override
	public ServletSender getServletSender(){
		return new SingleFileServletSender();
	}

}
