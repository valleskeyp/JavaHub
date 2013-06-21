package com.valleskeyp.mgdgame;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class GameClass implements ApplicationListener, InputProcessor {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture, aCard, bCard, cCard, dCard, eCard, fCard, strikeCard;
	private Sprite sprite, shadow, pause, play, gameOverText, gameWinText, strike1, strike2;
	private Sound correct, incorrect;
	private Music bgMusic;
	private String answer = "";
	private Card card1;
	private Card card2;
	private Boolean isPlaying = true;
	private int wrongGuesses = 3;
	private Boolean gameOver = false;
	private float timer = 0;
	
	public Array<HashMap<String, Float>> coord = new Array<HashMap<String, Float>>();
	public Array<Card> cards;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		Gdx.input.setInputProcessor(this);
		
		setCoordinates();
		
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("data/BGMusic.mp3"));
		bgMusic.setVolume(0.15f);
		bgMusic.play();
		bgMusic.setLooping(true);
		
		correct = Gdx.audio.newSound(Gdx.files.internal("data/correct.mp3"));
		incorrect = Gdx.audio.newSound(Gdx.files.internal("data/incorrect.wav"));
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		
		// background    ----  Don't forget to add sprite to draw batch when done
		texture = new Texture(Gdx.files.internal("data/bgGDX.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, 800, 480);
		
		sprite = new Sprite(region);
		sprite.setSize(1f, 1f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		
		// bg shadow
		texture = new Texture(Gdx.files.internal("data/shadow.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		region = new TextureRegion(texture, 0, 0, 800, 480);
		
		shadow = new Sprite(region);
			shadow.setSize(1f, 1f * shadow.getHeight() / shadow.getWidth());
		shadow.setOrigin(shadow.getWidth()/2, shadow.getHeight()/2);
		shadow.setPosition(-shadow.getWidth()/2, -shadow.getHeight()/2);
		
		// pause button
		texture = new Texture(Gdx.files.internal("data/pause.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		region = new TextureRegion(texture, 0, 0, 32, 32);
		
		pause = new Sprite(region);
		pause.setSize(.064f, .064f);
		pause.setOrigin(pause.getWidth()/2, pause.getHeight()/2);
		pause.setPosition(0.424f, 0.22f);
		
		// play button
		texture = new Texture(Gdx.files.internal("data/play.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		region = new TextureRegion(texture, 0, 0, 32, 32);

		play = new Sprite(region);
		play.setSize(.064f, .064f);
		play.setOrigin(play.getWidth()/2, play.getHeight()/2);
		play.setPosition(0.424f, 0.22f);
		
		// game over text
		texture = new Texture(Gdx.files.internal("data/gameOver.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		region = new TextureRegion(texture, 0, 0, 256, 128);

		gameOverText = new Sprite(region);
		gameOverText.setSize(.256f, .128f);
		gameOverText.setOrigin(gameOverText.getWidth()/2, gameOverText.getHeight()/2);
		gameOverText.setPosition(-.128f, -.064f);
		
		// game over text
		texture = new Texture(Gdx.files.internal("data/gameWin.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		region = new TextureRegion(texture, 0, 0, 256, 128);

		gameWinText = new Sprite(region);
		gameWinText.setSize(.256f, .128f);
		gameWinText.setOrigin(gameWinText.getWidth()/2, gameWinText.getHeight()/2);
		gameWinText.setPosition(-.128f, -.064f);

		// wrong card
		strikeCard = new Texture(Gdx.files.internal("data/wrong.png"));
		strikeCard.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		region = new TextureRegion(strikeCard, 0, 0, 80, 115);

		strike1 = new Sprite(region);
		strike1.setSize(.08f, .115f);
		strike1.setOrigin(strike1.getWidth()/2, strike1.getHeight()/2);
		strike1.setPosition(-.4f, .1f);
		
		region = new TextureRegion(strikeCard, 0, 0, 80, 115);

		strike2 = new Sprite(region);
		strike2.setSize(.08f, .115f);
		strike2.setOrigin(strike2.getWidth()/2, strike2.getHeight()/2);
		strike2.setPosition(-.4f, -.05f);

		
//// CARD CREATION /////////////////////
		
		texture = new Texture(Gdx.files.internal("data/card.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		restart(); // uses array of coordinates to make an array of Card objects -- fresh start
			
		// A card
		aCard = new Texture(Gdx.files.internal("data/aCard.png"));
		aCard.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		// B card
		bCard = new Texture(Gdx.files.internal("data/bCard.png"));
		bCard.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// C card
		cCard = new Texture(Gdx.files.internal("data/cCard.png"));
		cCard.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// D card
		dCard = new Texture(Gdx.files.internal("data/dCard.png"));
		dCard.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// E card
		eCard = new Texture(Gdx.files.internal("data/eCard.png"));
		eCard.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// F card
		fCard = new Texture(Gdx.files.internal("data/fCard.png"));
		fCard.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		
		
		
	}

	private void restart() {
		int i = 0;
		cards = new Array<Card>();
		for (HashMap<String, Float> hm : coord) {
			String str = "";
			if (i < 2) {
				str = "A";
			} else if (i < 4 && i >= 2) {
				str = "B";
			} else if (i < 6 && i >= 4) {
				str = "C";
			} else if (i < 8 && i >= 6) {
				str = "D";
			} else if (i < 10 && i >= 8) {
				str = "E";
			} else if (i < 12 && i >= 10) {
				str = "F";
			}
			Card card = new Card(texture, hm.get("x"), hm.get("y"), str);
			cards.add(card);
			i++;
		}
	}

	@Override
	public void dispose() {  //     DON'T FORGET TO DISPOSE OF EVERYTHING POSSIBLE!
		batch.dispose();
		texture.dispose();
		aCard.dispose();
		bCard.dispose();
		cCard.dispose();
		dCard.dispose();
		eCard.dispose();
		fCard.dispose();
		strikeCard.dispose();
		correct.dispose();
		incorrect.dispose();
		bgMusic.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		float dt = Gdx.graphics.getDeltaTime();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		
		if (cards.size > 0) {
			if (!gameOver) { //																		 GAME PLAY SCREEN
				if (isPlaying) {//										WHILE GAME PLAYING
					pause.draw(batch);

					if(answer.equals("correct")) { //         			   --ON RIGHT ANSWER
						cardMove(dt);
						if (Intersector.overlaps(card1.card.getBoundingRectangle(), card2.card.getBoundingRectangle())) {
							answer = "";
							for (Card card : cards) {
								if (card.letter.equals(card1.letter)) {
									cards.removeValue(card, true);
								}
							}
							for (Card card : cards) {
								if (card.letter.equals(card1.letter)) {
									cards.removeValue(card, true);
								}
							}
							card1 = null;
							card2 = null;
						}
					}

					switch (wrongGuesses) { // 						      	 ----DISPLAY WRONG GUESS ALLOWANCE
					case 2:
						//draw 1 wrong card
						strike1.draw(batch);
						break;
					case 3:
						//draw 2 wrong cards
						strike1.draw(batch);
						strike2.draw(batch);
						break;
					default:
						break;
					}

					for (Card card : cards) {
						card.draw(batch);
					}
					batch.end();
				} else {  //										  WHILE GAME PAUSED
					switch (wrongGuesses) { // 							----DISPLAY WRONG GUESS ALLOWANCE
					case 2:
						//draw 1 wrong card
						strike1.draw(batch);
						break;
					case 3:
						//draw 2 wrong cards
						strike1.draw(batch);
						strike2.draw(batch);
						break;
					default:
						break;
					}

					for (Card card : cards) {
						card.draw(batch);
					}
					shadow.draw(batch);
					play.draw(batch);
					batch.end();
				}
			} else {  //                   							  WHILE GAME OVER [LOSS]
				shadow.draw(batch);
				gameOverText.draw(batch);
				batch.end();
			}
		} else {  //												WHILE GAME OVER [WIN]
			timer += dt;
			
			shadow.draw(batch);
			gameWinText.draw(batch);
			batch.end();
			if (timer >= 3) {
				restart();
				timer = 0;
			} 
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
		if( isPlaying )
        {
            isPlaying = false;
            bgMusic.pause();
        }
        else
        {
            isPlaying = true;
            bgMusic.play();
        }
	}

	@Override
	public void resume() {
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector2 touchPos = new Vector2();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY());

        Ray cameraRay = camera.getPickRay(touchPos.x, touchPos.y);
        if (isPlaying && !gameOver) {  // only act when game is playing
        	for (Card card : cards) {
				Sprite sp = card.spriteReturn();
				if (sp.getBoundingRectangle().contains(cameraRay.origin.x, cameraRay.origin.y)) {
					if (answer.equals("")) {
						String str = card.letter;
						if (str.equals("A")) {
							card.flipCard(aCard);
							if (card1 == null) {
								card1 = card;
							} else {
								card2 = card;
							}
						} else if (str.equals("B")) {
							card.flipCard(bCard);
							if (card1 == null) {
								card1 = card;
							} else {
								card2 = card;
							}
						} else if (str.equals("C")) {
							card.flipCard(cCard);
							if (card1 == null) {
								card1 = card;
							} else {
								card2 = card;
							}
						} else if (str.equals("D")) {
							card.flipCard(dCard);
							if (card1 == null) {
								card1 = card;
							} else {
								card2 = card;
							}
						} else if (str.equals("E")) {
							card.flipCard(eCard);
							if (card1 == null) {
								card1 = card;
							} else {
								card2 = card;
							}
						} else if (str.equals("F")) {
							card.flipCard(fCard);
							if (card1 == null) {
								card1 = card;
							} else {
								card2 = card;
							}
						}
						
						if (card2 != null) { //   		TWO CARDS PICKED
							if (card1.letter.equals(card2.letter)) { // 		-- RIGHT ANSWER
								correct.play(1.0f);
								answer = "correct";
								// now interpolate card movement in batch and reset after
							} else { //							-- WRONG ANSWER
								incorrect.play(1.0f);
								answer = "wrong";
								wrongGuesses -= 1;
								if (wrongGuesses == 0) {
									answer = "";
									gameOver = true;
									restart();
									card1 = null;
									card2 = null;
									Timer timer = new Timer();
			            			timer.schedule(new TimerTask() { 
			            				// 								GAME OVER screen
			            				public void run() {
			            					gameOver = false;
			            					wrongGuesses = 3;
			            				} 
			            			}, 3000);
								} else {
									Timer timer = new Timer();
									timer.schedule(new TimerTask() { 
										// let the incorrect card display for 2 seconds, then hide and return to neutral state
										public void run() {
											answer = "";
											card1.flipCard(texture);
											card2.flipCard(texture);
											card1 = null;
											card2 = null;
										} 
									}, 2000);
								}
							}
						}
						
					}
				}
			}
        }
        
        if (pause.getBoundingRectangle().contains(cameraRay.origin.x, cameraRay.origin.y) || play.getBoundingRectangle().contains(cameraRay.origin.x, cameraRay.origin.y)) {
			pause();  // pause/resume game
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	public void cardMove(float _dt) {
		// change this so it sends the Card objects toward each other
		if (card1.card.getX() <= 0 && card1.card.getY() <= 0) {
			card1.card.setPosition(card1.card.getX() + (.3f * _dt), card1.card.getY() + (.18f * _dt));
		} else if (card1.card.getX() >= 0 && card1.card.getY() <= 0) {
			card1.card.setPosition(card1.card.getX() - (.3f * _dt), card1.card.getY() + (.18f * _dt));
		} else if (card1.card.getX() >= 0 && card1.card.getY() >= 0){
			card1.card.setPosition(card1.card.getX() - (.3f * _dt), card1.card.getY() - (.18f * _dt));
		} else if (card1.card.getX() <= 0 && card1.card.getY() >= 0){
			card1.card.setPosition(card1.card.getX() + (.3f * _dt), card1.card.getY() - (.18f * _dt));
		}
		if (card2.card.getX() <= 0 && card2.card.getY() <= 0) {
			card2.card.setPosition(card2.card.getX() + (.3f * _dt), card2.card.getY() + (.18f * _dt));
		} else if (card2.card.getX() >= 0 && card2.card.getY() <= 0) {
			card2.card.setPosition(card2.card.getX() - (.3f * _dt), card2.card.getY() + (.18f * _dt));
		} else if (card2.card.getX() >= 0 && card2.card.getY() >= 0){
			card2.card.setPosition(card2.card.getX() - (.3f * _dt), card2.card.getY() - (.18f * _dt));
		} else if (card2.card.getX() <= 0 && card2.card.getY() >= 0){
			card2.card.setPosition(card2.card.getX() + (.3f * _dt), card2.card.getY() - (.18f * _dt));
		}
	}
	
	private void setCoordinates() {
		HashMap<String, Float> hm = new HashMap<String, Float>();
		hm.put("x", -.15f);
		hm.put("y", .15f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", -.05f);
		hm.put("y", .15f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", .05f);
		hm.put("y", .15f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", .15f);
		hm.put("y", .15f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", -.15f);
		hm.put("y", .0f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", -.05f);
		hm.put("y", .0f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", .05f);
		hm.put("y", .0f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", .15f);
		hm.put("y", .0f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", -.15f);
		hm.put("y", -.15f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", -.05f);
		hm.put("y", -.15f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", .05f);
		hm.put("y", -.15f);
		coord.add(hm);

		hm = new HashMap<String, Float>();
		hm.put("x", .15f);
		hm.put("y", -.15f);
		coord.add(hm);
	}
}
