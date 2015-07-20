package com.ra4king.opengl.util.math;

/**
 * @author Roi Atalla
 */
public class Quaternion {
	private float x, y, z, w;
	
	public Quaternion() {
		reset();
	}
	
	public Quaternion(float x, float y, float z, float w) {
		set(x, y, z, w);
	}
	
	/**
	 * Angle is in radians
	 */
	public Quaternion(float angle, Vector3 vec) {
		float s = (float)Math.sin(angle / 2);
		
		x = vec.x() * s;
		y = vec.y() * s;
		z = vec.z() * s;
		w = (float)Math.cos(angle / 2);
	}
	
	public Quaternion(Quaternion q) {
		set(q);
	}
	
	public float x() {
		return x;
	}
	
	public Quaternion x(float x) {
		this.x = x;
		return this;
	}
	
	public float y() {
		return y;
	}
	
	public Quaternion y(float y) {
		this.y = y;
		return this;
	}
	
	public float z() {
		return z;
	}
	
	public Quaternion z(float z) {
		this.z = z;
		return this;
	}
	
	public float w() {
		return w;
	}
	
	public Quaternion w(float w) {
		this.w = w;
		return this;
	}
	
	public Quaternion set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}
	
	public Quaternion set(Quaternion q) {
		return set(q.x, q.y, q.z, q.w);
	}
	
	public Quaternion reset() {
		x = 0;
		y = 0;
		z = 0;
		w = 1;
		return this;
	}
	
	public float length() {
		return (float)Math.sqrt(x * x + y * y + z * z + w * w);
	}
	
	public Quaternion normalize() {
		float length = 1f / length();
		x *= length;
		y *= length;
		z *= length;
		w *= length;
		return this;
	}
	
	public float dot(Quaternion q) {
		return x * q.x + y * q.y + z * q.z + w * q.w;
	}
	
	public Vector3 mult(Vector3 v) {
		return mult(v, new Vector3());
	}
	
	public Vector3 mult(Vector3 v, Vector3 result) {
		Vector3 quatVector = new Vector3(x, y, z);
		
		Vector3 uv = quatVector.cross(v);
		Vector3 uuv = quatVector.cross(uv);
		
		uv.mult(w * 2);
		uuv.mult(2);
		
		return result.set(v).add(uv).add(uuv);
	}
	
	public Quaternion mult(Quaternion q) {
		float xx = w * q.x + x * q.w + y * q.z - z * q.y;
		float yy = w * q.y + y * q.w + z * q.x - x * q.z;
		float zz = w * q.z + z * q.w + x * q.y - y * q.x;
		float ww = w * q.w - x * q.x - y * q.y - z * q.z;
		
		x = xx;
		y = yy;
		z = zz;
		w = ww;
		
		return this;
	}
	
	public Quaternion conjugate() {
		x *= -1;
		y *= -1;
		z *= -1;
		
		return this;
	}
	
	public Quaternion inverse() {
		return normalize().conjugate();
	}
	
	public Matrix4 toMatrix() {
		return toMatrix(new Matrix4());
	}
	
	public Matrix4 toMatrix(Matrix4 mat4) {
		return mat4.set(new float[] {
				1 - 2 * y * y - 2 * z * z, 2 * x * y + 2 * w * z, 2 * x * z - 2 * w * y, 0,
				2 * x * y - 2 * w * z, 1 - 2 * x * x - 2 * z * z, 2 * y * z + 2 * w * x, 0,
				2 * x * z + 2 * w * y, 2 * y * z - 2 * w * x, 1 - 2 * x * x - 2 * y * y, 0,
				0, 0, 0, 1,
		});
	}
	
	@Override
	public String toString() {
		return "(" + x + "i + " + y + "j + " + z + "k + " + w + ")";
	}
}
