package com.xrbpowered.gl.ui.common;

import java.awt.Color;
import java.awt.Font;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIElement;

public class UIFpsOverlay extends UINode {

	public final UIClient client;
	
	protected Color bgColor = new Color(0x77000000, true);
	protected Color fgColor = Color.WHITE;
	protected Font font = null;
	
	protected int halign = GraphAssist.LEFT;
	protected int valign = GraphAssist.TOP;
	protected float margin = 10f;
	
	private final UIPane pane;
	private int currentFps = -1;
	
	public UIFpsOverlay(UIClient client) {
		super(client.getContainer());
		this.client = client;
		
		pane = new UIPane(this, false) {
			@Override
			protected void paintSelf(GraphAssist g) {
				g.graph.setBackground(bgColor);
				g.graph.clearRect(0, 0, (int)getWidth(), (int)getHeight());
				g.setColor(fgColor);
				if(font!=null)
					g.setFont(font);
				String s = String.format(currentFps>100 ? "%.0f FPS" : "%.1f FPS", currentFps/10f);
				g.drawString(s, getWidth()/2f, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
			}
		};
		pane.setSize(80, 20);
	}
	
	public UIFpsOverlay setBgColor(Color bgColor) {
		this.bgColor = bgColor;
		return this;
	}
	
	public UIFpsOverlay setFgColor(Color fgColor) {
		this.fgColor = fgColor;
		return this;
	}
	
	public UIFpsOverlay setFont(Font font) {
		this.font = font;
		return this;
	}

	private void updateLocation() {
		pane.setLocation(
				GraphAssist.align(getWidth()-pane.getWidth()-margin*2f, halign)+margin,
				GraphAssist.align(getHeight()-pane.getHeight()-margin*2f, halign)+margin
			);
	}

	public UIFpsOverlay setPaneSize(float width, float height) {
		pane.setSize(width, height);
		updateLocation();
		return this;
	}
	
	public UIFpsOverlay setAnchor(int halign, int valign, float margin) {
		this.halign = halign;
		this.valign = valign;
		this.margin = margin;
		updateLocation();
		return this;
	}

	@Override
	public void layout() {
		updateLocation();
		super.layout();
	}
	
	@Override
	public void updateTime(float dt) {
		int fps = (int)(client.getFps()*10f);
		if(fps!=currentFps) {
			currentFps = fps;
			repaint();
		}
		super.updateTime(dt);
	}
	
	@Override
	public UIElement getElementAt(float x, float y) {
		return null;
	}

}
