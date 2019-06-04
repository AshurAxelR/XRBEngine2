package com.xrbpowered.gl.res.texture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public class BufferTexture extends Texture {

	protected final boolean opaque;
	protected final BufferedImage imgBuffer;
	private final IntBuffer intBuffer;
	
	public BufferTexture(int w, int h, boolean opaque, boolean wrap, boolean filter) {
		this.opaque = opaque;
		this.width = w;
		this.height = h;
		this.imgBuffer = new BufferedImage(width, height, opaque ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
		this.intBuffer = ByteBuffer.allocateDirect(4 * width * height).order(ByteOrder.nativeOrder()).asIntBuffer();
		create(w, h, intBuffer, wrap, filter);
	}

	public BufferTexture(int w, int h, boolean wrap, boolean filter) {
		this(w, h, false, wrap, filter);
	}
	
	public Graphics2D startUpdate() {
		return (Graphics2D) imgBuffer.getGraphics();
	}
	
	public void finishUpdate() {
		DataBuffer dataBuffer = imgBuffer.getRaster().getDataBuffer();
		intBuffer.put(((DataBufferInt) dataBuffer).getData());
		intBuffer.flip();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getId());
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, opaque ? GL11.GL_RGB : GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_BYTE, intBuffer);
	}
	
	public static final Color CLEAR_COLOR = new Color(0, 0, 0, 0);
	
	public static void clearBuffer(Graphics2D g2, int w, int h) {
		g2.setBackground(CLEAR_COLOR);
		g2.clearRect(0, 0, w, h);
	}
}
