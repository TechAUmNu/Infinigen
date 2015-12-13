package main.java.com.ionsystems.infinigen.physics;

import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.ShapeHull;

public class PhysicsUtils {

	public static ConvexHullShape simplifyConvexShape(ConvexHullShape shape) {
		ShapeHull hull = new ShapeHull(shape);
		float margin = shape.getMargin();
		hull.buildHull(margin);
		ConvexHullShape simplifiedConvexHullShape = new ConvexHullShape(hull.getVertexPointer());
		return simplifiedConvexHullShape;
	}

}
