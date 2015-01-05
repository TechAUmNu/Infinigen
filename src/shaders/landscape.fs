#version 120

uniform vec4 cameraPosition;
varying vec3 varyingNormal;
varying vec4 varyingVertex;



void main() {
	vec4 colour = vec4(1.0, 1.0, 1.0, 1.0);
    vec4 vertexPosition = (gl_ModelViewMatrix * varyingVertex);
    vec3 surfaceNormal = normalize((gl_NormalMatrix * varyingNormal));
    vec3 lightDirection = normalize((gl_LightSource[0].position - vertexPosition).xyz);
    vec3 surfaceToCamera = normalize(cameraPosition - vertexPosition).xyz;
    
    //diffuse
    float diffuseLightIntensity = max(0, dot(surfaceNormal, lightDirection));
    
    gl_FragColor.rgb = diffuseLightIntensity * colour.rgb;
    
    //ambient
    gl_FragColor += gl_LightModel.ambient;
    
    //specular
    float specularCoefficient = 10;
    if(diffuseLightIntensity > 0.0){
        specularCoefficient = pow(max(0.0, dot(surfaceToCamera, -reflect(lightDirection, surfaceNormal))), gl_FrontMaterial.shininess);    
    
    	specularCoefficient = specularCoefficient / 0.90;
  	}
    //gl_FragColor += specularCoefficient;
   
    
}


