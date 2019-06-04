package com.xrbpowered.gl.ui;

import com.xrbpowered.gl.Renderer;
import com.xrbpowered.gl.ResourceUser;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class UINode extends UIContainer implements Renderer, ResourceUser {

	public UINode(UIContainer parent) {
		super(parent);
	}

	@Override
	public void setupResources() {
		for(UIElement c : children) {
			((ResourceUser) c).setupResources();
		}
	}

	@Override
	public void resizeResources() {
		for(UIElement c : children) {
			((ResourceUser) c).resizeResources();
		}
	}

	@Override
	public void releaseResources() {
		for(UIElement c : children) {
			((ResourceUser) c).releaseResources();
		}
	}

	@Override
	public void updateTime(float dt) {
		for(UIElement c : children) {
			((Renderer) c).updateTime(dt);
		}
	}
	
	public void render(RenderTarget target) {
		for(UIElement c : children) {
			((Renderer) c).render(target);
		}
	}

}
