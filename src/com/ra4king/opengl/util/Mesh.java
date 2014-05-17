package com.ra4king.opengl.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import org.lwjgl.opengl.APPLEVertexArrayObject;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GLContext;

/**
 * @author Roi Atalla
 */
public class Mesh {
	private final static boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
	private final static boolean HAS_VAO = GLContext.getCapabilities().OpenGL30;
	
	private int vao;
	private ArrayList<RenderCommand> renderCommands;
	
	public Mesh(ByteBuffer data, ArrayList<Attribute> attributes, ArrayList<RenderCommand> renderCommands) {
		this(data, attributes, renderCommands, null);
	}
	
	public Mesh(ByteBuffer data, ArrayList<Attribute> attributes, ArrayList<RenderCommand> renderCommands, ByteBuffer indices) {
		if(indices == null)
			for(RenderCommand r : renderCommands)
				if(r.isIndexedCmd)
					throw new IllegalArgumentException("One of the render commands requires indices when none is supplied.");
		
		this.renderCommands = renderCommands;
		
		int vbo1 = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo1);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		int vbo2 = -1;
		if(indices != null) {
			vbo2 = glGenBuffers();
			
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo2);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		
		vao = HAS_VAO ? glGenVertexArrays() : (IS_MAC ? APPLEVertexArrayObject.glGenVertexArraysAPPLE() : ARBVertexArrayObject.glGenVertexArrays());
		if(HAS_VAO)
			glBindVertexArray(vao);
		else if(IS_MAC)
			APPLEVertexArrayObject.glBindVertexArrayAPPLE(vao);
		else
			ARBVertexArrayObject.glBindVertexArray(vao);
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo1);
		for(Attribute attrib : attributes) {
			glEnableVertexAttribArray(attrib.index);
			glVertexAttribPointer(attrib.index, attrib.size, attrib.type.dataType, attrib.type.normalized, 0, attrib.offset);
		}
		
		if(vbo2 > -1)
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo2);
		
		if(HAS_VAO)
			glBindVertexArray(0);
		else if(IS_MAC)
			APPLEVertexArrayObject.glBindVertexArrayAPPLE(0);
		else
			ARBVertexArrayObject.glBindVertexArray(0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void render() {
		if(HAS_VAO)
			glBindVertexArray(vao);
		else if(IS_MAC)
			APPLEVertexArrayObject.glBindVertexArrayAPPLE(vao);
		else
			ARBVertexArrayObject.glBindVertexArray(vao);
		
		for(RenderCommand r : renderCommands)
			r.render();
		
		if(HAS_VAO)
			glBindVertexArray(0);
		else if(IS_MAC)
			APPLEVertexArrayObject.glBindVertexArrayAPPLE(0);
		else
			ARBVertexArrayObject.glBindVertexArray(0);
	}
	
	public static class RenderCommand {
		public final boolean isIndexedCmd;
		
		private int primitive;
		private int start;
		private int count;
		private RenderCommandType type;
		
		public RenderCommand(String primitive, String indicesType) {
			this(primitive, RenderCommandType.getRenderType(indicesType));
		}
		
		public RenderCommand(String primitive, RenderCommandType indicesType) {
			this(getPrimitive(primitive), indicesType);
		}
		
		public RenderCommand(int primitive, RenderCommandType indicesType) {
			isIndexedCmd = true;
			this.primitive = primitive;
			this.type = indicesType;
		}
		
		public RenderCommand(String primitive, int start, int count) {
			this(getPrimitive(primitive), start, count);
		}
		
		public RenderCommand(int primitive, int start, int count) {
			isIndexedCmd = false;
			this.primitive = primitive;
			this.start = start;
			this.count = count;
		}
		
		public int getStart() {
			return start;
		}
		
		public int getCount() {
			return count;
		}
		
		public ByteBuffer storeIndices(ByteBuffer b, String[] data) {
			if(count != 0)
				throw new IllegalArgumentException("Data already stored.");
			
			start = b.position();
			count = data.length;
			
			ByteBuffer b2 = ByteBuffer.allocate(start + count * type.size).order(ByteOrder.nativeOrder());
			b2.put((ByteBuffer)b.flip());
			type.parse(b2, data);
			
			return b2;
		}
		
		private static int getPrimitive(String name) {
			switch(name) {
				case "triangles":
					return GL_TRIANGLES;
				case "tri-fan":
					return GL_TRIANGLE_FAN;
				case "tri-strip":
					return GL_TRIANGLE_STRIP;
				case "lines":
					return GL_LINES;
				case "line-strip":
					return GL_LINE_STRIP;
				case "line-loop":
					return GL_LINE_LOOP;
				case "points":
					return GL_POINTS;
				default:
					throw new IllegalArgumentException("Invalid primitive name: " + name);
			}
		}
		
		public void render() {
			if(isIndexedCmd)
				glDrawElements(primitive, count, type.dataType, start);
			else
				glDrawArrays(primitive, start, count);
		}
	}
	
	public static enum RenderCommandType {
		UBYTE("ubyte", GL_UNSIGNED_BYTE, 1) {
			protected void parse(ByteBuffer b, String[] data) {
				for(String s : data)
					b.put((byte)Long.parseLong(s));
			}
		},
		USHORT("ushort", GL_UNSIGNED_SHORT, 2) {
			protected void parse(ByteBuffer b, String[] data) {
				for(String s : data)
					b.putShort((short)Long.parseLong(s));
			}
		},
		UINT("uint", GL_UNSIGNED_INT, 4) {
			protected void parse(ByteBuffer b, String[] data) {
				for(String s : data)
					b.putInt((int)Long.parseLong(s));
			}
		};
		
		public final String name;
		public final int dataType;
		public final int size;
		
		private RenderCommandType(String name, int type, int size) {
			this.name = name;
			this.dataType = type;
			this.size = size;
		}
		
		protected abstract void parse(ByteBuffer b, String[] data);
		
		public static RenderCommandType getRenderType(String name) {
			name = name.toLowerCase();
			
			for(RenderCommandType rt : values())
				if(rt.name.equals(name))
					return rt;
			
			throw new IllegalArgumentException("Unsupported render command type!");
		}
		
		public static RenderCommandType getRenderType(int dataType) {
			for(RenderCommandType rt : values())
				if(rt.dataType == dataType)
					return rt;
			
			throw new IllegalArgumentException("Unsupported render command type!");
		}
	}
	
	public static class Attribute {
		public final int index;
		public final int size;
		
		private AttributeType type;
		private int offset;
		
		public Attribute(int index, String type, int size) {
			this(index, AttributeType.getAttributeType(type), size);
		}
		
		public Attribute(int index, AttributeType type, int size) {
			this.index = index;
			this.type = type;
			this.size = size;
		}
		
		public ByteBuffer storeData(ByteBuffer b, String[] data) {
			offset = b.position();
			
			ByteBuffer b2 = ByteBuffer.allocate(offset + data.length * type.size).order(ByteOrder.nativeOrder());
			b2.put((ByteBuffer)b.flip());
			type.parse(b2, data);
			
			return b2;
		}
	}
	
	public static enum AttributeType {
		FLOAT("float", false, GL_FLOAT, 4),
		INT("int", false, GL_INT, 4), UINT("uint", false, GL_UNSIGNED_INT, 4), NORM_INT("norm-int", true, GL_INT, 4), NORM_UINT("norm-uint", true, GL_UNSIGNED_INT, 4),
		SHORT("short", false, GL_SHORT, 2), USHORT("ushort", false, GL_UNSIGNED_SHORT, 2), NORM_SHORT("norm-short", true, GL_SHORT, 2), NORM_USHORT("norm-ushort", true, GL_UNSIGNED_SHORT, 2),
		BYTE("byte", false, GL_BYTE, 1), UBYTE("ubyte", false, GL_UNSIGNED_BYTE, 1), NORM_BYTE("norm-byte", true, GL_BYTE, 1), NORM_UBYTE("norm-ubyte", true, GL_UNSIGNED_BYTE, 1);
		
		public final String name;
		public final boolean normalized;
		public final int dataType;
		public final int size;
		
		private AttributeType(String name, boolean normalized, int type, int size) {
			this.name = name;
			this.normalized = normalized;
			this.dataType = type;
			this.size = size;
		}
		
		private void parse(ByteBuffer b, String[] data) {
			switch(dataType) {
				case GL_FLOAT:
					for(String s : data)
						b.putFloat(Float.parseFloat(s));
					break;
				case GL_INT:
				case GL_UNSIGNED_INT:
					for(String s : data)
						b.putInt((int)Long.parseLong(s));
					break;
				case GL_SHORT:
				case GL_UNSIGNED_SHORT:
					for(String s : data)
						b.putShort((short)Long.parseLong(s));
					break;
				case GL_BYTE:
				case GL_UNSIGNED_BYTE:
					for(String s : data)
						b.put((byte)Long.parseLong(s));
					break;
			}
		}
		
		public static AttributeType getAttributeType(String name) {
			name = name.toLowerCase();
			
			for(AttributeType at : values())
				if(at.name.equals(name))
					return at;
			
			throw new IllegalArgumentException("Unsupported attribute type!");
		}
		
		public static AttributeType getAttributeType(int dataType, boolean normalized) {
			for(AttributeType at : values())
				if(at.dataType == dataType && at.normalized == normalized)
					return at;
			
			throw new IllegalArgumentException("Unsupported attribute type!");
		}
	}
}
