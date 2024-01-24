package com.xrbpowered.gl.examples.tutorial;

import java.awt.Color;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UITexture;

public class GLQuadUI extends UIClient {

	public GLQuadUI() {
		super("Quad using UI", 1f);
		
		clearColor = new Color(0x808080);
		
		UINode root = new UINode(getContainer());
		
		UITexture pane = new UITexture(root) {
			@Override
			public void setupResources() {
				setTexture(new Texture("example_assets/checker.png"));
				super.setupResources();
			}
		};
		pane.setPosition(10, 10);
		pane.setSize(512, 512);
	}
	
	public static void main(String[] args) {
		new GLQuadUI().run();
	}

}
