#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;
in vec4 worldPosition;


//Shadows///
in vec4 shadowCoord;
uniform sampler2DShadow shadowMap;
////////////////////////////

out vec4 out_Color;

uniform sampler2D textureSampler;

uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;
uniform float highlight;


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
	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i=0; i<4; i++){
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		
		vec3 unitLightVector = normalize(toLightVector[i]);	
		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.0);	
		vec3 lightDirection = -unitVectorToCamera;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColour[i])/attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i])/attFactor;
	}
	
	totalDiffuse = max(totalDiffuse, 0.0);
	
	vec4 textureColour = texture(textureSampler, pass_textureCoords);
	if(textureColour.a<0.5){
		discard;
	}
	
	
	
	///Shadows////
	float shadow=1.0;
	float bias = 0.00005;
	
	for (int i=0;i<4;i++){
		// use either :
		//  - Always the same samples.
		//    Gives a fixed pattern in the shadow, but no noise
		int index = i;
		//  - A random sample, based on the pixel's screen location. 
		//    No banding, but the shadow moves with the camera, which looks weird.
		// int index = int(16.0*random(gl_FragCoord.xyy, i))%16;
		//  - A random sample, based on the pixel's position in world space.
		//    The position is rounded to the millimeter to avoid too much aliasing
		//int index = int(16.0*random(floor(worldPosition.xyz*100.0), i))%16;
		
		// being fully in the shadow will eat up 4*0.2 = 0.8
		// 0.2 potentially remain, which is quite dark.
		shadow -= 0.2*(1.0-texture( shadowMap, vec3(shadowCoord.xy + poissonDisk[index]/700.0,  (shadowCoord.z-bias)/shadowCoord.w) ));
	}
	
	//shadow -= (1.0-textureProj(shadowMap, shadowCoord));
	
	
	
	
	
	
	
	
	
	
	//float shadow = textureProj(shadowMap, shadowCoord) / 2.0;
	

	out_Color = shadow * vec4(totalDiffuse, 1.0) * texture(textureSampler, pass_textureCoords) + shadow * vec4(totalSpecular, 1.0) ;
	//////////////
	
	
	if(highlight > 0.5){
		out_Color = mix(vec4(0.5, 0.5, 0.5, 1.0), out_Color, 0.5);
	}
	
	//out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);
}