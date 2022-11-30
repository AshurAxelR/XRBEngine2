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
import com.xrbpowered.gl.res.shader.InstanceInfo;
import com.xrbpowered.gl.res.shader.Shader;
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

	public static final int INST_COUNT = 1000000;
	public static final float RANGE = 100f;
	
	public static final InstanceInfo standardInstInfo = new InstanceInfo(StandardShader.standardVertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_Scale", 1)
			.addAttrib("ins_RotationAxis", 3)
			.addAttrib("ins_RotationPhase", 1)
			.addAttrib("ins_RotationSpeed", 1);
	
	public class InstanceShader extends StandardShader {
		public InstanceShader() {
			super(standardInstInfo, "std_inst_v.glsl", "std_f.glsl");
		}
	}
	
	public static class MeshComponentInfo {
		public Vector3f position = new Vector3f();
		public float scale = 1f;
		public Vector3f axis = new Vector3f(0, 1, 0);
		public float phase = 0f;
		public float speed = 0f;
	}
	
	public static class MeshComponent extends InstancedMeshList<MeshComponentInfo> {
		public MeshComponent() {
			super(standardInstInfo);
		}
		@Override
		protected void setInstanceData(float[] data, MeshComponentInfo obj, int index) {
			int offs = getDataOffs(index);
			if(obj.axis.lengthSquared()<1e-6f)
				obj.axis.set(0, 1, 0);
			else
				obj.axis.normalize();
			data[offs+0] = obj.position.x;
			data[offs+1] = obj.position.y;
			data[offs+2] = obj.position.z;
			data[offs+3] = obj.scale;
			data[offs+4] = obj.axis.x;
			data[offs+5] = obj.axis.y;
			data[offs+6] = obj.axis.z;
			data[offs+7] = obj.phase;
			data[offs+8] = obj.speed;
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
					info.scale = random.nextFloat()*0.5f+0.5f;
					
					info.axis.x = random.nextFloat()*2f - 1f;
					info.axis.y = random.nextFloat()*2f - 1f;
					info.axis.z = random.nextFloat()*2f - 1f;
					info.phase = random.nextFloat()*(float)Math.PI*2f;
					info.speed = random.nextFloat()*(float)Math.PI*0.1f;
					
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
				shader.time += dt;
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
