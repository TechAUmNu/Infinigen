#version 120

varying vec4 varyingColour;

varying vec3 varyingNormal;

varying vec4 varyingVertex;



void main() {
   
    varyingColour = gl_FrontMaterial.diffuse;
 
    varyingNormal = gl_Normal;
    
    varyingVertex = gl_Vertex;    
    
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}





