package com.ra4king.opengl.util.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author Roi Atalla
 */
public class Matrix3 {
	public static final int LENGTH = 9;
	
	private float[] matrix;
	
	/**
	 * Clears to identity
	 */
	public Matrix3() {
		matrix = new float[LENGTH];
		clearToIdentity();
	}
	
	public Matrix3(float[] m) {
		this();
		set(m);
	}
	
	public Matrix3(Matrix3 m) {
		this();
		set(m);
	}
	
	public Matrix3(Matrix4 m) {
		this();
		set(m);
	}
	
	public Matrix3 copy() {
		return new Matrix3(this);
	}
	
	/**
	 * Clears the matrix to all 0's 
	 */
	public Matrix3 clear() {
		for(int a = 0; a < LENGTH; a++)
			matrix[a] = 0;
		
		return this;
	}
	
	public Matrix3 clearToIdentity() {
		return clear().put(0, 1).put(4, 1).put(8, 1);
	}
	
	public float get(int index) {
		return matrix[index];
	}
	
	public float get(int col, int row) {
		return matrix[col * 3 + row];
	}
	
	public Matrix3 put(int index, float f) {
		matrix[index] = f;
		return this;
	}
	
	public Matrix3 put(int col, int row, float f) {
		matrix[col * 3 + row] = f;
		return this;
	}
	
	public Matrix3 putColumn(int index, Vector3 v) {
		put(index, 0, v.x());
		put(index, 1, v.y());
		put(index, 2, v.z());
		return this;
	}
	
	public Matrix3 set(float[] m) {
		if(m.length < LENGTH) {
			throw new IllegalArgumentException("float array must have at least " + LENGTH + " values.");
		}
		
		System.arraycopy(m, 0, this.matrix, 0, LENGTH);
		return this;
	}
	
	public Matrix3 set(Matrix3 m) {
		System.arraycopy(m.matrix, 0, this.matrix, 0, LENGTH);
		return this;
	}
	
	public Matrix3 set(Matrix4 m) {
		for(int a = 0; a < 3; a++) {
			put(a, 0, m.get(a, 0));
			put(a, 1, m.get(a, 1));
			put(a, 2, m.get(a, 2));
		}
		
		return this;
	}
	
	public Matrix3 mult(float f) {
		for(int a = 0; a < LENGTH; a++)
			put(a, get(a) * f);
		
		return this;
	}
	
	public Matrix3 mult(float[] m) {
		if(m.length < LENGTH) {
			throw new IllegalArgumentException("float array must have at least " + LENGTH + " values.");
		}
		
		return mult(new Matrix3(m));
	}
	
	public Matrix3 mult(Matrix3 m) {
		Matrix3 temp = new Matrix3();
		
		for(int a = 0; a < 3; a++) {
			temp.put(a, 0, matrix[0] * m.get(a, 0) + matrix[3] * m.get(a, 1) + matrix[6] * m.get(a, 2));
			temp.put(a, 1, matrix[1] * m.get(a, 0) + matrix[4] * m.get(a, 1) + matrix[7] * m.get(a, 2));
			temp.put(a, 2, matrix[2] * m.get(a, 0) + matrix[5] * m.get(a, 1) + matrix[8] * m.get(a, 2));
		}
		
		set(temp);
		
		return this;
	}
	
	public Vector3 mult(Vector3 vec) {
		return mult(vec, new Vector3());
	}
	
	public Vector3 mult(Vector3 vec, Vector3 result) {
		return result.set(matrix[0] * vec.x() + matrix[3] * vec.y() + matrix[6] * vec.z(),
		                   matrix[1] * vec.x() + matrix[4] * vec.y() + matrix[7] * vec.z(),
		                   matrix[2] * vec.x() + matrix[5] * vec.y() + matrix[8] * vec.z());
	}
	
	public Matrix3 transpose() {
		float old = matrix[1];
		put(1, matrix[3]);
		put(3, old);
		
		old = matrix[2];
		put(2, matrix[6]);
		put(6, old);
		
		old = matrix[5];
		put(5, matrix[7]);
		put(7, old);
		
		return this;
	}
	
	public float determinant() {
		return +matrix[0] * matrix[4] * matrix[8] + matrix[3] * matrix[7] * matrix[2] + matrix[6] * matrix[1] * matrix[5]
		         - matrix[2] * matrix[4] * matrix[6] - matrix[5] * matrix[7] * matrix[0] - matrix[8] * matrix[1] * matrix[3];
	}
	
	public Matrix3 inverse() {
		Matrix3 inv = new Matrix3();
		
		inv.put(0, +(matrix[4] * matrix[8] - matrix[5] * matrix[7]));
		inv.put(1, -(matrix[3] * matrix[8] - matrix[5] * matrix[6]));
		inv.put(2, +(matrix[3] * matrix[7] - matrix[4] * matrix[6]));
		
		inv.put(3, -(matrix[1] * matrix[8] - matrix[2] * matrix[7]));
		inv.put(4, +(matrix[0] * matrix[8] - matrix[2] * matrix[6]));
		inv.put(5, -(matrix[0] * matrix[7] - matrix[1] * matrix[6]));
		
		inv.put(6, +(matrix[1] * matrix[5] - matrix[2] * matrix[4]));
		inv.put(7, -(matrix[0] * matrix[5] - matrix[2] * matrix[3]));
		inv.put(8, +(matrix[0] * matrix[4] - matrix[1] * matrix[3]));
		
		return set(inv.transpose().mult(1 / determinant()));
	}
	
	private static final FloatBuffer direct = BufferUtils.createFloatBuffer(LENGTH);
	
	public FloatBuffer toBuffer() {
		direct.clear();
		for(int a = 0; a < LENGTH; a++) {
			direct.put(matrix[a]);
		}
		direct.flip();
		return direct;
	}
}
