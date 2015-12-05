#version 330

layout(location = 0) in vec2 pos;
layout(location = 1) in vec2 coord;

out vec2 texCoord;

uniform mat4 projectionMatrix;

void main() {
	gl_Position = projectionMatrix * vec4(pos, 0.0, 1.0);
	texCoord = coord;
}
