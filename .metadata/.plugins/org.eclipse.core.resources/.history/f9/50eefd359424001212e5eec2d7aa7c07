package com.valleskeyp.data;

import java.util.HashMap;

public enum enumData {
	TIMES1(1),
	TIMES2(2),
	TIMES4(4);
	
	private final int multiplier;
	
	enumData(int multiplier) {
		this.multiplier = multiplier;
	}
	
	public static HashMap<enumData, Integer> getModifier(int amount) {
		HashMap<enumData, Integer> total = new HashMap<enumData, Integer>();
		int randomNum = 3 + (int)(Math.random() * ((3 - 1) + 1));
		enumData[] times = {TIMES1,TIMES2,TIMES4};
		
		enumData num1 = times[randomNum];
		//total = (int) amount * num1.multiplier;
		return total;
	}
}
