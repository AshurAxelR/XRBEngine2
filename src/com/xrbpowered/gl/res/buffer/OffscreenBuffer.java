package com.xrbpowered.gl.res.buffer;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class OffscreenBuffer extends RenderTarget {

	protected int colorTexId;
	protected int depthTexId;
	
	protected OffscreenBuffer(int fbo, int w, int h) {
		super(fbo, w, h);
	}
	
	public OffscreenBuffer(int w, int h, boolean depthBuffer, boolean hdr) {
		super(GL30.glGenFramebuffers(), w, h);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		create(w, h, depthBuffer, hdr);
	}

	public OffscreenBuffer(int w, int h, boolean depthBuffer) {
		this(w, h, depthBuffer, false);
	}

	protected void create(int w, int h, boolean depthBuffer, boolean hdr) {
		colorTexId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, hdr ? GL30.GL_RGB16F : GL11.GL_RGB, w, h, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); //GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); // GL11.GL_LINEAR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexId, 0);
		
		depthTexId = 0;
		if(depthBuffer) {
			depthTexId = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexId);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, w, h, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexId, 0);
		}
		checkStatus();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public int getColorTexId() {
		return colorTexId;
	}
	
	public int getDepthTexId() {
		return depthTexId;
	}
	
	@Override
	public OffscreenBuffer resolve() {
		return this;
	}
	
	public void bindColorBuffer(int index) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexId);
	}

	public void bindDepthBuffer(int index) {
		if(depthTexId>0) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexId);
		}
	}

	@Override
	public void release() {
		GL30.glDeleteFramebuffers(fbo);
		GL11.glDeleteTextures(colorTexId);
		if(depthTexId>0)
			GL11.glDeleteTextures(depthTexId);
	}
	
}
