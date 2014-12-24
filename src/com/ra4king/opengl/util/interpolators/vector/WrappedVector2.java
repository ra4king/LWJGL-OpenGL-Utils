package com.ra4king.opengl.util.interpolators.vector;

import java.nio.FloatBuffer;

import com.ra4king.opengl.util.math.Vector2;

import net.indiespot.struct.cp.Struct;

/**
 * @author Roi Atalla
 */
public class WrappedVector2 implements IVector<WrappedVector2,Vector2> {
	private Vector2 vec;
	
	public WrappedVector2() {
		vec = Struct.malloc(Vector2.class).set(0);
	}
	
	public WrappedVector2(WrappedVector2 wv) {
		this();
		set(wv);
	}
	
	public WrappedVector2(Vector2 v) {
		this();
		set(v);
	}
	
	@Override
	public Vector2 getVec() {
		return vec;
	}
	
	@Override
	public WrappedVector2 copy() {
		return new WrappedVector2(this);
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			Struct.free(vec);
		} finally {
			super.finalize();
		}
	}
	
	@Override
	public WrappedVector2 set(WrappedVector2 wv) {
		vec.set(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector2 set(Vector2 v) {
		vec.set(v);
		return this;
	}
	
	@Override
	public WrappedVector2 set(float f) {
		vec.set(f);
		return this;
	}
	
	@Override
	public WrappedVector2 add(WrappedVector2 wv) {
		vec.add(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector2 add(Vector2 v) {
		vec.add(v);
		return this;
	}
	
	@Override
	public WrappedVector2 sub(WrappedVector2 wv) {
		vec.sub(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector2 sub(Vector2 v) {
		vec.sub(v);
		return this;
	}
	
	@Override
	public WrappedVector2 mult(WrappedVector2 wv) {
		vec.mult(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector2 mult(Vector2 v) {
		vec.mult(v);
		return this;
	}
	
	@Override
	public WrappedVector2 mult(float f) {
		vec.mult(f);
		return this;
	}
	
	@Override
	public WrappedVector2 divide(WrappedVector2 wv) {
		vec.divide(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector2 divide(Vector2 v) {
		vec.divide(v);
		return this;
	}
	
	@Override
	public WrappedVector2 divide(float f) {
		vec.divide(f);
		return this;
	}
	
	@Override
	public WrappedVector2 mod(float f) {
		vec.mod(f);
		return this;
	}
	
	@Override
	public float length() {
		return vec.length();
	}
	
	@Override
	public float lengthSquared() {
		return vec.lengthSquared();
	}
	
	@Override
	public FloatBuffer toBuffer() {
		return vec.toBuffer();
	}
}
