package com.xrbpowered.gl.examples;

import static org.lwjgl.opengl.GL11.*;

import java.awt.event.KeyEvent;
import java.util.Random;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.xrbpowered.gl.client.Client;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;

public class GLPoints extends Client {

	private static final int NUM_POINTS = 10000;
	private static final float POINTS_RANGE = 8f;

	private StaticMesh simplePoints;
	private Shader simplePointsShader;
	private float[] pointData;

	private CameraActor camera = null; 
	private Controller controller;
	
	public GLPoints() {
		super("GLPoints");
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
		
		VertexInfo simpleInfo = new VertexInfo().addAttrib("in_Position", 3).addAttrib("in_Size", 1);
		
		pointData = new float [NUM_POINTS*4];
		Random random = new Random();
		int offs = 0;
		for(int i=0; i<NUM_POINTS; i++) {
			pointData[offs++] = random.nextFloat() * 2f * POINTS_RANGE - POINTS_RANGE;
			pointData[offs++] = random.nextFloat() * 2f * POINTS_RANGE - POINTS_RANGE;
			pointData[offs++] = random.nextFloat() * 2f * POINTS_RANGE - POINTS_RANGE;
			pointData[offs++] = 0.1f;
		}

		simplePointsShader = new Shader(simpleInfo, "points_v.glsl", "points_f.glsl") {
			private int projectionMatrixLocation;
			private int viewMatrixLocation;
			private int screenHeightLocation;
			@Override
			protected void storeUniformLocations() {
				projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
				viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
				screenHeightLocation  = GL20.glGetUniformLocation(pId, "screenHeight");
			}
			@Override
			public void updateUniforms() {
				glEnable(GL20.GL_POINT_SPRITE);
				GL11.glEnable(GL32.GL_PROGRAM_POINT_SIZE);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				uniform(projectionMatrixLocation, camera.getProjection());
				uniform(viewMatrixLocation, camera.getView());
				GL20.glUniform1f(screenHeightLocation, getFrameHeight());
			}
			@Override
			public void unuse() {
				GL11.glDisable(GL11.GL_BLEND);
				super.unuse();
			}
		};
		simplePoints = new StaticMesh(simpleInfo, pointData, 1, NUM_POINTS, false);
		
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
		simplePointsShader.use();
		simplePoints.draw();
		simplePointsShader.unuse();
	}

	public static void main(String[] args) {
		new GLPoints().run();
	}

}
