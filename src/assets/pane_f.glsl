 #version 150 core

uniform sampler2D tex;
uniform float alpha;

in vec2 pass_TexCoord;

out vec4 out_Color;

void main(void) {
	out_Color = texture(tex, pass_TexCoord);
	out_Color.a *= alpha;
}