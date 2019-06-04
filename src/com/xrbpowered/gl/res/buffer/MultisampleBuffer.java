package com.xrbpowered.gl.res.buffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class MultisampleBuffer extends OffscreenBuffer {

	private int colorMSTexId;
	private int depthMSTexId;
	private OffscreenBuffer resolve;
	
	public MultisampleBuffer(int w, int h, int samples, boolean hdr) {
		super(GL30.glGenFramebuffers(), w, h);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		create(w, h, samples, true, hdr);
		resolve = new OffscreenBuffer(w, h, false, hdr);
	}

	public MultisampleBuffer(int w, int h, int samples) {
		this(w, h, samples, false);
	}

	protected void create(int w, int h, int samples, boolean depthBuffer, boolean hdr) {
		colorMSTexId = GL11.glGenTextures();
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, colorMSTexId);
		GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samples, hdr ? GL30.GL_RGB16F : GL11.GL_RGB, w, h, false);
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, colorMSTexId, 0);
		
		depthMSTexId = 0;
		if(depthBuffer) {
			depthMSTexId = GL11.glGenTextures();
			GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, depthMSTexId);
			GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samples, GL30.GL_DEPTH24_STENCIL8, w, h, false);
			GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthMSTexId, 0);
		}
		checkStatus();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	@Override
	public OffscreenBuffer resolve() {
		blit(this, resolve, false);
		return resolve;
	}
	
	public void bindColorBuffer(int index) {
		resolve.bindColorBuffer(index);
	}

	public void bindDepthBuffer(int index) {
		if(depthMSTexId>0)
			resolve.bindDepthBuffer(index);
	}
	
	@Override
	public boolean isMultisample() {
		return true;
	}
	
	@Override
	public void destroy() {
		resolve.destroy();
		super.destroy();
	}

}
