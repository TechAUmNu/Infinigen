package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import utility.EulerCamera;
import world.Block.BlockType;
import world.ChunkManager;

/**
 * Main designer class which handles creation of entities
 * @author Euan
 *
 */
public class Designer {
	public void initDesigner(ChunkManager cm, EulerCamera camera){
		//Generate a few chunks for a floor somewhere that nothing should be
		cm.GenerateChunk(10000, 10000, 10000, BlockType.BlockType_Dirt);
		cm.GenerateChunk(10001, 10000, 10000,BlockType.BlockType_Dirt);
		cm.GenerateChunk(10000, 10000, 10001, BlockType.BlockType_Dirt);
		cm.GenerateChunk(10001, 10000, 10001, BlockType.BlockType_Dirt);		
		camera.setPosition(320000, 320000, 320000);
	}
	
	
	public void processKeyboard(){
		while (Keyboard.next()) {
	        if (Keyboard.getEventKeyState()) {
	            if (Keyboard.getEventKey() == Keyboard.KEY_A) {
	            System.out.println("A Key Pressed");
	        }
	        if (Keyboard.getEventKey() == Keyboard.KEY_S) {
	            System.out.println("S Key Pressed");
	        }
	        if (Keyboard.getEventKey() == Keyboard.KEY_D) {
	            System.out.println("D Key Pressed");
	        }
	        } else {
	            if (Keyboard.getEventKey() == Keyboard.KEY_A) {
	            System.out.println("A Key Released");
	            }
	            if (Keyboard.getEventKey() == Keyboard.KEY_S) {
	            System.out.println("S Key Released");
	        }
	        if (Keyboard.getEventKey() == Keyboard.KEY_D) {
	            System.out.println("D Key Released");
	        }
	        }
	    }
		
		//process keyboard inputs for changing block etc
	}
	public static void processMouse(){
		//Process mouse for changing block/placing deleting blocks
		while(Mouse.next()) {
		    if (Mouse.getEventButton() > -1) {
		        if (Mouse.getEventButtonState()) {
		            System.out.println("PRESSED MOUSE BUTTON: " + Mouse.getEventButton());
		        }
		        else System.out.println("RELEASED MOUSE BUTTON: " + Mouse.getEventButton());
		    }
		}
	}
	
	
	/*public Ray GetPickRay() {
	    int mouseX = Mouse.getX();
	    int mouseY = WORLD.Byte56Game.getHeight() - Mouse.getY();

	    float windowWidth = WORLD.Byte56Game.getWidth();
	    float windowHeight = WORLD.Byte56Game.getHeight();

	    //get the mouse position in screenSpace coords
	    double screenSpaceX = ((float) mouseX / (windowWidth / 2) - 1.0f) * aspectRatio;
	    double screenSpaceY = (1.0f - (float) mouseY / (windowHeight / 2));

	    double viewRatio = Math.tan(((float) Math.PI / (180.f/ViewAngle) / 2.00f)) * zoomFactor;

	    screenSpaceX = screenSpaceX * viewRatio;
	    screenSpaceY = screenSpaceY * viewRatio;

	    //Find the far and near camera spaces
	    Vector4f cameraSpaceNear = new Vector4f((float) (screenSpaceX * NearPlane), (float) (screenSpaceY * NearPlane), (float) (-NearPlane), 1);
	    Vector4f cameraSpaceFar = new Vector4f((float) (screenSpaceX * FarPlane), (float) (screenSpaceY * FarPlane), (float) (-FarPlane), 1);


	    //Unproject the 2D window into 3D to see where in 3D we're actually clicking
	    Matrix4f tmpView = Matrix4f(view);
	    Matrix4f invView = (Matrix4f) tmpView.invert();
	    Vector4f worldSpaceNear = new Vector4f();
	    Matrix4f.transform(invView, cameraSpaceNear, worldSpaceNear);

	    Vector4f worldSpaceFar = new Vector4f();

	    Matrix4f.transform(invView, cameraSpaceFar, worldSpaceFar);

	    //calculate the ray position and direction
	    Vector3f rayPosition = new Vector3f(worldSpaceNear.x, worldSpaceNear.y, worldSpaceNear.z);
	    Vector3f rayDirection = new Vector3f(worldSpaceFar.x - worldSpaceNear.x, worldSpaceFar.y - worldSpaceNear.y, worldSpaceFar.z - worldSpaceNear.z);

	    rayDirection.normalise();

	    return new Ray(rayPosition, rayDirection);
	}
	
	public static float RayIntersectsTriangle(Ray R, Vector3f vertex1, Vector3f vertex2, Vector3f vertex3) {
	    // Compute vectors along two edges of the triangle.
	    Vector3f edge1 = null, edge2 = null;

	    edge1 = Vector3f.sub(vertex2, vertex1, edge1);
	    edge2 = Vector3f.sub(vertex3, vertex1, edge2);

	    // Compute the determinant.
	    Vector3f directionCrossEdge2 = null;
	    directionCrossEdge2 = Vector3f.cross(R.Direction, edge2, directionCrossEdge2);


	    float determinant = Vector3f.dot(directionCrossEdge2, edge1);
	    // If the ray and triangle are parallel, there is no collision.
	    if (determinant > -.0000001f && determinant < .0000001f) {
	        return Float.MAX_VALUE;
	    }

	    float inverseDeterminant = 1.0f / determinant;

	    // Calculate the U parameter of the intersection point.
	    Vector3f distanceVector = null;
	    distanceVector = Vector3f.sub(R.Position, vertex1, distanceVector);


	    float triangleU = Vector3f.dot(directionCrossEdge2, distanceVector);
	    triangleU *= inverseDeterminant;

	    // Make sure the U is inside the triangle.
	    if (triangleU < 0 || triangleU > 1) {
	        return Float.MAX_VALUE;
	    }

	    // Calculate the V parameter of the intersection point.
	    Vector3f distanceCrossEdge1 = null;
	    distanceCrossEdge1 = Vector3f.cross(distanceVector, edge1, distanceCrossEdge1);


	    float triangleV = Vector3f.dot(R.Direction, distanceCrossEdge1);
	    triangleV *= inverseDeterminant;

	    // Make sure the V is inside the triangle.
	    if (triangleV < 0 || triangleU + triangleV > 1) {
	        return Float.MAX_VALUE;
	    }

	    // Get the distance to the face from our ray origin
	    float rayDistance = Vector3f.dot(distanceCrossEdge1, edge2);
	    rayDistance *= inverseDeterminant;


	    // Is the triangle behind us?
	    if (rayDistance < 0) {
	        rayDistance *= -1;
	        return Float.MAX_VALUE;
	    }
	    return rayDistance;
	}
	*/
	
	public void addBlock(Position p){
		//Adds a block to the current entity
	}
	
	public void removeBlock(Position p){
		//Removes a block from the current entity
	}
	
	
	
	public void generateEntity(Entity e){
		
	}
	
	public void setEntity(Entity e){
		
	}
	
	
	
}
