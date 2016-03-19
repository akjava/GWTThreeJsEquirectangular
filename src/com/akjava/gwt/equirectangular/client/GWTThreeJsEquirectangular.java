package com.akjava.gwt.equirectangular.client;

import java.io.IOException;

import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
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
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ValueListBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTThreeJsEquirectangular extends AbstractThreeApp implements EntryPoint {
	
	private PerspectiveCamera camera;
	private Scene scene;
	private DockLayoutPanel root;
	private Mesh mesh;
	private WebGLRenderer renderer;

	Vector3 target;
	private HorizontalPanel controlPanel;
	//private SixCubeRecorder recorder;
	private SixCubicImageExtractor extactor;
	//private int maxRecordFrameSize=30*60;
	private PerspectiveCamera exportCamera;

	//private int imageSize=1024;
	
	private boolean posting=false;
	private int currentFrameIndex=0;
	
	private double frameRate=30;
	private int duration=1;
	
	private double tweenTime;
	
	private double startTime;

	private int maxRecordFrameSize;
	public void onModuleLoad() {
		
		//should interface
			Window.addWindowClosingHandler(new ClosingHandler() {
				@Override
				public void onWindowClosing(ClosingEvent event) {
					if(isRecording()){
						event.setMessage("Still Recording.stop and quit this app?");
					}
					
					
				}
			});
		
		 	root=new DockLayoutPanel(Unit.PX);
		 
			RootLayoutPanel.get().add(root);
			
			controlPanel = new HorizontalPanel();
			root.addSouth(controlPanel, 40);
			
			
			Button bt=new Button("test cube camera",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doTest();
				}
			});
			//controlPanel.add(bt);
			
			final ValueListBox<Integer> sizeBox=new ValueListBox<Integer>(new Renderer<Integer>() {
				@Override
				public String render(Integer object) {
					if(object!=null){
						return String.valueOf(object);
					}
					return null;
				}

				@Override
				public void render(Integer object, Appendable appendable) throws IOException {
					
				}
			});
			sizeBox.setValue(512);
			sizeBox.setAcceptableValues(Lists.newArrayList(256,512,1024,2048));
			
			controlPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			controlPanel.add(new Label("image-size"));
			controlPanel.add(sizeBox);
			
			
			final ValueListBox<Integer> durationBox=new ValueListBox<Integer>(new Renderer<Integer>() {
				@Override
				public String render(Integer object) {
					if(object!=null){
						return String.valueOf(object);
					}
					return null;
				}

				@Override
				public void render(Integer object, Appendable appendable) throws IOException {
					
				}
			});
			durationBox.setValue(1);
			durationBox.setAcceptableValues(Lists.newArrayList(1,5,10,15,30,60,180));
			
			
			controlPanel.add(new Label("duration"));
			controlPanel.add(durationBox);
			
			executeButton = new ExecuteButton("Record Images",false) {
				
				@Override
				public void executeOnClick() {

					SixCubeFrameIO.callClearImages(new PostListener() {
						
						@Override
						public void onReceived(String response) {
							LogUtils.log(response);
							int size=sizeBox.getValue();
							
							tweenTime=System.currentTimeMillis();
							
							
							duration=durationBox.getValue();
							
							currentFrameIndex=0;//can try again?
							extactor=new SixCubicImageExtractor(size, size, testCubeCamera);
							maxRecordFrameSize=(int) (frameRate*duration);
							
							startTime=System.currentTimeMillis();
							/*
							Timer timer=new Timer(){

								@Override
								public void run() {
									if(recorder.getFrames().size()!=frameSize){//still converting
										return;
									}
									
									
									//downloadFile();
									//uploadNonaFile();//possible slow & stop
									uploadSixCubeFileAtOnce();
									
									cancel();
								}
								
							};
							timer.scheduleRepeating(100);//after 1 sec
							*/
							tweenNight.start(tweenTime);//fade
						}
						
						@Override
						public void onError(String message) {
							// TODO Auto-generated method stub
							LogUtils.log("error on clear image:"+message);
						}
					});
					
				
					
				}
			};
			
			controlPanel.add(executeButton);
			
			//start demo
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {	
			@Override
			public void execute() {
				start(root);
			}
		});
		

	}
	

	
	protected void doTest() {
		Canvas canvas=testCubeCamera.getRenderTarget().gwtTextureToCanvas(renderer);
		Window.open(canvas.toDataUrl(), "test", null);
	}



	private void uploadSixCubeFile(){
		int imageSize=extactor.getWidth();
		SixCubicImageDataUrl frame=extactor.getImageDataUrls();
		
		SixCubeFrameIO.postToSixCubeServlet(imageSize,currentFrameIndex, frame,new PostListener(){

			@Override
			public void onError(String message) {
				LogUtils.log("error:"+message);
			}

			@Override
			public void onReceived(String response) {
				LogUtils.log("received:"+response);
				posting=false;
				
				int maxRecordFrameSize=(int) (frameRate*duration);
				if(currentFrameIndex==maxRecordFrameSize){
					onRecordEnd();
				}
			}});//simple post
		currentFrameIndex++;
		posting=true;

		//SixCubeFrameIO.postTextData("I:\\Program Files\\Hugin\\bin\\nona.exe",512, frameSize);
		
		
	}
	
	private void onRecordEnd() {
		double time=System.currentTimeMillis()-startTime;
		LogUtils.log("onEnd:"+time+",size="+extactor.getWidth());
		executeButton.setEnabled(true);
	}


