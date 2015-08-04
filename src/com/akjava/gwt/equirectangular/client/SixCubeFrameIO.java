package com.akjava.gwt.equirectangular.client;


import java.util.List;

import com.akjava.gwt.equirectangular.client.GWTThreeJsEquirectangular.PostListener;
import com.akjava.gwt.equirectangular.client.SixCubeRecorder.SixCubeFrame;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class SixCubeFrameIO {

	public static final List<String> directions=ImmutableList.of("up","down","front","back","right","left");
	
	public static JSZip toZip(List<SixCubeFrame> frames){
		JSZip zip=JSZip.newJSZip();
		
		for(int i=0;i<frames.size();i++){
			String index=toIndex(i+1);
			SixCubeFrame frame=frames.get(i);
			
			zip.base64UrlFile(index+"_up"+".png", frame.getUp());
			zip.base64UrlFile(index+"_down"+".png", frame.getDown());
			zip.base64UrlFile(index+"_front"+".png", frame.getFront());
			zip.base64UrlFile(index+"_back"+".png", frame.getBack());
			
			zip.base64UrlFile(index+"_right"+".png", frame.getRight());
			zip.base64UrlFile(index+"_left"+".png", frame.getLeft());	
		}
		
		return zip;
	}
	
	
	public static String getFileName(int index,int direction){
		return toIndex(index)+"_"+directions.get(direction)+".png";
	}
	
	
	static String toIndex(int number){
		return Strings.padStart(String.valueOf(number), 5, '0');
	}
	
	
	public static void postImageData(int index,SixCubeFrame frame){
		simplePostToWrite(toIndex(index)+"_up"+".png", frame.getUp());
		simplePostToWrite(toIndex(index)+"_down"+".png", frame.getDown());
		simplePostToWrite(toIndex(index)+"_front"+".png", frame.getFront());
		simplePostToWrite(toIndex(index)+"_back"+".png", frame.getBack());
		
		simplePostToWrite(toIndex(index)+"_right"+".png", frame.getRight());
		simplePostToWrite(toIndex(index)+"_left"+".png", frame.getLeft());
	}
	public static void postTextData(String nonaPath,int imageSize,int size){
		NonaBatchGenerator generator=new NonaBatchGenerator(nonaPath, imageSize);
		for(int i=0;i<size;i++){
			simplePostToWrite(toIndex(i+1)+".pto",generator.createPto(i+1));
		}
		simplePostToWrite("nona_cubic2erect.bat",generator.createBatch(size));
	
		simplePostToWrite("ffmpeg_image2movie.bat",new FFMpegBatchGenerator("s:\\download\\ffmpeg2.7.1\\bin\\ffmpeg.exe", 24, "output.mp4").createBatch());
	}
	
	public static void postToSixCubeServlet(int index,SixCubeFrame frame,PostListener listener){
		postToSixCube(toIndex(index)+".png",frame.getAll(),listener);
	}
	//post to NonaCubi2ErectServlet
	public static void simplePostToWrite(final String fileName,String data){
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/write");// TODO
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		StringBuilder sb = new StringBuilder();
		sb.append("name").append("=").append(URL.encodeQueryString(fileName));
		sb.append("&");
		sb.append("data").append("=").append(URL.encodeQueryString(data));
		
		try{
			Request response  =builder.sendRequest(sb.toString(), new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					LogUtils.log(fileName+"="+response.getText());
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					LogUtils.log("error:"+fileName+"="+exception.getMessage());
				}
			});
		}catch(Exception e){}
	}
	
	public static void postToSixCube(final String fileName,List<String> datas,final PostListener listener){
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/sixcube");
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		StringBuilder sb = new StringBuilder();
		sb.append("name").append("=").append(URL.encodeQueryString(fileName));
		
		
		for(int i=1;i<=6;i++){
			sb.append("&");
			sb.append("image").append(i+"=").append(URL.encodeQueryString(datas.get(i-1)));
		}
		
		try{
			Request response  =builder.sendRequest(sb.toString(), new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(listener!=null)
					listener.onReceived(response.getText());
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					if(listener!=null)
					listener.onError(exception.getMessage());
				}
			});
		}catch(Exception e){}
	}
	//TODO create store system-io version
}
