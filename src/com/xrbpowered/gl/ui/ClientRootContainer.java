package com.xrbpowered.gl.ui;

import com.xrbpowered.gl.client.Renderer;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.RootContainer;
import com.xrbpowered.zoomui.UIElement;

public class ClientRootContainer extends RootContainer implements Renderer {

	protected boolean updateRequired = true;
	
	public ClientRootContainer(ClientWindow window) {
		super(window, 1f);
	}

	@Override
	public void repaint() {
		updateRequired = true;
	}
	
	@Override
	public void paint(GraphAssist g) {
		paintChildren(g);
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
	
	@Override
	public void render(RenderTarget target) {
		if(invalidLayout)
			layout();
		if(updateRequired) {
			paint(new NodeAssist(getWindow()));
			updateRequired = false;
		}
		for(UIElement c : children) {
			((Renderer) c).render(target);
		}
	}
}
