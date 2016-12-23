package com.akjava.gwt.equirectangular.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.akjava.gwt.equirectangular.server.external.com.jhlabs.image.GaussianFilter;
import com.akjava.gwt.equirectangular.server.external.com.jhlabs.image.UnsharpFilter;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Stopwatch;
import com.google.common.io.BaseEncoding;

public class SixCubicToEquirectangularServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	  
	}
	
	private boolean clearImages(){
		File file=new File(getBaseDirectory());
		if(!file.exists()){
			System.out.println("SixCubicToEquirectangularServlet-faild clear images:not exist:"+getBaseDirectory());
			return false;
		}

		String[] names=file.list();
		for(String name:names){
			if(name.toLowerCase().endsWith(".png")){
				new File(file,name).delete();
			}
		}
		
		
		return true;
	}
	
	private String getBaseDirectory(){
		String directory=getInitParameter("directory");
		String fileSeparator=System.getProperty("file.separator");
		if(directory==null){
			directory=System.getProperty("user.home")+fileSeparator+"gwtthreejsequirectangular";
		}
		
		if(!directory.endsWith(fileSeparator)){
			//TODO check endless contnue "\\\\\\\"
			directory=directory+fileSeparator;
		}
		return directory;
	}
	

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		//image clear command
		String command=req.getParameter("command");
		if(command!=null && command.equals("clear")){
			boolean result=clearImages();
			resp.getWriter().println("clearImages:"+result);
			return;
		}
		
		
		
		Stopwatch totalWatch=Stopwatch.createStarted();
		//just send filename & data & simply write it
		final String name=req.getParameter("name");
		if(name==null){
			resp.sendError(500,"no name");
			return;
		}
		
		//image send by base64 because get from canvas.toDataUrl();
		
		List<BufferedImage> images=new ArrayList<BufferedImage>();
		for(int i=0;i<6;i++){
			images.add(null);
		}
		
		long decode=0;
		long makeImage=0;
		for(int i=1;i<=6;i++){
			String data=req.getParameter("image"+i);
			
			if(data==null){
				resp.sendError(500,"no image"+i);
				return;
			}
			byte[] bytes=null;
			
			if(data.startsWith("data:")){
				//TODO more check
				int index=data.indexOf(";base64,");
				if(index==-1){
					resp.sendError(500,"not base64");
					return;
				}
				Stopwatch decodeWatch=Stopwatch.createStarted();
				
				bytes=BaseEncoding.base64().decode(data.substring(index+";base64,".length()));
				decode+=decodeWatch.elapsed(TimeUnit.MILLISECONDS);
				Stopwatch makeImageWatch=Stopwatch.createStarted();
				BufferedImage inputImage=ImageIO.read(
						//new BufferedInputStream( //I'm not sure improve speed
						new ByteArrayInputStream(bytes)
						//,1024*1024)
						);
				makeImage+=makeImageWatch.elapsed(TimeUnit.MILLISECONDS);
				/*
				 * from "up","down","front","back","right","left"
 CUBE_TOP,
CUBE_LEFT,
CUBE_FRONT,
CUBE_RIGHT,
CUBE_BACK,
CUBE_DOWN,
				 */
				
				if(i==1){
					images.set(0, inputImage);
				}else if(i==2){
					images.set(5, inputImage);
				}else if(i==3){
					images.set(2, inputImage);
				}else if(i==4){
					images.set(4, inputImage);
				}else if(i==5){
					images.set(3, inputImage);
				}else if(i==6){
					images.set(1, inputImage);
				}
				
				//images.add(inputImage);
			}else{
				resp.sendError(500,"not start with data: not data-url images"+i);
				return;
			}
		}
		
		//check image-size all-same,
		int size=512;
		String sizeString=req.getParameter("size");
		if(sizeString!=null){
			int tmpSize=ValuesUtils.toInt(sizeString, 0);
			if(tmpSize!=0){
				size=tmpSize;
			}else{
				System.out.println("invalid size-request");
			}
		}
		
		SixCubicToEquirectangler converter=new SixCubicToEquirectangler(size*4);
		
		if(lastSize!=size){
			SixCubicToEquirectangler.map=null;
		}
		
		//TODO get from request
		final BufferedImage outputImage=converter.convertImage(images);
		BufferedImage tmp1=new GaussianFilter(6).filter(outputImage,null);
		UnsharpFilter unsharp=new UnsharpFilter();
		unsharp.setAmount(0.5f);
		unsharp.setRadius(4);
		BufferedImage tmp2=unsharp.filter(tmp1,null);
		
		final BufferedImage finalImage=tmp2;
		
		
		lastSize=size;
		
		
		
		long total=totalWatch.elapsed(TimeUnit.MILLISECONDS);
		//long write=writeWatch.elapsed(TimeUnit.MILLISECONDS);
		new Timer().schedule(new TimerTask() {
			//writing consume half time,now do it background
			@Override
			public void run() {
				File file=new File(getBaseDirectory()+name);
				try {
					ImageIO.write(finalImage, "png", file);
					//ImageIO.write(outputImage, "png", file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 10);
		resp.getWriter().println("SixCubicToEquirectangularServlet ok:create-time(ms)="+total+",genmap="+converter.genMapTime+",decode="+decode+",makeImage="+makeImage);
		
		//resp.getWriter().println("ok:create-time(ms)="+total+",genmap="+converter.genMapTime+",write="+write+",decode="+decode+",makeImage="+makeImage);
		
		
		//don't care encode
		Stopwatch writeWatch=Stopwatch.createStarted();
		
		
		
		/**
		 * 
		 * 2048x1028 1000ms
		 * 4096x2048 50000ms on 512MB servlet
		 * 
		 */
		//TODO
		//call nona,optional 
	}
	
	private int lastSize;

}
