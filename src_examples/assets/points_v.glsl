#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float screenHeight = 1;

in vec3 in_Position;
in float in_Size;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * vec4(in_Position, 1);
	gl_PointSize = in_Size * screenHeight / gl_Position.w;
}
