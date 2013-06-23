package com.studio332.blastwords;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "blastwords";
		cfg.useGL20 = true;
		cfg.width = 480;
		cfg.height = 800;
		
		new LwjglApplication(new BlastWords(), cfg);
	}
}
