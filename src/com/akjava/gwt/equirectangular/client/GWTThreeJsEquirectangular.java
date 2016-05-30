package com.akjava.gwt.equirectangular.client;

import com.akjava.gwt.equirectangular.client.app.CharacterApp;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTThreeJsEquirectangular extends GWTThreeJsEquirectangularBase {
	
	
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
}
