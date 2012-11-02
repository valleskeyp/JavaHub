package com.valleskeyp.data;

import java.util.HashMap;

public enum enumData {
	TIMES1(1),
	TIMES2(2),
	TIMES4(4);
// not sure what to do with the enum data...
// this was an attempt at returning a number that gets multiplied by the TIMES enumData
// in the end i was not able to return the data properly, the return is for a HashMap but my
// result was an integer.  perhaps as the app progresses i can properly use this feature.
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
