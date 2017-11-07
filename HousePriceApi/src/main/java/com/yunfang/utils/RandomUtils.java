package com.yunfang.utils;

import java.util.Random;

public class RandomUtils {
	
	public static double maxRise(){
		Random random = new Random();
		int value = random.nextInt(100) + 1;
		int r = 0;
		if( value<=5 ){
			r = random.nextInt(750)%151 + 600;
		}else if(value >= 6 && value<=20){
			r = random.nextInt(600)%101 + 500;
		}else if(value >= 21 && value<=40){
			r = random.nextInt(500)%101 + 400;
		}else if(value >= 41 && value<=75){
			r = random.nextInt(400)%101 + 330;
		}else{
			r = random.nextInt(330)%101 + 250;
		}
		return r/1000d;
	}
	
	public static double minRise(){
		Random random = new Random();
		int value = random.nextInt(100) + 1;
		int r = 0;
		if( value<=20){
			r = random.nextInt(150)%51 + 100;
		}else if(value >= 21 && value<=45){
			r = random.nextInt(200)%51 + 150;//150-200
		}else if(value >= 46 && value<=95){
			r = random.nextInt(300)%101 + 200;
		}else{
			r = random.nextInt(350)%101 + 300;
		}
		return r/1000d;
	}
	
}
