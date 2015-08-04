package com.akjava.gwt.equirectangular.client;

import java.util.concurrent.TimeUnit;

import com.akjava.gwt.equirectangular.shared.Cube2Cyl;
import com.akjava.gwt.equirectangular.shared.Cube2Cyl.CUBE_COORD;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Stopwatch;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTSkyboxConvert implements EntryPoint {
	
	int r[]=new int[]{0,255,255,0,0,255};
	int g[]=new int[]{0,255,0,255,0,255};
	int b[]=new int[]{0,255,0,0,255,0};
	
	Skybox skybox;
	public void onModuleLoad() {
		
		ImageElementLoader loader=new ImageElementLoader();
		
		String imageName="interstellar_large.jpg";//"skybox_texture_small.jpg"
		
		loader.load(imageName, new ImageElementListener() {
			
			@Override
			public void onLoad(ImageElement element) {
				skybox=new Skybox(element);
				cube2cyl();
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				LogUtils.log("faild:"+url);
			}
		});
		
		
	}
	
	public void cube2cyl(){
		

		Cube2Cyl c=new Cube2Cyl();
		c.init(skybox.cubicSize, Math.PI, Math.PI*2);
		
		
		int outputW=c.pxPanoSizeH;
		int outputH=c.pxPanoSizeV;
		LogUtils.log(outputW+"x"+outputH);
		
		Stopwatch watch=Stopwatch.createStarted();
		c.genMap();
		LogUtils.log("genMap:"+watch.elapsed(TimeUnit.MILLISECONDS)+"ms");
		
		doit(outputW,outputH,c.map);
	}
	
	public void doit(int w,int h,CUBE_COORD[] map){
		
		
		
		
		
		Canvas canvas=CanvasUtils.createCanvas(w, h);
		
		ImageData data=canvas.getContext2d().getImageData(0, 0, w, h);
		
		ImageData[] faceDatas=new ImageData[6];
		
		faceDatas[0]=skybox.getUp().getContext2d().getImageData(0, 0, skybox.cubicSize, skybox.cubicSize);
		faceDatas[1]=skybox.getLeft().getContext2d().getImageData(0, 0, skybox.cubicSize, skybox.cubicSize);
		faceDatas[2]=skybox.getFront().getContext2d().getImageData(0, 0, skybox.cubicSize, skybox.cubicSize);
		faceDatas[3]=skybox.getRight().getContext2d().getImageData(0, 0, skybox.cubicSize, skybox.cubicSize);
		faceDatas[4]=skybox.getBack().getContext2d().getImageData(0, 0, skybox.cubicSize, skybox.cubicSize);
		faceDatas[5]=skybox.getDown().getContext2d().getImageData(0, 0, skybox.cubicSize, skybox.cubicSize);
		
		
		for(int i=0;i<map.length;i++){
			int x=i%w;
			int y=i/w;
			
			CUBE_COORD cdata=map[i];
			if(cdata==null){
				LogUtils.log("null:"+x+","+y);
			}
			int face=cdata.face.ordinal();
			
			
			/*
			int red=r[face];
			int green=g[face];
			int blue=b[face];
			*/
			int red=faceDatas[face].getRedAt((int)cdata.x, (int)cdata.y);
			int green=faceDatas[face].getGreenAt((int)cdata.x, (int)cdata.y);
			int blue=faceDatas[face].getBlueAt((int)cdata.x, (int)cdata.y);
			
			data.setRedAt(red, x, y);
			data.setGreenAt(green, x, y);
			data.setBlueAt(blue, x, y);
			data.setAlphaAt(255, x, y);
		}
		canvas.getContext2d().putImageData(data, 0, 0);
		
		RootPanel.get().add(canvas);
		
		LogUtils.log("done");
	}
}
