#version 400 core

in vec3 position;
in vec3 normal;


out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;

//// Shadows ////
out vec4 shadowCoord;
out vec4 worldPosition;
uniform mat4 depthBiasMatrix;
/////////////////

uniform mat4 tranformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform vec4 plane;
uniform float shadows;

const float density = 0.00001;
const float gradient = 1.1;


void main(void){
	worldPosition = tranformationMatrix * vec4(position,1.0);
	
	if(shadows > 0.5) shadowCoord = depthBiasMatrix * worldPosition;

	surfaceNormal = (tranformationMatrix * vec4(normal, 0.0)).xyz;
	toLightVector[0] = lightPosition[0] - worldPosition.xyz;
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	

	
	
	
	
	
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	
}