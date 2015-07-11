package com.akjava.gwt.equirectangular.client;


import java.util.List;

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
		post(toIndex(index)+"_up"+".png", frame.getUp());
		post(toIndex(index)+"_down"+".png", frame.getDown());
		post(toIndex(index)+"_front"+".png", frame.getFront());
		post(toIndex(index)+"_back"+".png", frame.getBack());
		
		post(toIndex(index)+"_right"+".png", frame.getRight());
		post(toIndex(index)+"_left"+".png", frame.getLeft());
	}
	public static void postTextData(String nonaPath,int imageSize,int size){
		NonaBatchGenerator generator=new NonaBatchGenerator(nonaPath, imageSize);
		for(int i=0;i<size;i++){
			post(toIndex(i+1)+".pto",generator.createPto(i+1));
		}
		post("nona_cubic2erect.bat",generator.createBatch(size));
	
		post("ffmpeg_image2movie.bat",new FFMpegBatchGenerator("s:\\download\\ffmpeg2.7.1\\bin\\ffmpeg.exe", 24, "output.mp4").createBatch());
	}
	
	public static void post(final String fileName,String data){
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/write");//fixed TODO
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
	//TODO create store system-io version
}
