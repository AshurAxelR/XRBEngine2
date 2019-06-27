package com.xrbpowered.gl.ui.pane;

import java.awt.Rectangle;
import java.awt.RenderingHints;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.texture.BufferTexture;
import com.xrbpowered.gl.ui.ClientWindow;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class UIPane extends UITexture {

	protected boolean opaque;
	protected boolean requestRepaint = true;
	
	public UIPane(UIContainer parent, boolean opaque) {
		super(parent);
		this.opaque = opaque;
	}
	
	private BufferTexture createBuffer() {
		if(!((ClientWindow) getBase().getWindow()).client.hasContext())
			return null;
		float pix = getPixelScale();
		int w = (int)(getWidth()/pix);
		int h = (int)(getHeight()/pix);
		BufferTexture texture = new BufferTexture(w, h, opaque, false, false);
		setTexture(texture);
		return texture;
	}
	
	protected void updateBuffer() {
		BufferTexture texture = (BufferTexture) pane.getTexture();
		if(texture==null)
			texture = createBuffer();
		
		GraphAssist gBuff = new GraphAssist(texture.startUpdate());
		gBuff.graph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		gBuff.graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gBuff.graph.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		gBuff.setClip(new Rectangle(0, 0, texture.getWidth(), texture.getHeight()));
		gBuff.scale(1f/getPixelScale());
		
		paintSelf(gBuff);
		paintChildren(gBuff);
		
		texture.finishUpdate();
		requestRepaint = false;
	}
	
	@Override
	public void repaint() {
		requestRepaint = true;
	}
	
	@Override
	public void setSize(float width, float height) {
		if(width==getWidth() && height==getHeight())
			return;
		super.setSize(width, height);
		createBuffer();
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
	}
	
	@Override
	public void paint(GraphAssist g) {
		updatePaneBounds(g);
		updateBuffer();
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
		pane.release();
	}

	@Override
	public void updateTime(float dt) {
	}
	
	public void render(RenderTarget target) {
		if(isVisible()) {
			if(requestRepaint)
				updateBuffer();
			pane.draw(target);
		}
	}

}
