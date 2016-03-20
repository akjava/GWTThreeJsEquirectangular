package com.akjava.gwt.equirectangular.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.akjava.gwt.equirectangular.shared.Cube2Cyl;
import com.akjava.gwt.equirectangular.shared.Cube2Cyl.CUBE_COORD;
import com.akjava.lib.common.graphics.BilinearCalculator;
import com.akjava.lib.common.graphics.BilinearCalculator.AbstractSizeLimitBilinearValueGetter;
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
				Stopwatch watch0=Stopwatch.createStarted();
				BufferedImage outputImage =
		                new BufferedImage(erectW, erectH, BufferedImage.TYPE_INT_ARGB);
				//i guess create is slow
				// initially 3000,average 10ms
				//System.out.println("create bufferimage time:"+watch0.elapsed(TimeUnit.MILLISECONDS));
				
				for(int i=0;i<map.length;i++){
					int x=i%erectW;
					int y=i/erectW;
					
					CUBE_COORD cdata=map[i];
					if(cdata==null){
						System.out.println("null:"+x+","+y);
					}
					int face=cdata.face.ordinal();
					
					
					int[] rgb=convertToRGB(images.get(face),cdata.x,cdata.y);//toRGB(images.get(face).getRGB((int)cdata.x,(int)cdata.y));
					
					int rgba=toColor(rgb[0], rgb[1], rgb[2], 255);
					outputImage.setRGB(x,y,rgba);
				}
				
				
				
				return outputImage;
				
	}
	
	private boolean useBilinear=false;//maybe 5time slow
	private BilinearCalculator calculatro;
	private BufferedImageValueGetter valueGetter;
	
	public  static class  BufferedImageValueGetter extends AbstractSizeLimitBilinearValueGetter{
		public BufferedImageValueGetter() {
			super(0, 0);
		}
		private BufferedImage image;
		public BufferedImage getImage() {
			return image;
		}
		public void setImage(BufferedImage image) {
			if(image!=this.image){
			this.image = image;
			width=image.getWidth();
			height=image.getHeight();
			}
		}
		public static final int RED=0;
		public static final int GREEN=1;
		public static final int BLUE=2;
		private int mode;
		public int getMode() {
			return mode;
		}
		public void setMode(int mode) {
			this.mode = mode;
		}
		@Override
		public double getValueAt(int x, int y) {
			if(mode==RED){
				return image.getRGB(x,y) >>16&0xff;
			}else if(mode==GREEN){
				return image.getRGB(x,y) >>8&0xff;
			}else{
				return image.getRGB(x,y) &0xff;
			}
		}
		
	}
	public int[] convertToRGB(BufferedImage image,double x,double y){
		if(!useBilinear){
			return toRGB(image.getRGB((int)x,(int)y));
		}
		
		if(calculatro==null){
			calculatro=new BilinearCalculator(image.getWidth(), image.getHeight());
			valueGetter=new BufferedImageValueGetter();
		}
		
		//x=(int)x;
		//y=(int)y;
		
		int rgb[]=new int[3];
		valueGetter.setImage(image);
		
		valueGetter.setMode(BufferedImageValueGetter.RED);
		rgb[0]=(int) calculatro.calculateValue(x, y, valueGetter);
		
		valueGetter.setMode(BufferedImageValueGetter.GREEN);
		rgb[1]=(int) calculatro.calculateValue(x, y, valueGetter);
		
		valueGetter.setMode(BufferedImageValueGetter.BLUE);
		rgb[2]=(int) calculatro.calculateValue(x, y, valueGetter);
		
		return rgb;
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
