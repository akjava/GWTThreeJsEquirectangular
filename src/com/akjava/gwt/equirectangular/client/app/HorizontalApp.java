package com.akjava.gwt.equirectangular.client.app;

import com.akjava.gwt.equirectangular.client.EquirectangularApp;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.TWEEN;
import com.akjava.gwt.three.client.examples.js.controls.TrackballControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.gwt.renderers.WebGLRendererParameter;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.CubeCamera;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.extras.ImageUtils;
import com.akjava.gwt.three.client.js.extras.geometries.PlaneBufferGeometry;
import com.akjava.gwt.three.client.js.lights.AmbientLight;
import com.akjava.gwt.three.client.js.lights.PointLight;
import com.akjava.gwt.three.client.js.lights.SpotLight;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Group;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.google.gwt.user.client.ui.FocusPanel;

public class HorizontalApp implements EquirectangularApp {

	private PerspectiveCamera camera;
	private Scene scene;


	private WebGLRenderer renderer;

	Vector3 target;

	private double tweenTime;
	

	
	


	private double FAR=10000;
	private boolean DAY;
	private AmbientLight ambientLight;
	private PointLight pointLight;
	private SpotLight sunLight;
	private TrackballControls controls;
	private JSParameter parameters;
	private TWEEN tweenNight;
	private double WIDTH,HEIGHT;
	

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
		parameters.set("control", 1.0);
		this.maxFrame=maxFrame;
		tweenTime=System.currentTimeMillis();
		if(tweenNight!=null){
			tweenNight.stop();
		}
		double fadeTime=(double)maxFrame/30*1000;
		tweenNight =  TWEEN.Tween( parameters ).to( "control",0, fadeTime ).easing( 
				TWEEN.Easing_Cubic_InOut()
				//TWEEN.Easing_Exponential_Out()
				);
		tweenNight.start();
	}
	@Override
	public void onWindowResize(double w,double h) {
		camera.setAspect(w / h);
		camera.updateProjectionMatrix();

		renderer.setSize( w , h );
	}
	public void init(FocusPanel focusPanel,double w,double h){
		this.WIDTH=w;
		this.HEIGHT=h;
			//create demo
			//
			
			

			Group group=THREE.Group();
			LogUtils.log(group);
			camera = THREE.PerspectiveCamera(45, WIDTH/HEIGHT, 2, FAR);
			group.add(camera);
			
			LogUtils.log(group);
			
			group.getPosition().set(500, 400, 1200 );

			
			scene = THREE.Scene();
			scene.setFog(THREE.Fog( 0x00aaff, 1000, FAR ));
			scene.add(group);
			
			

			renderer = THREE.WebGLRenderer(WebGLRendererParameter.create().antialias(true));//renderer = new THREE.WebGLRenderer();
			renderer.setPixelRatio( GWTThreeUtils.getWindowDevicePixelRatio() );//renderer.setPixelRatio( window.devicePixelRatio );
			renderer.setSize( WIDTH, HEIGHT );//renderer.setSize( window.innerWidth, window.innerHeight );
			focusPanel.getElement().appendChild(renderer.getDomElement());
			
			
			renderer.getShadowMap().setEnabled(true);//renderer.shadowMap.enabled = true;
			renderer.getShadowMap().setType(THREE.PCFSoftShadowMap);//renderer.shadowMap.type = THREE.PCFSoftShadowMap;
			
			renderer.setGammaInput(true);//renderer.gammaInput = true;
			renderer.setGammaOutput(true);//renderer.gammaOutput = true;
			
			
			
			cubeCamera = THREE.CubeCamera(2, FAR, 512);
			group.add(cubeCamera);
			
			//ground
			Texture textureSquares = ImageUtils.loadTexture("textures/patterns/bright_squares256.png");
			
			textureSquares.getRepeat().set(50, 50);
			textureSquares.setWrapS(THREE.RepeatWrapping);
			textureSquares.setWrapT(THREE.RepeatWrapping);
			textureSquares.setMagFilter(THREE.NearestFilter);
			textureSquares.setFormat(THREE.RGBFormat);
			
			MeshPhongMaterial groundMaterial = THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().shininess(80).color(0xffffff).specular(0xffffff)
					.map(textureSquares));
			PlaneBufferGeometry planeGeometry = THREE.PlaneBufferGeometry(100, 100);

			Mesh ground = THREE.Mesh(planeGeometry, groundMaterial);
			
			ground.getPosition().set(0, 0, 0);
			ground.getRotation().setX(-Math.PI / 2);
			ground.getScale().set(1000, 1000, 1000);

			ground.setReceiveShadow(true);

			scene.add(ground);
			
			
			
			//light
			double sunIntensity = 0.3;
			double pointIntensity = 1;

					if ( DAY ) {

					sunIntensity = 1;
					pointIntensity = 0.5;

					}

					ambientLight = THREE.AmbientLight( 0x3f2806 );//ambientLight = new THREE.AmbientLight( 0x3f2806 );
					scene.add( ambientLight );

					pointLight = THREE.PointLight( 0xffaa00, pointIntensity, 5000 );//pointLight = new THREE.PointLight( 0xffaa00, pointIntensity, 5000 );
					pointLight.getPosition().set( 0, 0, 0 );//pointLight.position.set( 0, 0, 0 );
					scene.add( pointLight );

					sunLight = THREE.SpotLight( 0xffffff, sunIntensity, 0, Math.PI/2, 1 );//sunLight = new THREE.SpotLight( 0xffffff, sunIntensity, 0, Math.PI/2, 1 );
					//sunLight.getPosition().set( 1000, 2000, 1000 );//sunLight.position.set( 1000, 2000, 1000 );
					sunLight.getPosition().set( 1000, 2000, 1000 );
					
					sunLight.setCastShadow(true);//sunLight.castShadow = true;

					sunLight.setShadowBias(-0.0002);//sunLight.shadowBias = -0.0002;

					sunLight.setShadowCameraNear(750);//sunLight.shadowCameraNear = 750;
					sunLight.setShadowCameraFar(4000);//sunLight.shadowCameraFar = 4000;
					sunLight.setShadowCameraFov(30);//sunLight.shadowCameraFov = 30;

					sunLight.setShadowCameraVisible(false);//sunLight.shadowCameraVisible = false;

					scene.add( sunLight );
			
			//control
					controls = THREEExp.TrackballControls( group, renderer.getDomElement());//controls = new THREE.TrackballControls( camera, renderer.domElement );
					controls.getTarget().set( 0, 120, 0 );//controls.target.set( 0, 120, 0 );

					controls.setRotateSpeed(1.0);//controls.rotateSpeed = 1.0;
					controls.setZoomSpeed(1.2);//controls.zoomSpeed = 1.2;
					controls.setPanSpeed(0.8);//controls.panSpeed = 0.8;

					controls.setNoZoom(false);//controls.noZoom = false;
					controls.setNoPan(false);//controls.noPan = false;

					controls.setStaticMoving(true);//controls.staticMoving = true;
					controls.setDynamicDampingFactor(0.15);//controls.dynamicDampingFactor = 0.15;

					controls.setKeys(JavaScriptUtils.toArray(new int[]{65, 83, 68 }));//controls.keys = [ 65, 83, 68 ];
			
			//make recorder
			
			parameters = JSParameter.createParameter().set("control", 1);

	}
	
	
	private CubeCamera cubeCamera;
	
	private boolean started;
	private int maxFrame;
	private int currentFrame;
	
public void animate(double t){
		
		controls.update();
		
		double control=parameters.getDouble("control");
		//LogUtils.log(control);
		scene.getFog().getColor().setHSL( 0.63, 0.05, control);//scene.fog.color.setHSL( 0.63, 0.05, parameters.control );
		renderer.setClearColor( scene.getFog().getColor());//renderer.setClearColor( scene.fog.color );

		double test=0.2;
		sunLight.setIntensity((control * 0.7 + 0.3)*test);//sunLight.intensity = parameters.control * 0.7 + 0.3;
		pointLight.setIntensity(- control * 0.5 + 1);//pointLight.intensity = - parameters.control * 0.5 + 1;

		pointLight.getColor().setHSL( 0.1, 0.75, control * 0.5 + 0.5 );//pointLight.color.setHSL( 0.1, 0.75, parameters.control * 0.5 + 0.5 );
		renderer.render(scene, camera);
		//testCubeCamera.up
		
		
		
		if(started && currentFrame<maxFrame){
			
			double frameTime=1000.0/30;//30fps fixed
			tweenTime+=frameTime;
			
			TWEEN.update(tweenTime);
			currentFrame++;
		}
		
	}

@Override
public void endExtract() {
	// TODO Auto-generated method stub
	
}
}
