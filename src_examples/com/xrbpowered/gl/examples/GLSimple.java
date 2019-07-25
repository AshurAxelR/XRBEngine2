package com.xrbpowered.gl.examples;

import static org.lwjgl.opengl.GL11.*;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;

import com.xrbpowered.gl.client.Client;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class GLSimple extends Client {

	private StandardShader shader;
	private Texture texture;
	private StaticMesh mesh;
	private CameraActor camera = null; 
	private StaticMeshActor meshActor;
	private Controller controller;
	
	public GLSimple() {
		super("GLSimple");
		AssetManager.defaultAssets = new FileAssetManager("example_assets", AssetManager.defaultAssets);
	}
	
	@Override
	public void createResources() {
		glClearColor(0.5f, 0.5f, 0.5f, 1f);
		
		camera = new CameraActor.Perspective().setAspectRatio(getFrameWidth(), getFrameHeight());
		camera.position = new Vector3f(0, 0, 3);
		camera.updateTransform();
		controller = new Controller(input).setActor(camera);
		controller.setMouseLook(true);
		
		shader = StandardShader.getInstance();
		
		texture = new Texture("checker.png", true, true);
		//texture = new Texture(new Color(0xffddbb));

		//mesh = FastMeshBuilder.cube(1f, StandardShader.standardVertexInfo, null);
		mesh = ObjMeshLoader.loadObj("test.obj", 0, 1f, StandardShader.standardVertexInfo, null);

		meshActor = StaticMeshActor.make(mesh, shader, texture);
		meshActor.position = new Vector3f(0, 0, -2);
		meshActor.updateTransform();
		
		super.createResources();
	}
	
	@Override
	public void keyPressed(char c, int code) {
		if(code==KeyEvent.VK_ESCAPE)
			requestExit();
	}
	
	@Override
	public void resizeResources() {
		camera.setAspectRatio(getFrameWidth(), getFrameHeight());
	}
	
	@Override
	public void render(float dt) {
		controller.update(dt);
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);
		shader.setCamera(camera);
		meshActor.draw();
	}

	public static void main(String[] args) {
		new GLSimple().run();
	}

}
