package com.xrbpowered.gl.examples;

import java.awt.Color;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.common.UIFpsOverlay;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.gl.ui.pane.UITexture;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.std.file.UIFileBrowser;

public class GLClientWindow extends UIClient {

	private UINode root;
	private UITexture checker;
	private UIOffscreen render;
	private UINode files;
	
	public GLClientWindow() {
		super("GLFW Window");
		AssetManager.defaultAssets = new FileAssetManager("example_assets", AssetManager.defaultAssets);
		
		clearColor = new Color(0x5588cc);
		root = new UINode(getContainer()) {
			@Override
			public void layout() {
				files.setPosition(0, 0);
				files.setSize(getWidth()/2, getHeight());
				render.setPosition(getWidth()/2, 0);
				render.setSize(getWidth()/2, getHeight());
				// render.setLocation(0, 0);
				// render.setSize(getWidth(), getHeight());
				checker.setPosition(getWidth()-checker.getWidth()-10f, 10f);
				super.layout();
			}
		};
		new UIFpsOverlay(this).setPaneSize(120, 20).setAnchor(GraphAssist.RIGHT, GraphAssist.BOTTOM, 10f);
		files = new UIPane(root, true) {
			@Override
			public void layout() {
				fitChildren();
				super.layout();
			}
		};
		new UIFileBrowser(files, null);
		
		render = new UIOffscreen(root) {
			private StandardShader shader;
			private Texture texture;
			private StaticMesh mesh;
			private CameraActor camera = null; 
			private StaticMeshActor meshActor;
			private float r = 0;
			private Controller controller;
			
			@Override
			public void setSize(float width, float height) {
				super.setSize(width, height);
				camera.setAspectRatio(getWidth(), getHeight());
			}
			
			@Override
			public void setupResources() {
				GL11.glEnable(GL11.GL_CULL_FACE);
				
				clearColor = new Color(0x777777);
				camera = new CameraActor.Perspective().setAspectRatio(getWidth(), getHeight());
				camera.position = new Vector3f(0, 0, 3);
				camera.updateTransform();
				controller = new Controller(input).setActor(camera);
				
				shader = StandardShader.getInstance();
				shader.ambientColor.set(0.5f, 0.5f, 0.5f, 1f);
				shader.lightColor.set(0.5f, 0.5f, 0.5f, 1f);
				shader.lightDir.set(0, 0, -1).normalize();
				
				texture = new Texture("checker.png", true, true);
				//texture = new Texture(new Color(0xffddbb));

				//mesh = FastMeshBuilder.cube(1f, StandardShader.standardVertexInfo, null);
				mesh = ObjMeshLoader.loadObj("test.obj", 0, 1f, StandardShader.standardVertexInfo, null);
				if(mesh==null)
					throw new RuntimeException("Cannot load mesh");
				meshActor = StaticMeshActor.make(mesh, shader, texture);
				meshActor.position = new Vector3f(0, 0, -2);
				meshActor.updateTransform();
				
				super.setupResources();
			}
			
			@Override
			public boolean onMouseDown(float x, float y, Button button, int mods) {
				if(button==UIElement.Button.left) {
					getRoot().resetFocus();
					controller.setMouseLook(true);
				}
				return true;
			}
			
			@Override
			public void updateTime(float dt) {
				meshActor.rotation.y = r;
				meshActor.updateTransform();
				r += dt;
				if(input.isMouseDown(0))
					controller.update(dt);
				else
					controller.setMouseLook(false);
				super.updateTime(dt);
			}
			
			@Override
			protected void renderBuffer(RenderTarget target) {
				super.renderBuffer(target);
				shader.setCamera(camera);
				meshActor.draw();
			}
		};
		
		checker = new UITexture(root) {
			@Override
			public void setupResources() {
				setTexture(new Texture("checker.png"));
			}
		};
		checker.setSize(200, 200);
	}
	
	public static void main(String[] args) {
		new GLClientWindow().run();
	}

}
