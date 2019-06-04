package com.xrbpowered.gl.ui.pane;

import com.xrbpowered.gl.res.texture.Texture;

public class TexturePane extends AbstractPane {

	protected Texture texture = null;

	public TexturePane() {
	}
	
	public TexturePane(Texture texture) {
		setTexture(texture);
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		if(this.texture!=null)
			this.texture.destroy();
		this.texture = texture;
	}
	
	@Override
	protected void bindTexture(int index) {
		texture.bind(0);
	}
	
	public TexturePane resizeToTexture(float scale) {
		if(texture==null) {
			width = 0;
			height = 0;
		}
		else {
			width = (int)(texture.getWidth() * scale);
			height = (int)(texture.getHeight() * scale);
		}
		return this;
	}
	
	public TexturePane resizeToTexture() {
		return resizeToTexture(1f);
	}
	
	public boolean isVisible() {
		return super.isVisible() && texture!=null;
	}
	
	public void destroy() {
		if(texture!=null)
			texture.destroy();
	}
	
}
