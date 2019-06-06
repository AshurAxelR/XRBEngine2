 #version 150 core

uniform vec2 screenSize;
uniform vec2 panePosition;
uniform vec2 paneSize;
uniform float yscale = -1;

in vec2 in_Position;
in vec2 in_TexCoord;

out vec2 pass_TexCoord;

void main(void) {
	gl_Position = vec4(
		(in_Position.x * paneSize.x + panePosition.x) * 2.0 / screenSize.x - 1.0,
		((in_Position.y * paneSize.y + panePosition.y) * 2.0 / screenSize.y - 1.0) * yscale,
		0.0, 1.0);
	
	pass_TexCoord = in_TexCoord;
}