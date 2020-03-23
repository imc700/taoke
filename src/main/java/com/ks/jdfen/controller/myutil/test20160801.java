package com.ks.jdfen.controller.myutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class test20160801 {
    public static void main(String[] args) {
        // TODO 自动生成的方法存根    
        List<Person> people = new ArrayList<>();
        Person p1 = new Person("老沈");
        Person p2 = new Person("老黄");
        Person p3 = new Person("老韩");
        people.add(p1);
        people.add(p2);
        people.add(p3);
        for (Person p : people) {
            p.card.random(p);
            CardSet.rank(p);
            CardSet.judge(p);
        }
        bubbleSort(people);
        for (Person person : people) {
            System.out.println(person);
        }
//        Success.success(p1, p2, p3);
    }

    public static void bubbleSort(List<Person> data) {

        int j, k;

        int flag = data.size();//flag来记录最后交换的位置，也就是排序的尾边界

        while (flag > 0) {//排序未结束标志

            k = flag; //k 来记录遍历的尾边界

            flag = 0;

            for (j = 1; j < k; j++) {

//                if (data[j - 1] > data[j]) {//前面的数字大于后面的数字就交换
                if (Success.success(data.get(j - 1), data.get(j))) {//前面的数字大于后面的数字就交换

                    //交换a[j-1]和a[j]
                    Collections.swap(data, j - 1, j);
                    //表示交换过数据
                    flag = j;//记录最新的尾边界

                }

            }

        }

    }

}

class Person {//玩家
    Card card = new Card();//人有牌
    int win = 0;//赢的人win为1
    String name;//名字

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "card=" + card +
                ", win=" + win +
                ", name='" + name + '\'' +
                '}';
    }
}

class Card {//牌类
    int[] card3 = new int[3];//用于存储玩家的3张牌
    String[] card_3 = new String[3];
    int flag = 0;//用于存储不同类型牌的等级，用于判断

    @Override
    public String toString() {
        return "Card{" +
                "card3=" + Arrays.toString(card3) +
                ", card_3=" + Arrays.toString(card_3) +
                ", flag=" + flag +
                '}';
    }

    void random(Person p) {//随机获得三张牌
        for (int i = 0; i < card3.length; i++) {
            card3[i] = (int) (Math.random() * 13 + 2);
        }
        for (int i = 0; i < card3.length; i++) {
            switch (card3[i]) {
                case 2:
                    card_3[i] = "2";
                    break;
                case 3:
                    card_3[i] = "3";
                    break;
                case 4:
                    card_3[i] = "4";
                    break;
                case 5:
                    card_3[i] = "5";
                    break;
                case 6:
                    card_3[i] = "6";
                    break;
                case 7:
                    card_3[i] = "7";
                    break;
                case 8:
                    card_3[i] = "8";
                    break;
                case 9:
                    card_3[i] = "9";
                    break;
                case 10:
                    card_3[i] = "10";
                    break;
                case 11:
                    card_3[i] = "J";
                    break;
                case 12:
                    card_3[i] = "Q";
                    break;
                case 13:
                    card_3[i] = "K";
                    break;
                case 14:
                    card_3[i] = "A";
                    break;
            }
        }
        System.out.print(p.name + "的牌为:");
        for (int k = 0; k < card3.length; k++) {
            System.out.print("   " + card_3[k]);
        }
        System.out.println("");
    }
}

class CardSet {//用于比较前的牌的排序和属性设置

    static void rank(Person p) {//对3张牌进行大小的排序
        for (int i = 0; i < p.card.card3.length - 1; i++) {
            for (int j = 0; j < p.card.card3.length - 1 - i; j++) {
                if (p.card.card3[j] < p.card.card3[j + 1]) {
                    int temp = p.card.card3[j];
                    p.card.card3[j] = p.card.card3[j + 1];
                    p.card.card3[j + 1] = temp;
                }
            }
        }
    }

    static void judge(Person p) {//设置牌的属性
        if (p.card.card3[0] == p.card.card3[1] && p.card.card3[1] == p.card.card3[2]
                && p.card.card3[0] == p.card.card3[2]) {
            p.card.flag = 4;
        } else if (p.card.card3[0] == (p.card.card3[1] + 1)
                && p.card.card3[0] == (p.card.card3[2] + 2)) {
            p.card.flag = 3;
        } else if (p.card.card3[0] == p.card.card3[1] || p.card.card3[0] == p.card.card3[2]
                || p.card.card3[1] == p.card.card3[2]) {
            p.card.flag = 2;
            if (p.card.card3[0] == p.card.card3[2]) {
                int temp = p.card.card3[1];
                p.card.card3[1] = p.card.card3[2];
                p.card.card3[2] = temp;
            } else if (p.card.card3[1] == p.card.card3[2]) {
                int temp = p.card.card3[0];
                p.card.card3[0] = p.card.card3[2];
                p.card.card3[2] = temp;
            }
        } else {
            p.card.flag = 1;
        }
    }
}

class Success {//决定最终胜者

    static boolean success(Person p1, Person p2) {
        if (p1.card.flag > p2.card.flag) {
            p1.win = 1;
        } else if (p1.card.flag == p2.card.flag) {
            if (p1.card.card3[0] > p2.card.card3[0]) {
                p1.win = 1;
            } else if (p1.card.card3[0] == p2.card.card3[0]) {
                if (p1.card.card3[2] > p2.card.card3[2]) {
                    p1.win = 1;
                } else {
                    p2.win = 1;
                }
            } else {
                p2.win = 1;
            }
        } else {
            p2.win = 1;
        }
        if (p1.win == 1) {
            return true;
//            System.out.println(p1.name + "赢");
        } else {
            return false;
//            System.out.println(p2.name + "赢");
        }
    }
}