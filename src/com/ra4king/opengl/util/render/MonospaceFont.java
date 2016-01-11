package com.ra4king.opengl.util.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import com.ra4king.opengl.util.ShaderProgram;
import com.ra4king.opengl.util.Utils;
import com.ra4king.opengl.util.math.Matrix4;
import com.ra4king.opengl.util.math.Vector4;

/**
 * @author Roi Atalla
 */
public class MonospaceFont {
	private static HashMap<String, MonospaceFont> fonts = new HashMap<>();
	
	private String name;
	private int fontTex;
	private ShaderProgram fontProgram;
	private int projectionMatrixUniform, colorUniform;
	private int fontVAO, fontVBO;
	
	private String characters;
	private int charWidth, charHeight;
	private float texCharWidth;
	
	private MonospaceFont() {
	}
	
	/**
	 * All the characters must of the same width and on the same row.
	 *
	 * @param name        The name of this font
	 * @param charWidth   The width of each character
	 * @param imageWidth  The width of the image
	 * @param imageHeight The height of the image (and consequently of each chararacter)
	 * @param imageData   Pixel data in RGBA format, 32bpp as floats.
	 * @return An instance of this MonospaceFont representing this font.
	 */
	public static MonospaceFont init(String name, int charWidth, int imageWidth, int imageHeight, ByteBuffer imageData, String characters) {
		if(characters.length() * charWidth != imageWidth) {
			throw new IllegalArgumentException("characters.length * texCharWidth does not equal imageWidth");
		}
		
		MonospaceFont font = new MonospaceFont();
		font.name = name;
		
		font.characters = characters;
		font.charWidth = charWidth;
		font.charHeight = imageHeight;
		font.texCharWidth = 1f / characters.length();
		
		font.fontTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, font.fontTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imageWidth, imageHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		font.fontProgram = new ShaderProgram(Utils.readFully(PerformanceGraph.class.getResourceAsStream(RenderUtils.SHADERS_PATH + "mono_font.vert")),
		                                      Utils.readFully(PerformanceGraph.class.getResourceAsStream(RenderUtils.SHADERS_PATH + "mono_font.frag")));
		font.projectionMatrixUniform = font.fontProgram.getUniformLocation("projectionMatrix");
		font.colorUniform = font.fontProgram.getUniformLocation("color");
		
		font.fontProgram.begin();
		glUniform1i(font.fontProgram.getUniformLocation("fontTex"), 0);
		//glUniform1f(font.fontProgram.getUniformLocation("texCharWidth"), (float)charWidth / characters.length());
		font.fontProgram.end();
		
		font.fontVAO = glGenVertexArrays();
		glBindVertexArray(font.fontVAO);
		
		font.fontVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, font.fontVBO);
		
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		return font;
	}
	
	private static FloatBuffer buffer = BufferUtils.createFloatBuffer(100 * 4 * 6);
	
	public void render(String str, float x, float y, float height, Vector4 color) {
		glDisable(GL_DEPTH_TEST);
		
		fontProgram.begin();
		
		glUniformMatrix4(projectionMatrixUniform, false, new Matrix4().clearToOrtho(0, RenderUtils.getWidth(), 0, RenderUtils.getHeight(), 0, 1).toBuffer());
		
		glUniform4(colorUniform, color.toBuffer());
		
		buffer.clear();
		
		float charWidth = this.charWidth * (height / this.charHeight);
		
		int charsDrawn = 0;
		float lastLeft = x;
		for(char c : str.toLowerCase().toCharArray()) {
			if(c == ' ') {
				lastLeft += charWidth;
				continue;
			}
			
			int index;
			if((index = characters.indexOf(c)) == -1) {
				lastLeft += charWidth;
				continue;
			}
			
			if(buffer.remaining() < 24) {
				FloatBuffer temp = BufferUtils.createFloatBuffer(buffer.capacity() * 2);
				buffer.flip();
				temp.put(buffer);
				buffer = temp;
			}
			
			float texPos = texCharWidth * index;
			
			buffer.put(lastLeft).put(y + height);
			buffer.put(texPos).put(0f);
			
			buffer.put(lastLeft + charWidth).put(y);
			buffer.put(texPos + texCharWidth).put(1f);
			
			buffer.put(lastLeft).put(y);
			buffer.put(texPos).put(1f);
			
			buffer.put(lastLeft + charWidth).put(y);
			buffer.put(texPos + texCharWidth).put(1f);
			
			buffer.put(lastLeft).put(y + height);
			buffer.put(texPos).put(0f);
			
			buffer.put(lastLeft + charWidth).put(y + height);
			buffer.put(texPos + texCharWidth).put(0f);
			
			charsDrawn++;
			lastLeft += charWidth;
		}
		
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, fontVBO);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STREAM_DRAW);
		
		RenderUtils.glBindVertexArray(fontVAO);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, fontTex);
		
		glDrawArrays(GL_TRIANGLES, 0, charsDrawn * 6);
		
		glEnable(GL_DEPTH_TEST);
	}
}
