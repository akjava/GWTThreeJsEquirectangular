package com.akjava.gwt.equirectangular.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.akjava.gwt.equirectangular.shared.SixCubicToEquirectangler;
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
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Stopwatch totalWatch=Stopwatch.createStarted();
		//just send filename & data & simply write it
		String name=req.getParameter("name");
		if(name==null){
			resp.sendError(500,"no name");
			return;
		}
		
		//image send by base64 because get from canvas.toDataUrl();
		
		List<BufferedImage> images=new ArrayList<BufferedImage>();
		for(int i=0;i<6;i++){
			images.add(null);
		}
		
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
				bytes=BaseEncoding.base64().decode(data.substring(index+";base64,".length()));
				BufferedImage inputImage=ImageIO.read(new ByteArrayInputStream(bytes));
				
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
		
		//check image-size all-same
		
		SixCubicToEquirectangler converter=new SixCubicToEquirectangler(2048);
		BufferedImage outputImage=converter.convertImage(images);
		
		
		//don't care encode
		String dir="s:\\download\\nonadatas\\";
		File file=new File(dir+name);
		
		ImageIO.write(outputImage, "png", file);
		
		long total=totalWatch.elapsed(TimeUnit.MILLISECONDS);
		
		resp.getWriter().println("ok:total="+total+",genmap="+converter.genMapTime);
		//TODO
		//call nona,optional 
	}

}
