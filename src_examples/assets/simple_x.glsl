#version 150 core
#include <xunify>

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform sampler2D texDiffuse;

uniform float fogNear = 5;
uniform float fogFar = 50;
uniform vec4 fogColor = vec4(0.45, 0.45, 0.45, 1);

vs_in vec3 in_Position;
vs_in vec2 in_TexCoord;

vs_out vec4 pass_Position;
vs_out vec2 pass_TexCoord;

fs_out vec4 out_Color;

#ifdef VERTEX_SHADER
void main(void) {
	pass_Position = viewMatrix * modelMatrix * vec4(in_Position, 1);
	gl_Position = projectionMatrix * pass_Position;
	pass_TexCoord = in_TexCoord;
}
#endif

#ifdef FRAGMENT_SHADER
void main(void) {
	vec4 t_diffuse = texture(texDiffuse, pass_TexCoord);
	if(t_diffuse.a<0.5)
		discard;
	float viewDist = length(pass_Position.xyz);
	out_Color = mix(t_diffuse, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
}
#endif
