#version 330

in vec2 texCoord;

uniform sampler2D fontTex;
uniform vec4 color;

out vec4 fragColor;

void main() {
	vec3 fontColor = vec3(1.0) - texture(fontTex, texCoord).xyz;
	
	float mag = length(fontColor);
	fragColor = vec4(fontColor, mag * mag * mag) * color;
}
