#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;

//// Shadows ////
out vec4 shadowCoord;
uniform mat4 depthBiasMatrix;
/////////////////

uniform mat4 tranformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform vec4 plane;

const float density = 0.00001;
const float gradient = 1.1;


void main(void){
	shadowCoord = depthBiasMatrix * tranformationMatrix * vec4(position, 1);

	vec4 worldPosition = tranformationMatrix * vec4(position,1.0);
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoords = textureCoords;

	surfaceNormal = (tranformationMatrix * vec4(normal, 0.0)).xyz;
	
	for(int i = 0; i < 4; i++){
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	
}