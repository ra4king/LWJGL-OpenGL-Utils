package com.ra4king.opengl.util.math;

import java.nio.FloatBuffer;

/**
 * @author Roi Atalla
 */
public interface Vector<V> {
	V add(V v);
	
	V sub(V v);
	
	V mult(V v);
	
	V mult(float f);
	
	V divide(V v);
	
	V divide(float f);
	
	V mod(float f);
	
	float length();
	
	float lengthSquared();
	
	V copy();
	
	FloatBuffer toBuffer();
	
	@Override
	String toString();
}
