package com.akjava.gwt.equirectangular.server;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * extream simple 3x3 fixed BlurFilter
 * @author aki
 *
 */
public class BlurFilter  {
	public BufferedImage processImage(BufferedImage image) {
		final float[] datas={
			   0.11f, 0.11f, 0.11f,
			   0.11f, 0.12f, 0.11f,
			   0.11f, 0.11f, 0.11f
			};
		BufferedImageOp blurFilter = new ConvolveOp(
				new Kernel(3, 3, datas), ConvolveOp.EDGE_NO_OP, null);
		return blurFilter.filter(image, null);
	}
}