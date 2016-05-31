package com.akjava.gwt.equirectangular.client.app;

import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.Water;
import com.akjava.gwt.three.client.examples.js.controls.OrbitControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.extras.Shader;
import com.akjava.gwt.three.client.java.Skybox;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.AnimationMixer;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.extras.geometries.IcosahedronGeometry;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.lights.HemisphereLight;
import com.akjava.gwt.three.client.js.loaders.ImageLoader;
import com.akjava.gwt.three.client.js.loaders.ImageLoader.ImageLoadHandler;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.materials.ShaderMaterial;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.renderers.shaders.WebGLShaders.ShaderChunk.ShaderLib;
import com.akjava.gwt.three.client.js.textures.CubeTexture;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * @deprecated move to GWT360VRCreator
 * @author aki
 *
 */
public class CharacterApp extends AbstractEquirectangularApp{




	private PerspectiveCamera camera;
	
	double WIDTH;
	double HEIGHT;

	
	int parametersWidth=2000;
	int parametersHeight=2000;
	private OrbitControls controls;
	private Texture waterNormals;
	private Water water;
	private Mesh mirrorMesh;
	private Mesh sphere;
	private CubeTexture cubeMap;
	
	@Override
	public void init(FocusPanel container,double w,double h) {
	
		 WIDTH = w;
		 HEIGHT = h;

		// camera
		int VIEW_ANGLE = 55;
		double ASPECT = WIDTH/HEIGHT;
		double NEAR = 0.99;
		double FAR = 3000000;//3000000
		
		
		// renderer
		renderer = THREE.WebGLRenderer();//renderer = new THREE.WebGLRenderer();
		
		//somehow make a space
		renderer.setClearColor(0xd7cbb0);//same color as skybox
		
		renderer.setPixelRatio( GWTThreeUtils.getWindowDevicePixelRatio() );//renderer.setPixelRatio( window.devicePixelRatio );
		renderer.setSize( WIDTH, HEIGHT );

		// scene
		scene = THREE.Scene();//scene = new THREE.Scene();
	
		scene.add(THREE.AmbientLight( 0x444444 ));
		
		DirectionalLight directionalLight = THREE.DirectionalLight( 0x444444 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight.getPosition().set( -1, 1, 1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight );
		
		DirectionalLight directionalLight2 = THREE.DirectionalLight( 0x444444 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight2.getPosition().set( 1, 1, -1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight2 );
		
		DirectionalLight directionalLight3 = THREE.DirectionalLight( 0x444444 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight3.getPosition().set( 1, -1, 1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight3 );
		
		// camera
		camera = THREE.PerspectiveCamera(VIEW_ANGLE, ASPECT, NEAR, FAR);//camera = new THREE.PerspectiveCamera(VIEW_ANGLE, ASPECT, NEAR, FAR);
		camera.getPosition().set( 0, 0, 0 );

		
		
		cubeCamera=THREE.CubeCamera(NEAR, FAR, 512);
		cubeCamera.setPosition(0, 500, 0 );
		//cubeCamera.getRotation().setX(Math.toRadians(-90));
		
		container.getElement().appendChild( renderer.getDomElement() );
		
		//orbit controls only works on Camera
		controls = THREEExp.OrbitControls( camera, container.getElement());//controls = new THREE.OrbitControls( camera, renderer.domElement );
		
		controls.update();
		//controls.setTarget(THREE.Vector3( 0, 0, 0 ));
		//controls.setUserPan(false);//controls.userPan = false;
		//controls.setUserPanSpeed(0.0);//controls.userPanSpeed = 0.0;
	//	controls.setMaxDistance(5000.0);//controls.maxDistance = 5000.0;
	//	controls.setMaxPolarAngle(Math.PI * 0.495);//controls.maxPolarAngle = Math.PI * 0.495;
		controls.getCenter().set( 0, 500, 0 );//controls.center.set( 0, 500, 0 );

		
		final Texture texture=THREE.TextureLoader().load("models/texture.png");
		final Texture texture2=THREE.TextureLoader().load("models/texture2.png");
		final Texture texture3=THREE.TextureLoader().load("models/texture3.png");
		THREE.JSONLoader().load("models/merged3.json", new JSONLoadHandler() {
			
			

			

			

			@Override
			public void loaded(Geometry geometry, JsArray<Material> materials) {
				SkinnedMesh mesh=THREE.SkinnedMesh(geometry, THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().map(texture)
						.skinning(true)
						.morphTargets(true)
						));
				
				mesh.getScale().setScalar(1000);
				mesh.setPosition(0,-500, -200);
				controls.getTarget().set(0,-500,-400);
				scene.add(mesh);
				
				
				mixer=THREE.AnimationMixer(mesh);
				
				SkinnedMesh mesh2=THREE.SkinnedMesh(geometry, THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().map(texture2)
						.skinning(true)
						.morphTargets(true)
						));
				
				mesh2.getScale().setScalar(1000);
				mesh2.setPosition(0,-500, 400);
				mesh2.getRotation().setY(Math.toRadians(180));
				scene.add(mesh2);
				mixer2=THREE.AnimationMixer(mesh2);
				
				SkinnedMesh mesh3=THREE.SkinnedMesh(geometry, THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().map(texture3)
						.skinning(true)
						.morphTargets(true)
						));
				
				mesh3.getScale().setScalar(1000);
				mesh3.setPosition(-800,-500, 0);
				mesh3.getRotation().setY(Math.toRadians(90));
				scene.add(mesh3);
				mixer3=THREE.AnimationMixer(mesh3);
				
				THREE.XHRLoader().load("models/animation2.json", new XHRLoadHandler() {
					
					@Override
					public void onLoad(String text) {
						JSONValue value=JSONParser.parseStrict(text);
						AnimationClip clip=AnimationClip.parse(value.isObject().getJavaScriptObject());
						
						mixer.clipAction(clip).play();
						mixer2.clipAction(clip).startAt(1).play();
						mixer3.clipAction(clip).startAt(2).play();
					}
				});
				
			}
		});
		
		
		//stats
		
		
		fillScene();
		
		
		
	}
	private AnimationMixer mixer3;
	private AnimationMixer mixer;
	private AnimationMixer mixer2;
	public void fillScene(){
		
		HemisphereLight light = THREE.HemisphereLight( 0xffffbb, 0x080820, 1 );//var light = new THREE.HemisphereLight( 0xffffbb, 0x080820, 1 );
		light.getPosition().set( - 1, 1, - 1 );//light.position.set( - 1, 1, - 1 );
		scene.add( light );
		
//waternormals
		
		
	
		
				
				
				cubeMap = THREE.CubeTexture();
				cubeMap.setFormat(THREE.RGBFormat);//cubeMap.format = THREE.RGBFormat;
				cubeMap.setFlipY(false);//cubeMap.flipY = false;

				ImageLoader loader = THREE.ImageLoader();//var loader = new THREE.ImageLoader();
				loader.load( "textures/skyboxsun25degtest.png", new ImageLoadHandler() {
					
					@Override
					public void onProgress(NativeEvent progress) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoad(ImageElement imageElement) {
						Skybox skybox=new Skybox(imageElement);
						skybox.setToCubeTexture(cubeMap);
						
					}
					
					@Override
					public void onError(NativeEvent error) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
				
			
				Shader cubeShader = ShaderLib.cube();
				cubeShader.uniforms().set("tCube",cubeMap);//cubeShader.uniforms['tCube'].value = cubeMap;

				ShaderMaterial skyBoxMaterial = THREE.ShaderMaterial( GWTParamUtils.ShaderMaterial().fragmentShader(cubeShader.fragmentShader()).vertexShader(cubeShader.vertexShader()).uniforms(cubeShader.uniforms()).depthWrite(false).side(THREE.BackSide));//var skyBoxMaterial = new THREE.ShaderMaterial( {fragmentShader: cubeShader.fragmentShader,vertexShader: cubeShader.vertexShader,uniforms: cubeShader.uniforms,depthWrite: false,side: THREE.BackSide});
				Mesh skyBox = THREE.Mesh(//var skyBox = new THREE.Mesh(
						THREE.BoxGeometry( 1000000, 1000000, 1000000 ),//new THREE.BoxGeometry( 1000000, 1000000, 1000000 ),
						skyBoxMaterial
						);

						scene.add( skyBox );

					
						IcosahedronGeometry geometry = THREE.IcosahedronGeometry( 400, 4 );//var geometry = new THREE.IcosahedronGeometry( 400, 4 );

						for ( int i = 0, j = geometry.getFaces().length(); i < j; i ++ ) {//for ( var i = 0, j = geometry.faces.length; i < j; i ++ ) {

						geometry.getFaces().get( i ).getColor().setHex( (int) (Math.random() * 0xffffff) );//geometry.faces[ i ].color.setHex( Math.random() * 0xffffff );

						}
					


						MeshPhongMaterial material = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().vertexColors(THREE.FaceColors).shininess(100).envMap(cubeMap) );//var material = new THREE.MeshPhongMaterial( {vertexColors: THREE.FaceColors,shininess: 100,envMap: cubeMap} );

						sphere = THREE.Mesh( geometry, material );//sphere = new THREE.Mesh( geometry, material );
						//scene.add( sphere );
					
	}

	//forced convert canvas as imageelement
	public   native final ImageElement getSide(ImageElement image,int x,int y)/*-{
				var size = 1024;

						var canvas = document.createElement( 'canvas' );
						canvas.width = size;
						canvas.height = size;

						var context = canvas.getContext( '2d' );
						context.drawImage( image, - x * size, - y * size );

						return canvas;
	}-*/;




	
	
	public void onWindowResize(double w,double h) {
		
		WIDTH = w;
		HEIGHT = h;
		camera.setAspect(WIDTH / HEIGHT);
		camera.updateProjectionMatrix();

		renderer.setSize( WIDTH , HEIGHT );
		
	}
	
	public void animate() {//GWT animateFrame has time
		
		controls.update();
		
		//ThreeLog.log(camera.getPosition());
		
		renderer.render(scene, camera);
		if(mixer!=null){
			mixer.update(1.0/30);//fix
			mixer2.update(1.0/30);//fix
			mixer3.update(1.0/30);//fix
		}
	}


}
