package com.ra4king.opengl.util.scene.binders;

import static org.lwjgl.opengl.GL20.*;

import com.ra4king.opengl.util.ShaderProgram;
import com.ra4king.opengl.util.math.Vector4;

import net.indiespot.struct.cp.CopyStruct;
import net.indiespot.struct.cp.Struct;

/**
 * @author Roi Atalla
 */
public class UniformVec4Binder extends UniformBinderBase {
	private Vector4 value = Struct.malloc(Vector4.class).set(0f);
	
	public UniformVec4Binder() {}
	
	public UniformVec4Binder(Vector4 vec) {
		setValue(vec);
	}
	
	public void setValue(Vector4 vec) {
		value.set(vec);
	}
	
	@CopyStruct
	public Vector4 getValue() {
		return value;
	}
	
	@Override
	public void bindState(ShaderProgram program) {
		glUniform4(getUniformLocation(program), value.toBuffer());
	}
	
	@Override
	public void unbindState(ShaderProgram program) {}
}
