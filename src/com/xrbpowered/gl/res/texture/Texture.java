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

	public Texture(int w, int h, int texId) {
		this.width = w;
		this.height = h;
		this.texId = texId;
	}

	public Texture(String path, boolean wrap, boolean filter) {
		this(path, wrap, filter, filter);
	}

	public Texture(String path, boolean wrap, boolean filterMin, boolean filterMag) {
		try {
			BufferedImage img = AssetManager.defaultAssets.loadImage(path);
			create(img, null, wrap, filterMin, filterMag);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Texture(BufferedImage img, boolean wrap, boolean filter) {
		create(img, null, wrap, filter, filter);
	}

	public Texture(BufferedImage img, boolean wrap, boolean filterMin, boolean filterMag) {
		create(img, null, wrap, filterMin, filterMag);
	}

	public Texture(int w, int h, IntBuffer buf, boolean wrap, boolean filter) {
		create(w, h, buf, wrap, filter, filter);
	}

	public Texture(int w, int h, IntBuffer buf, boolean wrap, boolean filterMin, boolean filterMag) {
		create(w, h, buf, wrap, filterMin, filterMag);
	}

	public Texture(Color color) {
		IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		buf.put(color.getRGB());
		buf.flip();
		create(1, 1, buf, false, false, false);
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
		create(img.getWidth(), img.getHeight(), getPixels(img, buf), wrap, filter, filter);
	}

	protected void create(BufferedImage img, IntBuffer buf, boolean wrap, boolean filterMin, boolean filterMag) {
		create(img.getWidth(), img.getHeight(), getPixels(img, buf), wrap, filterMin, filterMag);
	}
	
	protected void put(int targetType, int w, int h, IntBuffer buf) {
		GL11.glTexImage2D(targetType, 0, GL11.GL_RGBA, w, h, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buf);
	}

	protected void create(int w, int h, IntBuffer buf, boolean wrap, boolean filter) {
		create(w, h, buf, wrap, filter, filter);
	}

	protected void create(int w, int h, IntBuffer buf, boolean wrap, boolean filterMin, boolean filterMag) {
		width = w;
		height = h;
		texId = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		put(GL11.GL_TEXTURE_2D, w, h, buf);
		setProperties(GL11.GL_TEXTURE_2D, wrap, filterMin, filterMag, anisotropy, filterMin);
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

	public static void bindAll(int startIndex, Texture[] textures) {
		if(textures!=null) {
			int index = startIndex;
			for(int i=0; i<textures.length; i++) {
				if(textures[i]!=null)
					textures[i].bind(index);
				else
					unbind(index);
				index++;
			}
		}
	}

	public static void bindAll(Texture[] textures) {
		bindAll(0, textures);
	}

	public static void unbind(int index) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public static void setProperties(int textureType, boolean wrap, boolean filterMin, boolean filterMag, int anisotropy, boolean mipmap) {
		GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_WRAP_S, wrap ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_WRAP_T, wrap ? GL11.GL_REPEAT : GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MAG_FILTER, filterMag ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MIN_FILTER, filterMin ? (mipmap ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR) : GL11.GL_NEAREST);
		
		if(filterMin && mipmap) {
			GL30.glGenerateMipmap(textureType);
		}
		if(filterMin && anisotropy>1) {
			GL11.glTexParameterf(textureType, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);
		}
	}
	
}
