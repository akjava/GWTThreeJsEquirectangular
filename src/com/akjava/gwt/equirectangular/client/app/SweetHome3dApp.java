package com.akjava.gwt.equirectangular.client.app;

import java.util.List;

import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.core.Object3D;
import com.akjava.gwt.three.client.js.lights.AmbientLight;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.loaders.ObjectLoader;
import com.akjava.gwt.three.client.js.loaders.ObjectLoader.ObjectLoadHandler;
import com.akjava.gwt.three.client.js.objects.Group;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SweetHome3dApp extends AbstractEquirectangularApp {
	
	private PerspectiveCamera camera;
	

	private Mesh mesh;

	
	
	private Group group;
	
	
	
	public void init(FocusPanel focusPanel,double w,double h){
		camera = THREE.PerspectiveCamera(75, w / h, 1, 1100);
		
		scene = THREE.Scene();
		
		AmbientLight ambientLight=THREE.AmbientLight(0x888888);
		scene.add(ambientLight);
		
		DirectionalLight dlight=THREE.DirectionalLight(0x888888);//TODO fix light
		dlight.getPosition().set(-1, 1, -1).normalize();
		
		
		ObjectLoader loader = THREE.ObjectLoader();//var loader = new THREE.ObjectLoader();
		
		loader.load("sweethome/userguideexamplefixed.json",new ObjectLoadHandler() {
			@Override
			public void onLoad(Object3D object) {
				List<Object3D> childrens=JavaScriptUtils.toList(object.getChildren());
				Group group=THREE.Group();
				for(Object3D obj:childrens){
					object.remove(obj);
					group.add(obj);
				}
				scene.add(group);
			}
		});
		
		//JavaScriptUtils.toList(array)

		// make renderer
		renderer = THREE.WebGLRenderer();
		renderer.setPixelRatio(GWTThreeUtils.getWindowDevicePixelRatio());
		renderer.setSize(w, h);
		focusPanel.getElement().appendChild(renderer.getDomElement());

		// resolution is dummy
		//cubeCamera = THREE.CubeCamera(1, 1100, 512);
		cubeCamera=THREE.CubeCamera(.001, 10000, 2048);
		group = THREE.Group();

		group.add(camera);
		group.add(cubeCamera);

		scene.add(group);
		
		group.getPosition().set( -226, 100, -340 );
	}
	
	
	@Override
	public void onWindowResize(double w,double h) {
		camera.setAspect(w / h);
		camera.updateProjectionMatrix();

		renderer.setSize( w , h);
	}

	
	

	
	public void animate(double t){
		renderer.render(scene, camera);
	}

}
