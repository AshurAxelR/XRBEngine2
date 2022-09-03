package com.xrbpowered.gl.examples;

import java.awt.Color;
import java.util.Random;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;
import com.xrbpowered.gl.scene.comp.InstancedMeshList;
import com.xrbpowered.gl.ui.common.UIFpsOverlay;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIElement;

public class GLInstances extends UIClient {

	public static final int INST_COUNT = 40000;
	public static final float RANGE = 40f;
	
	public static final VertexInfo standardInstVertexInfo = new VertexInfo(StandardShader.standardVertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_RotationY", 1)
			.addAttrib("ins_Scale", 1); 
	
	public class InstanceShader extends StandardShader {
		public InstanceShader() {
			super("std_inst_v.glsl", "std_f.glsl");
		}
	}
	
	public static class MeshComponentInfo {
		public Vector3f position = new Vector3f();
		public float rotationY = 0f;
		public float scale = 1f;
	}
	
	public static class MeshComponent extends InstancedMeshList<MeshComponentInfo> {
		public MeshComponent() {
			super(standardInstVertexInfo);
		}
		@Override
		protected void setInstanceData(float[] data, MeshComponentInfo obj, int index) {
			int offs = getDataOffs(index);
			data[offs+0] = obj.position.x;
			data[offs+1] = obj.position.y;
			data[offs+2] = obj.position.z;
			data[offs+3] = obj.rotationY;
			data[offs+4] = obj.scale;
		}
	}
	
	private CameraActor camera;
	private Controller cameraController;
	private Controller activeController = null;
	
	private InstanceShader shader;
	
	private Texture diffuse, normal;
	private StaticMesh mesh;

	private MeshComponent meshComp;
	private ComponentRenderer<MeshComponent> meshRenderer;
	
	public GLInstances() {
		super("GLInstances");
		AssetManager.defaultAssets = new FileAssetManager("example_assets", AssetManager.defaultAssets);
		clearColor = new Color(0xeeeeee);
		
		new UIOffscreen(getContainer()) {
			@Override
			public void setSize(float width, float height) {
				super.setSize(width, height);
				camera.setAspectRatio(getWidth(), getHeight());
			}
			
			@Override
			public void setupResources() {
				clearColor = new Color(0xdddddd);
				camera = new CameraActor.Perspective().setRange(0.1f, 40f).setAspectRatio(getWidth(), getHeight());
				camera.position = new Vector3f(0, 0, 2);
				camera.updateTransform();
				cameraController = new Controller(input).setActor(camera);
				
				shader = new InstanceShader();
				shader.setFog(((CameraActor.Perspective) camera).getFar()/2, ((CameraActor.Perspective) camera).getFar(),
						new Vector4f(clearColor.getRed()/255f, clearColor.getGreen()/255f, clearColor.getBlue()/255f, 0.0f));
				shader.ambientColor.set(0.5f, 0.5f, 0.5f, 1f);
				shader.lightColor.set(0.5f, 0.5f, 0.5f, 1f);
				shader.lightDir.set(1, 2, -2).normalize();
				
				diffuse = new Texture("floor_tiles.jpg");
				normal = new Texture("floor_tiles_n.jpg"); // new Texture(new Color(0x8080ff));
				mesh = FastMeshBuilder.cube(1f, StandardShader.standardVertexInfo, null);
				
				meshComp = new MeshComponent();
				meshComp.setTextures(new Texture[] {diffuse, new Texture(new Color(0xffffff)), normal});
				meshComp.setMesh(mesh);
				
				meshComp.startCreateInstances();
				Random random = new Random();
				for(int i=0; i<INST_COUNT; i++) {
					MeshComponentInfo info = new MeshComponentInfo();
					info.position.x = random.nextFloat()*RANGE*2f - RANGE;
					info.position.y = random.nextFloat()*RANGE*2f - RANGE;
					info.position.z = random.nextFloat()*RANGE*2f - RANGE;
					info.rotationY = random.nextFloat()*(float)Math.PI*2f;
					info.scale = random.nextFloat()*0.5f+0.5f;
					meshComp.addInstance(info);
				}
				meshComp.finishCreateInstances();
				
				meshRenderer = new ComponentRenderer<GLInstances.MeshComponent>() {
					@Override
					protected Shader getShader() {
						return shader;
					}
				};
				meshRenderer.add(meshComp);

				super.setupResources();
			}
			
			@Override
			public boolean onMouseDown(float x, float y, Button button, int mods) {
				if(button==UIElement.Button.left) {
					activeController = cameraController;
					getBase().resetFocus();
					activeController.setMouseLook(true);
				}
				return true;
			}
			
			@Override
			public void updateTime(float dt) {
				if(activeController!=null) {
					if(input.isMouseDown(1) || input.isMouseDown(0))
						activeController.update(dt);
					else {
						activeController.setMouseLook(false);
						activeController = null;
					}
				}
				super.updateTime(dt);
			}
			
			@Override
			protected void renderBuffer(RenderTarget target) {
				super.renderBuffer(target);
				shader.setCamera(camera);
				meshRenderer.drawInstances();
			}
		};
		
		new UIFpsOverlay(this).setPaneSize(120, 20).setAnchor(GraphAssist.RIGHT, GraphAssist.BOTTOM, 10f);
	}

	public static void main(String[] args) {
		new GLInstances().run();
	}

}
