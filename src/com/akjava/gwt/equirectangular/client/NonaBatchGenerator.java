package com.akjava.gwt.equirectangular.client;

import java.util.List;

import com.akjava.lib.common.utils.TemplateUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class NonaBatchGenerator {
	private String nonaPath;
	public NonaBatchGenerator(String nonaPath, int imageSize) {
		super();
		this.nonaPath = nonaPath;
		this.imageSize = imageSize;
	}

	private String format="PNG";
	
	public static final String HUGIN_HEADER="# hugin project file\r\n" +
			"#hugin_ptoversion 2\r\n";
	
	
	private int height=1080;
	private int imageSize=640;
	
	public static final int calculateHeightFromImageSize(int size){
		return (int)(Math.PI*size/2);
	}
	
	//create .bat
	public String createBatch(int size){
		List<String> lines=Lists.newArrayList();
		String template="\"${path}\" -o ${index}.png ${index}.pto";
		
		for(int i=1;i<=size;i++){
			String index=SixCubeFrameIO.toIndex(i);
			lines.add(TemplateUtils.createText(template, ImmutableMap.of(
					"path",nonaPath,
					"index",index
					)));
		}
		
		return Joiner.on("\r\n").join(lines);
	}
	
	//create .pto; pto filename must be ixCubeFrameIO.toIndex(i)+".pto"
	public String createPto(int index){
		List<String> lines=Lists.newArrayList();
		String global="p f2 w${width} h${height} v360  E0 R0 n\"${format}\"";
		
		lines.add(TemplateUtils.createText(global, ImmutableMap.of(
				"width",String.valueOf(height*2),
				"height",String.valueOf(height),
				"format",format
				)));
		
		
		
		lines.add("# image lines");
		
		String imageTemplate="i w${width} h${height} f0 v90 Ra0 Rb0 Rc0 Rd0 Re0 Eev0 Er1 Eb1 r0 p${p} y${y} TrX0 TrY0 TrZ0 Tpy0 Tpp0 j0 a0 b0 c0 d0 e0 g0 t0 Va1 Vb0 Vc0 Vd0 Vx0 Vy0  Vm5 n\"${fileName}\"";
		List<Integer> yValues=Lists.newArrayList(0,0,0,180,90,270);
		List<Integer> pValues=Lists.newArrayList(90,-90,0,0,0,0);
	
		for(int i=0;i<SixCubeFrameIO.directions.size();i++){
			lines.add(TemplateUtils.createText(imageTemplate, ImmutableMap.of(
					"width",String.valueOf(imageSize),
					"height",String.valueOf(imageSize),
					"p",String.valueOf(pValues.get(i)),
					"y",String.valueOf(yValues.get(i)),
					"fileName",SixCubeFrameIO.getFileName(index, i)
					)));
		}
		
		
		return HUGIN_HEADER+Joiner.on("\r\n").join(lines);
	}
}
