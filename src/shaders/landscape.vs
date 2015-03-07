#version 430

varying vec4 varyingColour;

varying vec3 varyingNormal;

varying vec4 varyingVertex;



void main() {
   
   	gl_TexCoord[0] = gl_MultiTexCoord0;
    varyingColour = gl_FrontMaterial.diffuse;
 
    varyingNormal = gl_Normal;
    
    varyingVertex = gl_Vertex;    
    
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}





