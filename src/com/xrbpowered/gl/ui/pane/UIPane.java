package com.xrbpowered.gl.ui.pane;

import java.awt.Rectangle;
import java.awt.RenderingHints;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.texture.BufferTexture;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class UIPane extends UITexture {

	protected boolean opaque;
	
	public UIPane(UIContainer parent, boolean opaque) {
		super(parent);
		this.opaque = opaque;
	}
	
	@Override
	public void setSize(float width, float height) {
		if(width==getWidth() && height==getHeight())
			return;
		float pix = getPixelScale();
		BufferTexture texture = new BufferTexture((int)(width/pix), (int)(height/pix), true, false, false);
		setTexture(texture);
		super.setSize(width, height);
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
	}
	
	@Override
	public void paint(GraphAssist g) {
		updatePaneBounds(g);
		BufferTexture texture = (BufferTexture) pane.getTexture();
		
		GraphAssist gBuff = new GraphAssist(texture.startUpdate());
		gBuff.graph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		gBuff.graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gBuff.graph.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		gBuff.setClip(new Rectangle(0, 0, texture.getWidth(), texture.getHeight()));
		gBuff.scale(1f/getPixelScale());
		
		paintSelf(gBuff);
		paintChildren(gBuff);
		
		texture.finishUpdate();
	}
	
	protected void fitChildren() {
		for(UIElement c : children) {
			c.setLocation(0, 0);
			c.setSize(getWidth(), getHeight());
		}
	}
	
	@Override
	public void setupResources() {
	}

	@Override
	public void resizeResources() {
	}

	@Override
	public void releaseResources() {
	}

	@Override
	public void updateTime(float dt) {
	}
	
	public void render(RenderTarget target) {
		pane.draw();
	}

}
