package com.xrbpowered.gl.res.buffer;

import static org.lwjgl.opengl.GL11.glClearColor;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public abstract class RenderTarget {

	public final int fbo;
	private final int width, height;
	
	protected RenderTarget(int fbo, int w, int h) {
		this.fbo = fbo;
		this.width = w;
		this.height = h;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean isMultisample() {
		return false;
	}
	
	public void use() {
		GL11.glViewport(0, 0, getWidth(), getHeight());
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
	}
	
	public RenderTarget resolve() {
		return this;
	}
	
	public void release() {
	}
	
	public static void blit(RenderTarget source, RenderTarget target, boolean filter) {
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, source.fbo);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.fbo);
		GL30.glBlitFramebuffer(0, 0, source.getWidth(), source.getHeight(), 0, 0, target.getWidth(), target.getHeight(), GL11.GL_COLOR_BUFFER_BIT, filter ? GL11.GL_LINEAR : GL11.GL_NEAREST);
	}
	
	protected static void checkStatus() {
		int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if(status != GL30.GL_FRAMEBUFFER_COMPLETE)
			throw new RuntimeException(String.format("Framebuffer not complete: %04X", status));
	}
	
	public static void setClearColor(Color color) {
		glClearColor(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f);
	}

}
