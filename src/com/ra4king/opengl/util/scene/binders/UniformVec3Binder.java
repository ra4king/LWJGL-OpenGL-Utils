package com.ra4king.opengl.util.scene.binders;

import static org.lwjgl.opengl.GL20.*;

import com.ra4king.opengl.util.ShaderProgram;
import com.ra4king.opengl.util.math.Vector3;

import net.indiespot.struct.cp.CopyStruct;
import net.indiespot.struct.cp.Struct;

/**
 * @author Roi Atalla
 */
public class UniformVec3Binder extends UniformBinderBase {
	private Vector3 value = Struct.malloc(Vector3.class).set(0f);
	
	public UniformVec3Binder() {}
	
	public UniformVec3Binder(Vector3 vec) {
		setValue(vec);
	}
	
	public void setValue(Vector3 vec) {
		value.set(vec);
	}
	
	@CopyStruct
	public Vector3 getValue() {
		return value;
	}
	
	@Override
	public void bindState(ShaderProgram program) {
		glUniform3(getUniformLocation(program), value.toBuffer());
	}
	
	@Override
	public void unbindState(ShaderProgram program) {}
}
