package main.java.com.ionsystems.infinigen.newNetworking;

import java.io.Serializable;


import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;

public class PhysicsNetworkBody implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -815178468615812742L;
	int hash;
	Vector3f linearVelocity;
	Vector3f angularVelocity;
	Transform worldTransform;
	Quat4f orientation;
}
