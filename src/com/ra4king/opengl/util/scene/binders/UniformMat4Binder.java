package com.ra4king.opengl.util.scene.binders;

import static org.lwjgl.opengl.GL20.*;

import com.ra4king.opengl.util.ShaderProgram;
import com.ra4king.opengl.util.math.Matrix4;

import net.indiespot.struct.cp.CopyStruct;
import net.indiespot.struct.cp.Struct;

/**
 * @author Roi Atalla
 */
public class UniformMat4Binder extends UniformBinderBase {
	private Matrix4 value = Struct.malloc(Matrix4.class).clearToIdentity();
	
	public UniformMat4Binder() {}
	
	public UniformMat4Binder(Matrix4 mat) {
		setValue(mat);
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			Struct.free(value);
		} finally {
			super.finalize();
		}
	}
	
	public void setValue(Matrix4 mat) {
		value.set(mat);
	}
	
	@CopyStruct
	public Matrix4 getValue() {
		return value;
	}
	
	@Override
	public void bindState(ShaderProgram program) {
		glUniformMatrix4(getUniformLocation(program), false, value.toBuffer());
	}
	
	@Override
	public void unbindState(ShaderProgram program) {}
}
