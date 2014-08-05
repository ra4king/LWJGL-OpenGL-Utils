package com.ra4king.opengl.util.math;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Roi Atalla
 */
public class MatrixStack {
	private Deque<Matrix4> stack;
	private Matrix4 current;
	
	public MatrixStack() {
		current = new Matrix4().clearToIdentity();
		stack = new ArrayDeque<>();
	}
	
	public MatrixStack clear() {
		stack.clear();
		current = null;
		return this;
	}
	
	public Matrix4 getTop() {
		return current;
	}
	
	public MatrixStack setTop(Matrix4 m) {
		current = m;
		return this;
	}
	
	public MatrixStack pushMatrix() {
		stack.push(current);
		current = new Matrix4(current);
		return this;
	}
	
	public MatrixStack popMatrix() {
		current = stack.pop();
		return this;
	}
}
