package com.xrbpowered.gl.client;

import com.xrbpowered.gl.res.buffer.RenderTarget;

public interface Renderer {
	public void setupResources();
	public void resizeResources();
	public void releaseResources();

	public void updateTime(float dt);
	public void render(RenderTarget target);
}
