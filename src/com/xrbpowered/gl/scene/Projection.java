package com.xrbpowered.gl.scene;

import org.joml.Matrix4f;

public class Projection {
	
	public static Matrix4f perspective(float fov, float aspectRatio, float near, float far) {
		Matrix4f matrix = new Matrix4f();
		matrix.zero();

		float t = (float)Math.tan(Math.toRadians(fov) / 2.0);
		matrix._m00(1f / (aspectRatio * t));
		matrix._m11(1f / t);
		matrix._m22((far + near) / (near - far));
		matrix._m23(-1);
		matrix._m32((2f * near * far) / (near - far));

		return matrix;
	}
	
	public static Matrix4f perspective(float left, float right, float bottom, float top, float near, float far) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();

		matrix._m00(2f * near / (right - left));
		matrix._m11(2f * near / (top - bottom));
		matrix._m22((far + near) / (far - near));
		matrix._m23(-1f);
		matrix._m32(-2f * far * near / (far - near));
		matrix._m20((right + left) / (right - left));
		matrix._m21((top + bottom) / (top - bottom));
		matrix._m33(0f);

		return matrix;
	}

	public static Matrix4f orthogonal(float left, float right, float bottom, float top, float near, float far) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();

		matrix._m00(2f / (right - left));
		matrix._m11(2f / (top - bottom));
		matrix._m22(-2f / (far - near));
		matrix._m32((far + near) / (far - near));
		matrix._m30((right + left) / (right - left));
		matrix._m31((top + bottom) / (top - bottom));

		return matrix;
	}
}
