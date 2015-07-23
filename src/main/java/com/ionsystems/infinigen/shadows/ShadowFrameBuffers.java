package main.java.com.ionsystems.infinigen.shadows;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.ByteBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShadowFrameBuffers {

	private static final int DEPTH_WIDTH = 1024;
	private static final int DEPTH_HEIGHT = 1024;
	private int shadowFrameBuffer;
	private int shadowDepthTexture;

	public ShadowFrameBuffers() {
		initialiseShadowTexture();
	}

	private void initialiseShadowTexture() {
		shadowFrameBuffer = createFrameBuffer();
		shadowDepthTexture = createDepthTextureAttachment(DEPTH_WIDTH, DEPTH_HEIGHT);
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Error setting up shadowbuffer");
		}
	}

	private int createFrameBuffer() {
		int frameBuffer = glGenFramebuffers();
		// generate name for frame buffer
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		// create the framebuffer
		glDrawBuffer(GL_NONE);
		// indicate that we will always render to color attachment 0
		return frameBuffer;
	}

	private int createDepthTextureAttachment(int width, int height) {
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, texture, 0);
		return texture;
	}

	public void bindShadowFrameBuffer() {
		bindFrameBuffer(shadowFrameBuffer, DEPTH_WIDTH, DEPTH_HEIGHT);

	}

	private void bindFrameBuffer(int frameBuffer, int width, int height) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);// To make sure the texture
													// isn't bound
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public Vector3f getDepthTexture() {
		return new Vector3f(DEPTH_WIDTH, DEPTH_HEIGHT, shadowDepthTexture);
	}

	public void unbindCurrentFrameBuffer() {//call to switch to default frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
    }
}
