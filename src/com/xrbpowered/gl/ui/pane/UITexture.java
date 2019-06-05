package com.xrbpowered.gl.ui.pane;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class UITexture extends UINode {

	public final TexturePane pane = new TexturePane();
	
	public UITexture(UIContainer parent) {
		super(parent);
	}

	protected UITexture setTexture(Texture texture, float scale, boolean resize) {
		pane.setTexture(texture);
		pane.resizeToTexture(scale);
		if(resize)
			super.setSize(pane.width, pane.height);
		return this;
	} 
	
	public UITexture setTexture(Texture texture, float scale) {
		return setTexture(texture, 1f, true);
	}

	public UITexture setTexture(Texture texture) {
		return setTexture(texture, 1f);
	}
	
	protected void updatePaneBounds(GraphAssist g) {
		Point2D p = new Point2D.Float(0, 0);
		Point2D pr = new Point2D.Float(getWidth(), getHeight());
		AffineTransform t = g.getTransform();
		t.transform(p, p);
		t.transform(pr, pr);
		pane.setAnchor((int)p.getX(), (int)p.getY());
		pane.setSize((int)(pr.getX()-p.getX()), (int)(pr.getY()-p.getY()));
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		updatePaneBounds(g);
	}
	
	public void render(RenderTarget target) {
		pane.draw();
		super.render(target);
	}
}
