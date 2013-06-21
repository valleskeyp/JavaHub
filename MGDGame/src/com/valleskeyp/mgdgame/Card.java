package com.valleskeyp.mgdgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Card {
	Sprite card;
	String letter;
	
	public Card(Texture t, float x, float y, String l) {
		TextureRegion region = new TextureRegion(t, 0, 0, 80, 115);
		letter = l;
		
		card = new Sprite(region);
		card.setSize(.08f, .115f);
		card.setOrigin(card.getWidth()/2, card.getHeight()/2);
		card.setPosition(x, y);
	}
	
	public void update() {
		
	}
	
	public void draw(SpriteBatch batch) {
		card.draw(batch);
	}
	
	public Sprite spriteReturn() {
		return card;
	}
	
	public void flipCard(Texture t) {
		TextureRegion region = new TextureRegion(t, 0, 0, 80, 115);
		
		card.setRegion(region);
	}
	
}
