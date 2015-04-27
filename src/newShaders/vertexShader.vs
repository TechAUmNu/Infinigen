#version 400 core

in vec3 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform mat4 tranformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;


void main(void){

	gl_Position = projectionMatrix * viewMatrix * tranformationMatrix * vec4(position, 1.0);
	pass_textureCoords = textureCoords;

}