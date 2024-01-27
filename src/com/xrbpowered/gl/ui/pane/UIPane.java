package com.xrbpowered.gl.ui.pane;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.texture.BufferTexture;
import com.xrbpowered.gl.ui.ClientWindow;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class UIPane extends UITexture {

	public static final Color transparent = new Color(0, true);
	
	protected boolean opaque;
	protected boolean requestRepaint = true;
	
	public UIPane(UIContainer parent, boolean opaque) {
		super(parent);
		this.opaque = opaque;
	}
	
	private BufferTexture checkCreateBuffer() {
		if(!((ClientWindow) getRoot().getWindow()).client.hasContext())
			return null;
		
		BufferTexture old = (BufferTexture) pane.getTexture();
		float pix = getPixelSize();
		int w = (int)(getWidth()/pix);
		int h = (int)(getHeight()/pix);
		if(old!=null && old.getWidth()==w && old.getHeight()==h)
			return old;
		
		BufferTexture texture = new BufferTexture(w, h, opaque, false, false);
		setTexture(texture);
		if(old!=null)
			old.release(); // glDeleteTextures must go after glGenTextures to avoid bad behaviour due to id recycling
		
		return texture;
	}
	
	protected void updateBuffer() {
		BufferTexture texture = checkCreateBuffer();
		if(texture==null)
			return;
		
		GraphAssist gBuff = new GraphAssist(texture.startUpdate());
		gBuff.graph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		gBuff.graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gBuff.graph.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		gBuff.setClip(new Rectangle(0, 0, texture.getWidth(), texture.getHeight()));
		gBuff.scale(1f/getPixelSize());
		
		paintBackground(gBuff);
		paintChildren(gBuff);
		paintForeground(gBuff);
		
		texture.finishUpdate();
		requestRepaint = false;
	}
	
	@Override
	public void repaint() {
		requestRepaint = true;
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
	}

	@Override
	protected void paintForeground(GraphAssist g) {
	}
	
	@Override
	public void paint(GraphAssist g) {
		updatePaneBounds(g);
		updateBuffer();
	}
	
	public void clear(GraphAssist g, Color bg) {
		g.graph.setBackground(bg);
		g.graph.clearRect(0, 0, (int)getWidth(), (int)getHeight());
	}

	public void clear(GraphAssist g) {
		clear(g, transparent);
	}

	protected void fitChildren() {
		for(UIElement c : children) {
			c.setPosition(0, 0);
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
