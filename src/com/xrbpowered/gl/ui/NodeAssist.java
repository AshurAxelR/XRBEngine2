package com.xrbpowered.gl.ui;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIWindow;

public class NodeAssist extends GraphAssist {

	private AffineTransform transform = new AffineTransform();
	private Rectangle clip = new Rectangle();

	public NodeAssist(UIWindow window) {
		this(window.getClientWidth(), window.getClientHeight());
	}

	public NodeAssist(int width, int height) {
		super(null);
		setClip(new Rectangle(0, 0, width, height));
	}

	@Override
	public AffineTransform getTransform() {
		return new AffineTransform(transform);
	}
	
	@Override
	public void setTransform(AffineTransform t) {
		transform.setTransform(t);
	}
	
	public void clearTransform() {
		transform.setToIdentity();
	}
	
	public void translate(double tx, double ty) {
		transform.translate(tx, ty);
	}
	
	public void scale(double scale) {
		transform.scale(scale, scale);
	}
	
	@Override
	public Rectangle getClip() {
		return new Rectangle(clip);
	}
	
	@Override
	public void setClip(Rectangle r) {
		clip.setBounds(r);
	}
}
