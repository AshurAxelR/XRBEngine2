package com.xrbpowered.gl.ui.pane;

import com.xrbpowered.gl.res.buffer.RenderTarget;

public abstract class Pane {

	public int x = 0;
	public int y = 0;
	public int width = 0;
	public int height = 0;
	public float alpha = 1f;
	public boolean ydown = true;
	
	private boolean visible = true;
	
	public Pane() {
	}
	
	public abstract void bindTexture(int index);
	
	public Pane setAnchor(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Pane setSize(int w, int h) {
		this.width = w;
		this.height = h;
		return this;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void draw(RenderTarget target) {
		if(!isVisible() || alpha<=0f)
			return;
		PaneShader shader = PaneShader.getInstance();
		shader.use();
		shader.updateScreenSize(target);
		shader.updateUniforms(x, y, width, height, alpha, ydown);
		bindTexture(0);
		shader.quad.draw();
		shader.unuse();
	}
	
	public void release() {
	}
	
}