/*
	private void uploadSixCubeFileAtOnce(){
		int imageSize=extactor.getWidth();
		int maxRecordFrameSize=(int) (frameRate*duration);
		for(int i=0;i<maxRecordFrameSize;i++){
			SixCubicImageDataUrl frame=recorder.getFrames().get(i);
			SixCubeFrameIO.postToSixCubeServlet(imageSize,i, frame,null);//simple post
		}
		
		//SixCubeFrameIO.postTextData("I:\\Program Files\\Hugin\\bin\\nona.exe",512, frameSize);
	}
	*/
	/*
	private void uploadNonaFile(){
		int maxRecordFrameSize=(int) (frameRate*duration);
		for(int i=0;i<maxRecordFrameSize;i++){
			SixCubicImageDataUrl frame=recorder.getFrames().get(i);
			SixCubeFrameIO.postImageData(i+1, frame);//simple post
		}
		
		SixCubeFrameIO.postTextData("I:\\Program Files\\Hugin\\bin\\nona.exe",recorder.getImageSize(), maxRecordFrameSize);
	}
	*/
	/*
	private void downloadFile(){
		JSZip zip=SixCubeFrameIO.toZip(recorder.getFrames());
		
		NonaBatchGenerator generator=new NonaBatchGenerator("I:\\Program Files\\Hugin\\bin\\nona.exe",512);
		int maxRecordFrameSize=(int) (frameRate*duration);
		for(int i=1;i<=maxRecordFrameSize;i++){
			String b1=generator.createPto(i);
			zip.file(SixCubeFrameIO.toIndex(i)+".pto", b1);
		}
	
		
		
		String batch=generator.createBatch(maxRecordFrameSize);
		zip.file("create.bat", batch);
		
		Anchor anchor=HTML5Download.get().generateDownloadLink(zip.generateBlob(null), "application/zip", "test.zip", "download zip");
		controlPanel.add(anchor);
		
	}
	*/
	private double FAR=10000;
	private boolean DAY;
	private AmbientLight ambientLight;
	private PointLight pointLight;
	private SpotLight sunLight;
	private TrackballControls controls;
	private JSParameter parameters;
	private TWEEN tweenNight;
	public void init(){
			//create demo
			//
			FocusPanel focusPanel = new FocusPanel();
			GWTHTMLUtils.setBackgroundColor(focusPanel, "0xff0000");
			root.add(focusPanel);
			

			Group group=THREE.Group();
			LogUtils.log(group);
			camera = THREE.PerspectiveCamera(45, getWindowInnerWidth()/getWindowInnerHeight(), 2, FAR);
			group.add(camera);
			
			LogUtils.log(group);
			
			group.getPosition().set(500, 400, 1200 );

			
			scene = THREE.Scene();
			scene.setFog(THREE.Fog( 0x00aaff, 1000, FAR ));
			scene.add(group);
			
			

			renderer = THREE.WebGLRenderer(WebGLRendererParameter.create().antialias(true));//renderer = new THREE.WebGLRenderer();
			renderer.setPixelRatio( GWTThreeUtils.getWindowDevicePixelRatio() );//renderer.setPixelRatio( window.devicePixelRatio );
			renderer.setSize( getWindowInnerWidth(), getWindowInnerHeight() );//renderer.setSize( window.innerWidth, window.innerHeight );
			focusPanel.getElement().appendChild(renderer.getDomElement());
			
			
			renderer.getShadowMap().setEnabled(true);//renderer.shadowMap.enabled = true;
			renderer.getShadowMap().setType(THREE.PCFSoftShadowMap);//renderer.shadowMap.type = THREE.PCFSoftShadowMap;
			
			renderer.setGammaInput(true);//renderer.gammaInput = true;
			renderer.setGammaOutput(true);//renderer.gammaOutput = true;
			
			exportCamera = THREE.PerspectiveCamera( 90,1, 2, FAR );
			//exportCamera.getPosition().setY(-1);
			group.add(exportCamera);
			
			testCubeCamera = THREE.CubeCamera(2, FAR, 512);
			group.add(testCubeCamera);
			
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
			tweenNight =  TWEEN.Tween( parameters ).to( "control",0, fadeTime ).easing( 
					TWEEN.Easing_Cubic_InOut()
					//TWEEN.Easing_Exponential_Out()
					);
	}
	
	private double fadeTime=1000*60;
	private ExecuteButton executeButton;
	private CubeCamera testCubeCamera;
	
	@Override
	public void onWindowResize() {
		camera.setAspect(getWindowInnerWidth() / getWindowInnerHeight());
		camera.updateProjectionMatrix();

		renderer.setSize( (int)getWindowInnerWidth() , (int)getWindowInnerHeight() );
	}


	
	private boolean isRecording(){
		return extactor!=null && currentFrameIndex<maxRecordFrameSize;
	}
	
	public void animate(double time){
		if(posting){//still posting to servlet
			return;
		}
		
		
		
		controls.update();
		
		double control=parameters.getDouble("control");
		scene.getFog().getColor().setHSL( 0.63, 0.05, control);//scene.fog.color.setHSL( 0.63, 0.05, parameters.control );
		renderer.setClearColor( scene.getFog().getColor());//renderer.setClearColor( scene.fog.color );

		double test=0.2;
		sunLight.setIntensity((control * 0.7 + 0.3)*test);//sunLight.intensity = parameters.control * 0.7 + 0.3;
		pointLight.setIntensity(- control * 0.5 + 1);//pointLight.intensity = - parameters.control * 0.5 + 1;

		pointLight.getColor().setHSL( 0.1, 0.75, control * 0.5 + 0.5 );//pointLight.color.setHSL( 0.1, 0.75, parameters.control * 0.5 + 0.5 );

		
		//group.getRotation().setX(-Math.PI/2);
		
		//LogUtils.log(time);
		
		//testcube
		testCubeCamera.updateCubeMap(renderer, scene);
		
		renderer.render(scene, camera);
		//testCubeCamera.up
		
		
		
		if(isRecording()){
			extactor.update(renderer, scene);
			uploadSixCubeFile();
			
			double frameTime=fadeTime/maxRecordFrameSize;
			tweenTime+=frameTime;
			
			TWEEN.update(tweenTime);
		}
		
		
		//
	}
}
