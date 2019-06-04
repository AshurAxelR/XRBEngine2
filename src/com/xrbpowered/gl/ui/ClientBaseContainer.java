package com.xrbpowered.gl.ui;

import com.xrbpowered.gl.Renderer;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.zoomui.BaseContainer;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIElement;

public class ClientBaseContainer extends BaseContainer implements Renderer {

	protected boolean updateRequired = true;
	
	public ClientBaseContainer(ClientWindow window) {
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
