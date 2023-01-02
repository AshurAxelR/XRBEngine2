 #version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float time = 0f;

in vec3 in_Position;
in vec3 in_Normal;
in vec3 in_Tangent;
in vec2 in_TexCoord;

in vec3 ins_Position;
in float ins_Scale;
in vec3 ins_RotationAxis;
in float ins_RotationPhase;
in float ins_RotationSpeed;

out vec4 pass_Position;
out vec4 pass_Color;
out vec3 pass_Normal;
out mat3 pass_TBN;
out vec2 pass_TexCoord;

mat4 translationMatrix(vec3 t) {
	mat4 m = mat4(1);
	m[3] = vec4(t, 1);
	return m;
}

/*
mat4 rotationYMatrix(float a) {
	mat4 m = mat4(1);
	m[0][0] = cos(a);
	m[0][2] = sin(a);
	m[2][0] = -m[0][2];
	m[2][2] = m[0][0];
	return m;
}
*/

mat4 axisRotationMatrix(vec3 u, float a) {
	mat4 m = mat4(1);
	float s = sin(a);
	float c = cos(a);
	float ic = 1-c;
	m[0][0] = u.x*u.x*ic + c;
	m[0][1] = u.x*u.y*ic + s*u.z;
	m[0][2] = u.x*u.z*ic - s*u.y;
	m[1][0] = u.y*u.x*ic - s*u.z;
	m[1][1] = u.y*u.y*ic + c;
	m[1][2] = u.y*u.z*ic + s*u.x;
	m[2][0] = u.z*u.x*ic + s*u.y;
	m[2][1] = u.z*u.y*ic - s*u.x;
	m[2][2] = u.z*u.z*ic + c;
	return m;
}

mat4 scaleMatrix(float s) {
	mat4 m = mat4(1);
	m[0][0] = s;
	m[1][1] = s;
	m[2][2] = s;
	return m;
}
void main(void) {
	float rotationAngle = ins_RotationPhase + time*ins_RotationSpeed;
	mat4 modelMatrix = translationMatrix(ins_Position) * axisRotationMatrix(ins_RotationAxis, rotationAngle) * scaleMatrix(ins_Scale);
	pass_Position = viewMatrix * modelMatrix * vec4(in_Position, 1);
	gl_Position = projectionMatrix * pass_Position;
	
	vec3 norm = normalize(vec3(viewMatrix * modelMatrix * vec4(in_Normal, 0)));
	pass_Normal = norm;
	vec3 tan = normalize(vec3(viewMatrix * modelMatrix * vec4(in_Tangent, 0)));
	tan = normalize(tan - dot(tan, norm) * norm);
	pass_TBN = mat3(tan, cross(tan, norm), norm);
	
	pass_TexCoord = in_TexCoord;
}