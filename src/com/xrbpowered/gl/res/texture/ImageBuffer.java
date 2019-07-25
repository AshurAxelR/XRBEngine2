package com.xrbpowered.gl.res.texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageBuffer extends BufferedImage {

	public ImageBuffer(int width, int height, boolean opaque) {
		super(width, height, opaque ? TYPE_INT_RGB : TYPE_INT_ARGB);
	}
	
	public boolean isOpaque() {
		return getType()==TYPE_INT_RGB;
	}
	
	@Override
	public Graphics2D getGraphics() {
		return (Graphics2D) super.getGraphics();
	}

}
