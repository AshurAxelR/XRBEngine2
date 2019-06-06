package com.xrbpowered.gl.ui.pane;

public abstract class AbstractPane {

	public int x = 0;
	public int y = 0;
	public int width = 0;
	public int height = 0;
	public float alpha = 1f;
	
	protected boolean ydown = true;
	
	private boolean visible = true;
	
	public AbstractPane() {
	}
	
	protected abstract void bindTexture(int index);
	
	public AbstractPane setAnchor(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public AbstractPane setSize(int w, int h) {
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
	
	public void draw() {
		if(!isVisible() || alpha<=0f)
			return;
		PaneShader shader = PaneShader.getInstance();
		shader.use();
		shader.updateUniforms(x, y, width, height, alpha, ydown);
		bindTexture(0);
		shader.quad.draw();
		shader.unuse();
	}
	
	public void destroy() {
	}
	
}
