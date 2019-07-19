package com.xrbpowered.gl.ui;

import com.xrbpowered.gl.client.Renderer;
import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class UINode extends UIContainer implements Renderer {

	public UINode(UIContainer parent) {
		super(parent);
	}
	
	public UIClient getClient() {
		return ((ClientWindow) getBase().getWindow()).client;
	}

	@Override
	public void setupResources() {
		for(UIElement c : children) {
			((Renderer) c).setupResources();
		}
	}

	@Override
	public void resizeResources() {
		for(UIElement c : children) {
			((Renderer) c).resizeResources();
		}
	}

	@Override
	public void releaseResources() {
		for(UIElement c : children) {
			((Renderer) c).releaseResources();
		}
	}

	@Override
	public void updateTime(float dt) {
		for(UIElement c : children) {
			((Renderer) c).updateTime(dt);
		}
	}
	
	public void render(RenderTarget target) {
		if(isVisible()) {
			for(UIElement c : children) {
				((Renderer) c).render(target);
			}
		}
	}

}
