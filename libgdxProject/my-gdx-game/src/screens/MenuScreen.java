package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.valleskeyp.mygdxgame.MyInputProcessor;

public class MenuScreen implements Screen {
	private Sprite backGround;
	private SpriteBatch batch;
	private Sprite sprite1;
	private Sprite sprite2;
	private Sprite sprite3;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		backGround.draw(batch);
		sprite1.draw(batch);
		sprite2.draw(batch);
		sprite3.draw(batch);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		
		MyInputProcessor inputProcessor = new MyInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		Texture backGroundTexture = new Texture("data/bgGDX.png");
		backGround = new Sprite(backGroundTexture);
		backGround.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		Texture sprite1Texture = new Texture("data/sprite1.png");
		sprite1 = new Sprite(sprite1Texture);
		sprite1.setSize(120, 120);
		sprite1.setPosition(100, 100);
		
		Texture sprite2Texture = new Texture("data/sprite2.png");
		sprite2 = new Sprite(sprite2Texture);
		sprite2.setSize(120, 120);
		sprite2.setPosition(300, 100);
		
		Texture sprite3Texture = new Texture("data/card.png");
		sprite3 = new Sprite(sprite3Texture);
		sprite3.setSize(180, 180);
		sprite3.setPosition(500, 100);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
