package com.ra4king.opengl.util.interpolators.vector;

import java.nio.FloatBuffer;

/**
 * @author Roi Atalla
 * 
 *         V = the type that is extending IVector
 *         K = the type that is being wrapped
 */
public interface IVector<V extends IVector<V,K>, K> {
	K getVec();
	
	V copy();
	
	V set(V v);
	
	V set(K k);
	
	V set(float f);
	
	V add(V v);
	
	V add(K k);
	
	V sub(V v);
	
	V sub(K K);
	
	V mult(V v);
	
	V mult(K k);
	
	V mult(float f);
	
	V divide(V v);
	
	V divide(K k);
	
	V divide(float f);
	
	V mod(float f);
	
	float length();
	
	float lengthSquared();
	
	FloatBuffer toBuffer();
	
	@Override
	String toString();
}
