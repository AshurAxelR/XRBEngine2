#version 150 core

uniform vec3 objId;

out vec4 out_objId;

void main(void) {
	out_objId = vec4(objId, 0);
}