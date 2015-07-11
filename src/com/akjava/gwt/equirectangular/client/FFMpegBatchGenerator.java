package com.akjava.gwt.equirectangular.client;

import com.akjava.lib.common.utils.TemplateUtils;
import com.google.common.collect.ImmutableMap;

public class FFMpegBatchGenerator {
	private String ffmpegPath;
	private int rate;
	public FFMpegBatchGenerator(String ffmpegPath, int rate, String outputName) {
		super();
		this.ffmpegPath = ffmpegPath;
		this.rate = rate;
		this.outputName = outputName;
	}
	private String outputName;
	
	public String createBatch(){
		String command="\"${ffmpeg}\" -i %%5d.png -r ${rate} -crf 7 -b:v 100m  ${output}";
		return TemplateUtils.createText(command, ImmutableMap.of(
				"ffmpeg",ffmpegPath,
				"rate",String.valueOf(rate),
				"output",outputName
				));
	}
}
