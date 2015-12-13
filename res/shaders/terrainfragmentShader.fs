#version 400 core


in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;


out vec4 out_Color;

//Shadows///
in vec4 shadowCoord;
in vec4 worldPosition;
uniform sampler2DShadow shadowMap;
uniform float shadows;
////////////////////////////


uniform sampler2D textureSampler;


uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;


vec2 poissonDisk[16] = vec2[]( 
   vec2( -0.94201624, -0.39906216 ), 
   vec2( 0.94558609, -0.76890725 ), 
   vec2( -0.094184101, -0.92938870 ), 
   vec2( 0.34495938, 0.29387760 ), 
   vec2( -0.91588581, 0.45771432 ), 
   vec2( -0.81544232, -0.87912464 ), 
   vec2( -0.38277543, 0.27676845 ), 
   vec2( 0.97484398, 0.75648379 ), 
   vec2( 0.44323325, -0.97511554 ), 
   vec2( 0.53742981, -0.47373420 ), 
   vec2( -0.26496911, -0.41893023 ), 
   vec2( 0.79197514, 0.19090188 ), 
   vec2( -0.24188840, 0.99706507 ), 
   vec2( -0.81409955, 0.91437590 ), 
   vec2( 0.19984126, 0.78641367 ), 
   vec2( 0.14383161, -0.14100790 ) 
);

float random(vec3 seed, int i){
	vec4 seed4 = vec4(seed,i);
	float dot_product = dot(seed4, vec4(12.9898,78.233,45.164,94.673));
	return fract(sin(dot_product) * 43758.5453);
}

void main(void){

	
	
	vec3 unitNormal = normalize(surfaceNormal);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	int i = 0;
	//for(int i = 0; i < 4; i++){	
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
	//}
	
	totalDiffuse = max(totalDiffuse, 0.0);
	
	
	float shadow = 1.0;
	
	if(shadows > 0.5){
		if(textureProj(shadowMap, shadowCoord) < 0.5){
			shadow = 0.5;
		}
	}
	
	
	
	
	vec4 sampleX = texture(textureSampler, worldPosition.yz);
	vec4 sampleY = texture(textureSampler, worldPosition.xz);
	vec4 sampleZ = texture(textureSampler, worldPosition.xy);
	
	vec4 blendedColour = sampleX * surfaceNormal.x + sampleY * surfaceNormal.y + sampleZ * surfaceNormal.z;
	
	float ambient = 0.1;
	out_Color = shadow * vec4(totalDiffuse, 1.0) * blendedColour + shadow * vec4(totalSpecular, 1.0) + ambient ;
	//////////////
	
	out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);
}