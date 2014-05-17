package com.ra4king.opengl.util.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author Roi Atalla
 */
public class Vector4 implements Vector<Vector4> {
	private float x, y, z, w;
	
	public static final Vector4 RIGHT = new Vector4(1, 0, 0, 1);
	public static final Vector4 LEFT = new Vector4(-1, 0, 0, 1);
	public static final Vector4 UP = new Vector4(0, 1, 0, 1);
	public static final Vector4 DOWN = new Vector4(0, -1, 0, 1);
	public static final Vector4 FORWARD = new Vector4(0, 0, -1, 1);
	public static final Vector4 BACK = new Vector4(0, 0, 1, 1);
	
	public Vector4() {
		set(0, 0, 0, 0);
	}
	
	public Vector4(float v) {
		this(v, v, v, v);
	}
	
	public Vector4(float x, float y, float z, float w) {
		set(x, y, z, w);
	}
	
	public Vector4(Vector2 vec) {
		set(vec);
	}
	
	public Vector4(Vector2 vec, float z, float w) {
		set(vec, z, w);
	}
	
	public Vector4(Vector3 vec) {
		set(vec);
	}
	
	public Vector4(Vector3 vec, float w) {
		set(vec, w);
	}
	
	public Vector4(Vector4 vec) {
		set(vec);
	}
	
	@Override
	public Vector4 copy() {
		return new Vector4(this);
	}
	
	public float x() {
		return x;
	}
	
	public Vector4 x(float x) {
		this.x = x;
		return this;
	}
	
	public float y() {
		return y;
	}
	
	public Vector4 y(float y) {
		this.y = y;
		return this;
	}
	
	public float z() {
		return z;
	}
	
	public Vector4 z(float z) {
		this.z = z;
		return this;
	}
	
	public float w() {
		return w;
	}
	
	public Vector4 w(float w) {
		this.w = w;
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Vector4) {
			Vector4 v = (Vector4)o;
			return x == v.x && y == v.y && z == v.z && w == v.w;
		}
		
		return false;
	}
	
	public Vector4 set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}
	
	public Vector4 set(Vector2 vec) {
		return set(vec, 0, 0);
	}
	
	public Vector4 set(Vector2 vec, float z, float w) {
		return set(vec.x(), vec.y(), z, w);
	}
	
	public Vector4 set(Vector3 vec) {
		return set(vec, 0);
	}
	
	public Vector4 set(Vector3 vec, float w) {
		return set(vec.x(), vec.y(), vec.z(), w);
	}
	
	public Vector4 set(Vector4 vec) {
		return set(vec.x, vec.y, vec.z, vec.w);
	}
	
	public Vector4 reset() {
		x = y = z = w = 0;
		return this;
	}
	
	@Override
	public float length() {
		return (float)Math.sqrt(x * x + y * y + z * z + w * w);
	}
	
	@Override
	public float lengthSquared() {
		return x * x + y * y + z * z + w * w;
	}
	
	public Vector4 normalize() {
		float length = length();
		x /= length;
		y /= length;
		z /= length;
		w /= length;
		return this;
	}
	
	public float dot(Vector4 vec) {
		return x * vec.x + y * vec.y + z * vec.z + w * vec.w;
	}
	
	public Vector4 add(float x, float y, float z, float w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
		return this;
	}
	
	@Override
	public Vector4 add(Vector4 vec) {
		return add(vec.x, vec.y, vec.z, vec.w);
	}
	
	public Vector4 sub(float x, float y, float z, float w) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		this.w -= w;
		return this;
	}
	
	@Override
	public Vector4 sub(Vector4 vec) {
		return sub(vec.x, vec.y, vec.z, vec.w);
	}
	
	@Override
	public Vector4 mult(float f) {
		return mult(f, f, f, f);
	}
	
	public Vector4 mult(float x, float y, float z, float w) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		this.w *= w;
		return this;
	}
	
	@Override
	public Vector4 mult(Vector4 vec) {
		return mult(vec.x, vec.y, vec.z, vec.w);
	}
	
	@Override
	public Vector4 divide(float f) {
		return divide(f, f, f, f);
	}
	
	public Vector4 divide(float x, float y, float z, float w) {
		this.x /= x;
		this.y /= y;
		this.z /= z;
		this.w /= w;
		return this;
	}
	
	@Override
	public Vector4 divide(Vector4 vec) {
		return divide(vec.x, vec.y, vec.z, vec.w);
	}
	
	@Override
	public Vector4 mod(float f) {
		x %= f;
		y %= f;
		z %= f;
		w %= f;
		
		return this;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}
	
	private final static FloatBuffer direct = BufferUtils.createFloatBuffer(4);
	
	@Override
	public FloatBuffer toBuffer() {
		direct.clear();
		direct.put(x).put(y).put(z).put(w);
		direct.flip();
		return direct;
	}
}
