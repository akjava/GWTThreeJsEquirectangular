package com.akjava.gwt.equirectangular.shared;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.akjava.gwt.equirectangular.shared.Cube2Cyl.CUBE_COORD;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Stopwatch;

public class SixCubicToEquirectangler {

	public static void main(String argv[]){
		if(argv.length!=8){
			System.out.println("SixCubicToEquirectangler width up down front back right left output");
		}
		SixCubicToEquirectangler converter=new SixCubicToEquirectangler(Integer.valueOf(argv[0]));
		List<BufferedImage> images=new ArrayList<BufferedImage>();
		try {
		for(int i=0;i<6;i++){	
				BufferedImage input=ImageIO.read(new File(argv[i+1]));
				images.add(input);
		}
		
		BufferedImage output=converter.convertImage(images);
		
		ImageIO.write(output, "png", new File(argv[6]));
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private int erectW;
	
	public static volatile CUBE_COORD[] map;
	
	public SixCubicToEquirectangler(int erectW) {
		super();
		this.erectW = erectW;
	}


	public long genMapTime;
	
	public BufferedImage convertImage(List<BufferedImage> images){
		//time to convert should I share data?.
				int erectH=erectW/2;
				if(map==null){
				Stopwatch watch=Stopwatch.createStarted();
				Cube2Cyl c=new Cube2Cyl();
		
				
				int cubicSize=images.get(0).getWidth();
				c.init(erectW,erectH,cubicSize, Math.PI, Math.PI*2);
				c.genMap();
				map=c.map;
				genMapTime=watch.elapsed(TimeUnit.MILLISECONDS);
				}
				
				BufferedImage outputImage =
		                new BufferedImage(erectW, erectH, BufferedImage.TYPE_INT_ARGB);
				
				
				for(int i=0;i<map.length;i++){
					int x=i%erectW;
					int y=i/erectW;
					
					CUBE_COORD cdata=map[i];
					if(cdata==null){
						LogUtils.log("null:"+x+","+y);
					}
					int face=cdata.face.ordinal();
					
					
					int[] rgb=toRGB(images.get(face).getRGB((int)cdata.x,(int)cdata.y));
					
					int rgba=toColor(rgb[0], rgb[1], rgb[2], 255);
					outputImage.setRGB(x,y,rgba);
				}
				
				
				
				return outputImage;
				
	}
	public static int[] toRGB(int value){
		int[]rgb=new int[3];
		rgb[0]=value>>16&0xff;
		rgb[1]=value>>8&0xff;
		rgb[2]=value&0xff;
		return rgb;
	}
	public static int toColor(int r,int g,int b,int a){
		return (a<<24)|(r<<16) | (g<<8) | b;
	}
}
