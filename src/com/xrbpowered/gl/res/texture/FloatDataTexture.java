package com.xrbpowered.gl.res.texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public class FloatDataTexture extends Texture {

	public FloatBuffer buffer;
	
	public FloatDataTexture(int w, int h, boolean wrap) {
		width = w;
		height = h;
		texId = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		setProperties(GL11.GL_TEXTURE_2D, wrap, false, false, anisotropy);
		buffer = ByteBuffer.allocateDirect(16 * w * h).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	public FloatDataTexture setData(float[] data) {
		buffer.put(data);
		buffer.flip();
		return setData(buffer);
	}

	public FloatDataTexture setData(float[][][] data) {
		for(int y=0; y<width; y++)
			for(int x=0; x<height; x++)
				buffer.put(data[x][y]);
		buffer.flip();
		return setData(buffer);
	}
	
	public FloatDataTexture setData(Vector4f[][] data) {
		int offs = 0;
		for(int y=0; y<width; y++)
			for(int x=0; x<height; x++) {
				data[x][y].get(offs, buffer);
				offs += 4;
			}
		buffer.flip();
		return setData(buffer);
	}
	
	public FloatDataTexture setData(FloatBuffer buf) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, width, height, 0, GL12.GL_RGBA, GL12.GL_FLOAT, buf);
		return this;
	}
	
	public FloatDataTexture freeBuffer() {
		buffer = null;
		return this;
	}


}
