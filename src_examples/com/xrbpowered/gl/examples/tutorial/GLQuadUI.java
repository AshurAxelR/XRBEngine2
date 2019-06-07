package com.xrbpowered.gl.examples.tutorial;

import java.awt.Color;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UITexture;

public class GLQuadUI extends UIClient {

	public GLQuadUI() {
		super("Quad using UI", 1f);
		AssetManager.defaultAssets = new FileAssetManager("example_assets", AssetManager.defaultAssets);
		
		clearColor = new Color(0x808080);
		
		UINode root = new UINode(getContainer());
		
		UITexture pane = new UITexture(root) {
			@Override
			public void setupResources() {
				setTexture(new Texture("checker.png"));
				super.setupResources();
			}
		};
		pane.setLocation(10, 10);
		pane.setSize(512, 512);
	}
	
	public static void main(String[] args) {
		new GLQuadUI().run();
	}

}
