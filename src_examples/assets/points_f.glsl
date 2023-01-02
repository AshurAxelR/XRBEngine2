#version 150 core

out vec4 out_Color;

void main(void) {
	out_Color = vec4(gl_PointCoord, 0, 1);
}
