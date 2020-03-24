package com.ks.jdfen.zha;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cards {
   	private ArrayList<Card> cards;
   	private int index;

   	/**
   	 * 是否全牌
   	 */
   	public Cards(boolean isCardsAll) {
   		this.cards = InitCards(isCardsAll);
   	}

   	public ArrayList<Card> getCards() {
   		return cards;
   	}

   	@Override
   	public String toString() {
   		return cards.toString();
   	}

   	//初始化扑克牌
   	private ArrayList<Card> InitCards(boolean isCardsAll) {
   		ArrayList<Card> list = new ArrayList<>();
   		int id = 0;

   		// 两层循环将花色和牌值搭配
   		for (int i = 1; i < 5; i++) {
   			for (int j = 2; j < 15; j++) {
   				// 创建一张扑克牌对象
   				Card card = new Card();
   				// 设置扑克牌的花色
   				card.setColor(i);
   				// 设置扑克牌的数值
   				card.setNumber(j);

   				id = i + 1;
   				card.setId(id);
   				// 将扑克牌添加
   				list.add(card);
   			}
   		}

   		if (isCardsAll) {
   			Card king1 = new Card();
   			king1.setColor(5);
   			king1.setId(53);
   			king1.setNumber(15);
   			list.add(king1);
   			Card king2 = new Card();
   			king2.setColor(6);
   			king2.setId(54);
   			king2.setNumber(16);
   			list.add(king2);
   		}

   		return list;
   	}
   	//洗牌
   	public void shuffle() {
   		List<Card> list = new ArrayList<Card>();
   		this.index = 0;
   		Random rd = new Random();
   		int id;
   		int count = 0;

   		while (count < cards.size()) {
   			id = rd.nextInt(cards.size());
   			if (!cards.get(id).isFlag()) {
   				count++;
   				list.add(cards.get(id));
   				cards.get(id).setFlag(true);
   			}
   		}
   		for (Card card : cards) {
   			card.setFlag(false);
   		}
   		cards = (ArrayList<Card>) list;
   	}

       //发牌
   	public Card dealCard() {
   		if (index > cards.size()) {
   			throw new IndexOutOfBoundsException();
   		}
   		Card card = cards.get(index++);
   		card.setFlag(false);
   		return card;
   	}

   }
