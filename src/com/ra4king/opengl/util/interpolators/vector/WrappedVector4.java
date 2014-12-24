package com.ra4king.opengl.util.interpolators.vector;

import java.nio.FloatBuffer;

import com.ra4king.opengl.util.math.Vector4;

import net.indiespot.struct.cp.Struct;
import net.indiespot.struct.cp.TakeStruct;

/**
 * @author Roi Atalla
 */
public class WrappedVector4 implements IVector<WrappedVector4,Vector4> {
	private Vector4 vec;
	
	public WrappedVector4() {
		vec = Struct.malloc(Vector4.class).set(0);
	}
	
	public WrappedVector4(WrappedVector4 wv) {
		this();
		set(wv);
	}
	
	public WrappedVector4(Vector4 v) {
		this();
		set(v);
	}
	
	@Override
	@TakeStruct
	public Vector4 getVec() {
		return vec;
	}
	
	@Override
	public WrappedVector4 copy() {
		return new WrappedVector4(this);
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
	public WrappedVector4 set(WrappedVector4 wv) {
		vec.set(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector4 set(Vector4 v) {
		vec.set(v);
		return this;
	}
	
	@Override
	public WrappedVector4 set(float f) {
		vec.set(f);
		return this;
	}
	
	@Override
	public WrappedVector4 add(WrappedVector4 wv) {
		vec.add(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector4 add(Vector4 v) {
		vec.add(v);
		return this;
	}
	
	@Override
	public WrappedVector4 sub(WrappedVector4 wv) {
		vec.sub(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector4 sub(Vector4 v) {
		vec.sub(v);
		return this;
	}
	
	@Override
	public WrappedVector4 mult(WrappedVector4 wv) {
		vec.mult(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector4 mult(Vector4 v) {
		vec.mult(v);
		return this;
	}
	
	@Override
	public WrappedVector4 mult(float f) {
		vec.mult(f);
		return this;
	}
	
	@Override
	public WrappedVector4 divide(WrappedVector4 wv) {
		vec.divide(wv.vec);
		return this;
	}
	
	@Override
	public WrappedVector4 divide(Vector4 v) {
		vec.divide(v);
		return this;
	}
	
	@Override
	public WrappedVector4 divide(float f) {
		vec.divide(f);
		return this;
	}
	
	@Override
	public WrappedVector4 mod(float f) {
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
