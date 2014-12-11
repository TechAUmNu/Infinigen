#version 120

varying vec4 varyingColour;

varying vec3 varyingNormal;

varying vec4 varyingVertex;

varying float varyingHeight;

void main() {
   
    varyingColour = gl_FrontMaterial.diffuse;
 
    varyingNormal = gl_Normal;
    
    varyingVertex = gl_Vertex;
    //varyingHeight = gl_Vertex.y / (255.0 * 5);
    
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}





