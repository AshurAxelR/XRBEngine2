package com.xrbpowered.gl.res.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.xrbpowered.gl.res.asset.AssetManager;

public class Texture {

	// settings
	public static int anisotropy = 4;
	
	protected int width, height;
	protected int texId;
	
	public Texture() {
		texId = 0;
	}

	public static IntBuffer getPixels(BufferedImage img, IntBuffer buf) {
		int w = img.getWidth();
		int h = img.getHeight();
		if(buf==null)
			buf = ByteBuffer.allocateDirect(4 * w * h).order(ByteOrder.nativeOrder()).asIntBuffer();
		int[] pixels = img.getRGB(0, 0, w, h, null, 0, w);
		buf.put(pixels);
		buf.flip();
		return buf;
	}

	public Texture(String path, boolean wrap, boolean filter) {
		try {
			BufferedImage img = AssetManager.defaultAssets.loadImage(path);
			create(img, null, wrap, filter);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Texture(BufferedImage img, boolean wrap, boolean filter) {
		create(img, null, wrap, filter);
	}
	
	public Texture(int w, int h, IntBuffer buf, boolean wrap, boolean filter) {
		create(w, h, buf, wrap, filter);
	}

	public Texture(Color color) {
		IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		buf.put(color.getRGB());
		buf.flip();
		create(1, 1, buf, false, false);
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getId() {
		return texId;
	}

	protected void create(BufferedImage img, IntBuffer buf, boolean wrap, boolean filter) {
		create(img.getWidth(), img.getHeight(), getPixels(img, buf), wrap, filter);
	}
	
	protected void put(int targetType, int w, int h, IntBuffer buf) {
		GL11.glTexImage2D(targetType, 0, GL11.GL_RGBA, w, h, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buf);
	}
	
	protected void create(int w, int h, IntBuffer buf, boolean wrap, boolean filter) {
		width = w;
		height = h;
		texId = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		put(GL11.GL_TEXTURE_2D, w, h, buf);
		setProperties(GL11.GL_TEXTURE_2D, wrap, filter, anisotropy);
	}
	
	public Texture(String path) {
		this(path, true, true);
	}
	
	public Texture(BufferedImage img) {
		this(img, true, true);
	}
	
	public void bind(int index) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
	}
	
	public void release() {
		GL11.glDeleteTextures(texId);
	}

	public static void bindAll(Texture[] textures) {
		if(textures!=null) {
			for(int i=0; i<textures.length; i++)
				textures[i].bind(i);
		}
	}
	
	public static void unbind(int index) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public static void setProperties(int textureType, boolean wrap, boolean filter, int anisotropy) {
		GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_WRAP_S, wrap ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_WRAP_T, wrap ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MAG_FILTER, filter ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MIN_FILTER, filter ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_NEAREST);
		
		if(filter) {
			GL30.glGenerateMipmap(textureType);
			if(anisotropy>1) {
				GL11.glTexParameterf(textureType, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);
			}
		}
	}
	
}
