package com.akjava.gwt.equirectangular.client.page;

import com.akjava.gwt.equirectangular.client.AbstractThreeApp;
import com.akjava.gwt.equirectangular.client.NonaBatchGenerator;
import com.akjava.gwt.equirectangular.client.PostListener;
import com.akjava.gwt.equirectangular.client.SixCubeFrameIO;
import com.akjava.gwt.equirectangular.client.SixCubeRecorder;
import com.akjava.gwt.equirectangular.client.SixCubicImageDataUrl;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.jszip.client.JSZip;
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
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTThreeJsEquirectangularTurnSkybox extends AbstractThreeApp implements EntryPoint {
	
	private PerspectiveCamera camera;
	private Scene scene;
	private DockLayoutPanel root;
	private Mesh mesh;
	private WebGLRenderer renderer;

	Vector3 target;
	private HorizontalPanel controlPanel;
	private SixCubeRecorder recorder;
	private int maxRecordFrameSize=1;
	private PerspectiveCamera exportCamera;
	private Group group;
	
	private boolean posting=false;
	private int currentFrameIndex=0;
	public void onModuleLoad() {
		
		 	root=new DockLayoutPanel(Unit.PX);
		 
			RootLayoutPanel.get().add(root);
			
			controlPanel = new HorizontalPanel();
			root.addSouth(controlPanel, 40);
			
			//create buttons
			Button bt=new Button("test",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
					recorder.start();//wait timing
					
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
					
				}
			});
			controlPanel.add(bt);
			
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {	
			@Override
			public void execute() {
				
			start(root);
			}
		});
		

	}
	

	
	private void uploadSixCubeFile(){
		SixCubicImageDataUrl frame=recorder.getFrames().get(0);
		SixCubeFrameIO.postToSixCubeServlet(512,currentFrameIndex, frame,new PostListener(){

			@Override
			public void onError(String message) {
				LogUtils.log("error:"+message);
			}

			@Override
			public void onReceived(String response) {
				LogUtils.log("received:"+response);
				posting=false;
			}});//simple post
		currentFrameIndex++;
		posting=true;
		recorder.clear();
		//SixCubeFrameIO.postTextData("I:\\Program Files\\Hugin\\bin\\nona.exe",512, frameSize);
	}
	
	private void uploadSixCubeFileAtOnce(){
		for(int i=0;i<maxRecordFrameSize;i++){
			SixCubicImageDataUrl frame=recorder.getFrames().get(i);
			SixCubeFrameIO.postToSixCubeServlet(512,i, frame,null);//simple post
		}
		
		//SixCubeFrameIO.postTextData("I:\\Program Files\\Hugin\\bin\\nona.exe",512, frameSize);
	}
	
	private void uploadNonaFile(){
		for(int i=0;i<maxRecordFrameSize;i++){
			SixCubicImageDataUrl frame=recorder.getFrames().get(i);
			SixCubeFrameIO.postImageData(i+1, frame);//simple post
		}
		
		SixCubeFrameIO.postTextData("I:\\Program Files\\Hugin\\bin\\nona.exe",512, maxRecordFrameSize);
	}
	private void downloadFile(){
		JSZip zip=SixCubeFrameIO.toZip(recorder.getFrames());
		
		NonaBatchGenerator generator=new NonaBatchGenerator("I:\\Program Files\\Hugin\\bin\\nona.exe",512);
		
		for(int i=1;i<=maxRecordFrameSize;i++){
			String b1=generator.createPto(i);
			zip.file(SixCubeFrameIO.toIndex(i)+".pto", b1);
		}
	
		
		
		String batch=generator.createBatch(maxRecordFrameSize);
		zip.file("create.bat", batch);
		
		Anchor anchor=HTML5Download.get().generateDownloadLink(zip.generateBlob(null), "application/zip", "test.zip", "download zip");
		controlPanel.add(anchor);
		
	}
	
	public void init(){
	

			//create demo
			FocusPanel focusPanel = new FocusPanel();
			
			root.add(focusPanel);
			

			camera = THREE.PerspectiveCamera( 75, getWindowInnerWidth() / getWindowInnerHeight(), 1, 1100 );//camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 1, 1100 );
			target=THREE.Vector3( 0, 0, 0 );//camera.target = new THREE.Vector3( 0, 0, 0 );

			scene = THREE.Scene();//scene = new THREE.Scene();

			SphereGeometry geometry = THREE.SphereGeometry( 500, 60, 40 );//var geometry = new THREE.SphereGeometry( 500, 60, 40 );
			geometry.applyMatrix( THREE.Matrix4().makeScale( -1, 1, 1 ) );//geometry.applyMatrix( new THREE.Matrix4().makeScale( -1, 1, 1 ) );

			MeshBasicMaterial material = THREE.MeshBasicMaterial( GWTParamUtils.MeshBasicMaterial().map(ImageUtils.loadTexture( "textures/2294472375_24a3b8ef46_o.jpg" )));//var material = THREE.MeshBasicMaterial( {map() );//MeshBasicMaterial material = THREE.MeshBasicMaterial( {//var material = new THREE.MeshBasicMaterial( {map: THREE.ImageUtils.loadTexture( 'textures/2294472375_24a3b8ef46_o.jpg' )} );

			mesh = THREE.Mesh( geometry, material );//mesh = new THREE.Mesh( geometry, material );

			scene.add( mesh );

			renderer = THREE.WebGLRenderer();//renderer = new THREE.WebGLRenderer();
			renderer.setPixelRatio( GWTThreeUtils.getWindowDevicePixelRatio() );//renderer.setPixelRatio( window.devicePixelRatio );
			renderer.setSize( getWindowInnerWidth(), getWindowInnerHeight() );//renderer.setSize( window.innerWidth, window.innerHeight );
			focusPanel.getElement().appendChild(renderer.getDomElement());
			
			exportCamera = THREE.PerspectiveCamera( 90, 1, 1, 1100 );
			
			
			recorder = new SixCubeRecorder(512, exportCamera, maxRecordFrameSize);
			
			group = THREE.Group();
			
			group.add(camera);
			group.add(exportCamera);
			
			scene.add(group);
	}
	
	
	@Override
	public void onWindowResize() {
		camera.setAspect(getWindowInnerWidth() / getWindowInnerHeight());
		camera.updateProjectionMatrix();

		renderer.setSize( (int)getWindowInnerWidth() , (int)getWindowInnerHeight() );
	}


	
	public void animate(double time){
		if(posting){//still posting to servlet
			return;
		}
		//group.getRotation().setX(-Math.PI/2);
		
		double z=group.getRotation().getZ();
		group.getRotation().setZ(z+0.01);
		
		//LogUtils.log(time);
		renderer.render(scene, camera);
		
		if(currentFrameIndex<maxRecordFrameSize && recorder.isRecording()){
		recorder.update(renderer, scene);
		uploadSixCubeFile();
		}
		//
	}
}
