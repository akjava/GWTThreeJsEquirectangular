package com.akjava.gwt.equirectangular.client.app;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.extras.ImageUtils;
import com.akjava.gwt.three.client.js.extras.geometries.SphereGeometry;
import com.akjava.gwt.three.client.js.materials.MeshBasicMaterial;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Group;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TurnSkyboxApp extends AbstractEquirectangularApp {
	
	private PerspectiveCamera camera;
	

	private Mesh mesh;

	Vector3 target;
	
	
	private Group group;
	
	
	
	public void init(FocusPanel focusPanel,double w,double h){
	

		LogUtils.log(w+"x"+h);
		
			

			camera = THREE.PerspectiveCamera( 75, w / h, 1, 1100 );//camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 1, 1100 );
			target=THREE.Vector3( 0, 0, 0 );//camera.target = new THREE.Vector3( 0, 0, 0 );

			scene = THREE.Scene();//scene = new THREE.Scene();

			SphereGeometry geometry = THREE.SphereGeometry( 500, 60, 40 );//var geometry = new THREE.SphereGeometry( 500, 60, 40 );
			geometry.applyMatrix( THREE.Matrix4().makeScale( -1, 1, 1 ) );//geometry.applyMatrix( new THREE.Matrix4().makeScale( -1, 1, 1 ) );

			MeshBasicMaterial material = THREE.MeshBasicMaterial( GWTParamUtils.MeshBasicMaterial().map(ImageUtils.loadTexture( "textures/2294472375_24a3b8ef46_o.jpg" )));//var material = THREE.MeshBasicMaterial( {map() );//MeshBasicMaterial material = THREE.MeshBasicMaterial( {//var material = new THREE.MeshBasicMaterial( {map: THREE.ImageUtils.loadTexture( 'textures/2294472375_24a3b8ef46_o.jpg' )} );

			mesh = THREE.Mesh( geometry, material );//mesh = new THREE.Mesh( geometry, material );

			scene.add( mesh );

			renderer = THREE.WebGLRenderer();//renderer = new THREE.WebGLRenderer();
			renderer.setPixelRatio( GWTThreeUtils.getWindowDevicePixelRatio() );//renderer.setPixelRatio( window.devicePixelRatio );
			renderer.setSize( w, h );//renderer.setSize( window.innerWidth, window.innerHeight );
			focusPanel.getElement().appendChild(renderer.getDomElement());
			
			//resolution is dummy
			cubeCamera = THREE.CubeCamera(1, 1100, 512);
			
			
			
			group = THREE.Group();
			
			group.add(camera);
			group.add(cubeCamera);
			
			scene.add(group);
	}
	
	
	@Override
	public void onWindowResize(double w,double h) {
		camera.setAspect(w / h);
		camera.updateProjectionMatrix();

		renderer.setSize( w , h);
	}

	
	

	
	public void animate(double t){
		//LogUtils.log("update");
		//group.getRotation().setX(-Math.PI/2);
		
		double z=group.getRotation().getZ();
		group.getRotation().setZ(z+0.01);
		
		//LogUtils.log(time);
		renderer.render(scene, camera);
		
		if(isRecording()){
			//do something special
		}
		
	}

}
