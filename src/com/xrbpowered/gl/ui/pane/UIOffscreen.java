package com.xrbpowered.gl.ui.pane;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.xrbpowered.gl.res.buffer.OffscreenBuffer;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class UIOffscreen extends UINode {

	public final OffscreenPane pane = new OffscreenPane();
	public Color clearColor = Color.RED;
	public final float bufferScale;
	
	public UIOffscreen(UIContainer parent, float scale) {
		super(parent);
		this.bufferScale = scale;
	}
	
	public UIOffscreen(UIContainer parent) {
		this(parent, 1f);
	}

	@Override
	public void setSize(float width, float height) {
		if(width==getWidth() && height==getHeight())
			return;
		float pix = getPixelScale() * bufferScale;
		OffscreenBuffer buffer = new OffscreenBuffer((int)(width/pix), (int)(height/pix), true);
		pane.setBuffer(buffer);
		super.setSize(width, height);
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
	
	protected void renderBuffer(RenderTarget target) {
		glClearColor(clearColor.getRed()/255f, clearColor.getGreen()/255f, clearColor.getBlue()/255f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void render(RenderTarget target) {
		pane.getBuffer().use();
		renderBuffer(pane.getBuffer());
		target.use();
		pane.draw(target);
		super.render(target);
	}
	
	@Override
	public void releaseResources() {
		pane.getBuffer().release();
		super.releaseResources();
	}
}
