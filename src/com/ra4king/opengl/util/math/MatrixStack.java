package com.ra4king.opengl.util.math;

import java.util.ArrayList;

/**
 * @author Roi Atalla
 */
public class MatrixStack {
	private ArrayList<Matrix4> stack = new ArrayList<>();
	private int currIdx;

	public MatrixStack() {
		clear();
	}

	public MatrixStack clear() {
		stack.clear();
		stack.add(new Matrix4());
		currIdx = 0;
		return this;
	}

	public Matrix4 getTop() {
		return stack.get(currIdx);
	}

	public MatrixStack setTop(Matrix4 m) {
		stack.get(currIdx).set(m);
		return this;
	}

	public MatrixStack pushMatrix() {
		stack.add(new Matrix4(getTop()));
		currIdx++;
		return this;
	}

	public MatrixStack popMatrix() {
		if (currIdx == 0) {
			throw new IllegalStateException("Already at the bottom of the stack.");
		}

		stack.remove(currIdx);
		currIdx--;
		return this;
	}
}
