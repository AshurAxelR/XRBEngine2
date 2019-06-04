package com.xrbpowered.gl.examples;

import java.awt.Color;

import org.joml.Vector3f;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Projection;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.std.file.UIFileBrowser;

public class GLClientWindow extends UIClient {

	private UINode root;
	//private UITexture checker;
	private UIOffscreen render;
	private UINode files;
	
	public GLClientWindow() {
		super("GLFW Window");
		AssetManager.defaultAssets = new FileAssetManager("example_assets", AssetManager.defaultAssets);
		
		clearColor = new Color(0x5588cc);
		root = new UINode(getContainer()) {
			@Override
			public void layout() {
				files.setLocation(0, 0);
				files.setSize(getWidth()/2, getHeight());
				render.setLocation(getWidth()/2, 0);
				render.setSize(getWidth()/2, getHeight());
				super.layout();
			}
		};
		/*checker = new UITexture(root) {
			@Override
			public void setupResources() {
				setTexture(new Texture("checker.png"), 0.5f);
			}
		};*/
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
			
			@Override
			public void setSize(float width, float height) {
				super.setSize(width, height);
				camera.setProjection(Projection.perspective(70f, getWidth()/getHeight(), 0.1f, 100.0f));
				camera.updateTransform();
			}
			
			@Override
			public void setupResources() {
				clearColor = new Color(0x777777);
				camera = new CameraActor();
				camera.position = new Vector3f(0, 0, 3);
				camera.updateTransform();
				
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
			public void updateTime(float dt) {
				meshActor.rotation.x = r;
				meshActor.rotation.y = r*0.3f;
				meshActor.rotation.z = r*0.1f;
				meshActor.updateTransform();
				r += dt;
				super.updateTime(dt);
			}
			
			@Override
			protected void renderBuffer(RenderTarget target) {
				super.renderBuffer(target);
				shader.setCamera(camera);
				meshActor.draw();
			}
		};
	}
	
	public static void main(String[] args) {
		new GLClientWindow().run();
	}

}
