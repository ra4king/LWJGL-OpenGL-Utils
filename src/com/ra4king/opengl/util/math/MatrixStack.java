package com.ra4king.opengl.util.math;

import java.util.Stack;

public class MatrixStack {
	private Stack<Matrix4> stack;
	private Matrix4 current;
	
	public MatrixStack() {
		current = new Matrix4().clearToIdentity();
		stack = new Stack<>();
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
