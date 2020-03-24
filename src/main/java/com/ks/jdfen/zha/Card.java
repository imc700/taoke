package com.ks.jdfen.zha;

public class Card {
  	private int id;
  	private int color;
  	private int number;
  	private boolean flag;

  	public Card() {
  		this.flag = false;
  	}
  	public int getId() {
  		return id;
  	}
  	public void setId(int id) {
  		this.id = id;
  	}
  	public int getNumber() {
  		return number;
  	}
  	public void setNumber(int number) {
  		this.number = number;
  	}
  	public int getColor() {
  		return color;
  	}
  	public void setColor(int color) {
  		this.color = color;
  	}
  	public boolean isFlag() {
  		return flag;
  	}
  	public void setFlag(boolean flag) {
  		this.flag = flag;
  	}
  	@Override
  	public String toString() {
  		return colorToString(color) + numberToString(number);
  	}
      //将花色与数值大小进行字符化
  	private String colorToString(int i){
  		String flower = null;
  		switch (i) {
  		case 1:
  			flower = "♦";
  			break;
  		case 2:
  			flower = "♣";
  			break;
  		case 3:
  			flower = "♥";
  			break;
  		case 4:
  			flower = "♠";
  			break;
  		case 5:
  			flower = "j";
  			break;
  		case 6:
  			flower = "J";
  			break;
  		}
  		return flower;
  	}
  	private String numberToString(int i){
  		String nun = null;
  		switch (i) {
  		case 2:
  			nun = "2";
  			break;
  		case 3:
  			nun = "3";
  			break;
  		case 4:
  			nun = "4";
  			break;
  		case 5:
  			nun = "5";
  			break;
  		case 6:
  			nun = "6";
  			break;
  		case 7:
  			nun = "7";
  			break;
  		case 8:
  			nun = "8";
  			break;
  		case 9:
  			nun = "9";
  			break;
  		case 10:
  			nun = "10";
  			break;
  		case 11:
  			nun = "J";
  			break;
  		case 12:
  			nun = "Q";
  			break;
  		case 13:
  			nun = "K";
  			break;
  		case 14:
  			nun = "A";
  			break;
  		case 15:
  			nun = "oker";
  			break;
  		case 16:
  			nun = "oker";
  			break;
  		}
  		return nun;
  	}
  }
