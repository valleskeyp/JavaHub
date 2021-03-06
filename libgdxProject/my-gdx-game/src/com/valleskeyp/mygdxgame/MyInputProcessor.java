package com.valleskeyp.mygdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;

public class MyInputProcessor implements InputProcessor {
	Sound shuffle = Gdx.audio.newSound(Gdx.files.internal("data/shuffle.wav"));
	Sound correct = Gdx.audio.newSound(Gdx.files.internal("data/correct.mp3"));

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (screenX >= 100 && screenX <= 220 && screenY >= Gdx.graphics.getHeight() - 220 && screenY <= Gdx.graphics.getHeight() - 120) {
			shuffle.play(1.0f);
		}
		if (screenX >= 300 && screenX <= 420 && screenY >= Gdx.graphics.getHeight() - 220 && screenY <= Gdx.graphics.getHeight() - 120) {
			correct.play(1.0f);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}


}
