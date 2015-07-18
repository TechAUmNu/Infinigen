#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D textureSampler;


uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void){

	
	
	vec3 unitNormal = normalize(surfaceNormal);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i = 0; i < 4; i++){	
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		
		vec3 unitLightVector = normalize(toLightVector[i]);	
		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.0);	
		vec3 unitVectorToCamera = normalize(toCameraVector);
		vec3 lightDirection = -unitVectorToCamera;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);	
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColour[i])/attFactor;	
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i])/attFactor;
	}
	
	totalDiffuse = max(totalDiffuse, 0.0);
	out_Color = vec4(1,1,1,0);
	out_Color = vec4(totalDiffuse, 1.0) * texture(textureSampler, pass_textureCoords) + vec4(totalSpecular, 1.0);
	//out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);
}