package com.akjava.gwt.equirectangular.client;

import java.io.IOException;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.three.client.java.ui.AbstractThreeApp;
import com.akjava.gwt.three.client.js.cameras.CubeCamera;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ValueListBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public abstract class GWTThreeJsEquirectangularBase extends AbstractThreeApp implements EntryPoint {
	
	
	private DockLayoutPanel center;


	private HorizontalPanel controlPanel;
	//private SixCubeRecorder recorder;
	private EquirectangularImageExtractor extactor;
	
	//private int imageSize=1024;
	
	private boolean posting=false;
	private int currentFrameIndex=0;
	
	private double frameRate=30;
	private int duration=0;
	private int maxRecordFrameSize;
	private ExecuteButton executeButton;


	private CheckBox noPost;
	
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
		
			final DockLayoutPanel root=new DockLayoutPanel(Unit.PX);
		 
			RootLayoutPanel.get().add(root);
			
			controlPanel = new HorizontalPanel();
			root.addSouth(controlPanel, 40);
			
	
			
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
			durationBox.setValue(duration);
			durationBox.setAcceptableValues(Lists.newArrayList(0,1,5,10,15,30,60,180));
			
			
			controlPanel.add(new Label("duration"));
			controlPanel.add(durationBox);
			
			executeButton = new ExecuteButton("Record Images",false) {
				
				@Override
				public void executeOnClick() {
					
					SixCubeFrameIO.callClearImages(new PostListener() {
						
						@Override
						public void onReceived(String response) {
							//LogUtils.log(response);
							int size=sizeBox.getValue();
							
							
							
							duration=durationBox.getValue();
							
							currentFrameIndex=0;//can try again?
							extactor=getEquirectangularImageExtractor(size,getEquirectangularApp().getCubeCamera());
							maxRecordFrameSize=countMaxRecordFrameSize();
							
							getEquirectangularApp().startExtract(maxRecordFrameSize);
							
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
							noPost.setEnabled(true);
						}
						
						

						@Override
						public void onError(String message) {
							// TODO Auto-generated method stub
							LogUtils.log("error on clear image:"+message);
						}
					},getServletSender().getClearPagePath());
					
				
					
				}
			};
			
			controlPanel.add(executeButton);
			
			noPost = new CheckBox("no write file(just test on recording)");
			controlPanel.add(noPost);
			
			
			
			center=new DockLayoutPanel(Unit.PX);
			root.add(center);
			//start demo
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {	
			@Override
			public void execute() {
				
				start(center);
			}
		});
		
			addResizeHandler();

	}
	

	
	private int countMaxRecordFrameSize() {
		return duration==0?1:(int) (frameRate*duration);
	}



	private void uploadToServletSender(){
		int imageSize=extactor.getWidth();
		UploadImageDataUrls frame=extactor.getImageDataUrls();
		
		getServletSender().postToServlet(imageSize,currentFrameIndex, frame,new PostListener(){

			@Override
			public void onError(String message) {
				LogUtils.log("error:"+message);
			}

			@Override
			public void onReceived(String response) {
				LogUtils.log("received:"+response);
				posting=false;
				
				int maxRecordFrameSize=countMaxRecordFrameSize();
				if(currentFrameIndex==maxRecordFrameSize){
					onRecordEnd();
				}
			}});//simple post
		
		posting=true;
	}
	
	public EquirectangularImageExtractor getEquirectangularImageExtractor(int size,CubeCamera camera){
		LogUtils.log("SixCubicImageExtractor called");
		return new SixCubicImageExtractor(size, size, camera);
	}
	
	public ServletSender getServletSender(){
		LogUtils.log("SixCubeServletSender called");
		return new SixCubeServletSender();
	}
	
	private void onRecordEnd() {
		executeButton.setEnabled(true);
	}



	protected abstract EquirectangularApp getEquirectangularApp();
	

	


	
	private boolean isRecording(){
		return extactor!=null && currentFrameIndex<maxRecordFrameSize;
	}
	
	//TODO make mthod
	private int perRecord=1;
	public int getPerRecord() {
		return perRecord;
	}



	public void setPerRecord(int perRecord) {
		this.perRecord = perRecord;
	}

	private int current=1;
	public void animate(double time){
		if(posting){//still posting to servlet
			if(current<perRecord){
				getEquirectangularApp().animate(time);//bg-update
				current++;
				return;
			}
			return;
		}
		
		
		getEquirectangularApp().animate(time);
		
		if(isRecording()){
			if(!noPost.getValue()){
			if(current<perRecord){
				current++;
				return;
			}else{
				current=1;
			}
			extactor.update(getEquirectangularApp().getRenderer(), getEquirectangularApp().getScene());
			uploadToServletSender();
			currentFrameIndex++;
			}else{
				currentFrameIndex++;
				if(currentFrameIndex==maxRecordFrameSize){
					onRecordEnd();
				}
			}
			
		}
	}







	@Override
	public void init() {
		FocusPanel focusPanel = new FocusPanel();
		getParent().add(focusPanel);
		
		getEquirectangularApp().init(focusPanel,getWindowInnerWidth(),getWindowInnerHeight());
	}







	@Override
	public void onWindowResize() {
		getEquirectangularApp().onWindowResize(getWindowInnerWidth(),getWindowInnerHeight());
	}
}
