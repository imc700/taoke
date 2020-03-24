package com.ks.jdfen.zha;

import java.util.ArrayList;

public class Player {
   	private String name;
       //玩家手牌
   	private ArrayList<Card> handCards;
       //玩家手牌等级
   	private int grade;

   	public Player(String name ) {
   		this.name = name;
   		this.handCards = new ArrayList<Card>();
   	}
   	public String getName() {
   		return name;
   	}
   	public void setName(String name) {
   		this.name = name;
   	}
   	public ArrayList<Card> getPlayerCards() {
   		return handCards;
   	}
   	public void setPlayerCards(ArrayList<Card> playerCards) {
   		this.handCards = playerCards;
   	}
   	public int getGrade() {
   		return grade;
   	}
   	public void setGrade(int grade) {
   		this.grade = grade;
   	}
   	@Override
   	public String toString() {
   		return "玩家：" + name + " " + handCards;
   	}
   }
