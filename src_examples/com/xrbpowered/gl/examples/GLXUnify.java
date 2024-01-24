package com.xrbpowered.gl.examples;

import java.awt.Color;

import org.joml.Vector3f;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.zoomui.UIElement;

public class GLXUnify extends UIClient {

	private static final VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 3)
			.addAttrib("in_TexCoord", 2); 
	
	private CameraActor camera;
	private Controller controller;
	
	private ActorShader shader;
	private Texture texture;
	private StaticMesh mesh;
	private StaticMeshActor meshActor;

	private float r = 0;
	
	public GLXUnify() {
		super("GLXUnify", 1f);
		AssetManager.defaultAssets = new FileAssetManager("example_assets", AssetManager.defaultAssets);

		new UIOffscreen(getContainer()) {
			@Override
			public void setSize(float width, float height) {
				super.setSize(width, height);
				camera.setAspectRatio(getWidth(), getHeight());
			}
			
			@Override
			public void setupResources() {
				Shader.resolveIncludes = true;
				Shader.xunifyDefs = true;
				
				clearColor = new Color(0x777777);
				camera = new CameraActor.Perspective().setAspectRatio(getWidth(), getHeight());
				camera.position = new Vector3f(0, 0, 3);
				camera.updateTransform();
				controller = new Controller(input).setActor(camera);
				
				shader = new ActorShader(vertexInfo, "simple_x.glsl");
				shader.setCamera(camera);
				
				texture = new Texture("checker.png", true, true);

				mesh = ObjMeshLoader.loadObj("test.obj", 0, 1f, vertexInfo, null);
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
				meshActor.draw();
			}
		};
	}

	public static void main(String[] args) {
		new GLXUnify().run();
	}

}
