package com.ks.jdfen.zha;

import java.util.Arrays;
import java.util.List;

/**
 * @author ：imc
 * @date ：Created in 2020/3/24 11:15 上午
 * @description：
 */
public class testZha {
    public static void main(String[] args) throws Exception {
        List<String> seatNameList = Arrays.asList("1","","","","","","","");
        System.out.println(seatNameList.indexOf(""));
//        WinThreePoker poker = new WinThreePoker(new Player("1号玩家"),new Player("2号玩家"),new Player("3号玩家"));
//        poker.startPlayingCards();


        //陈骁让处理他想要的字典
        /*WinThreePoker zha = new WinThreePoker();
        File file = ResourceUtils.getFile("classpath:chenxiaozidian.txt");
        String content = FileUtil.readAsString(file);
        for (String pai : content.split("\n")) {
            String[] three = pai.split("\t");
            ArrayList<Card> cards = new ArrayList<>();
            for (String p : three) {
                p = p.trim();
                Card card = new Card();
                card.setColor(switchStringToInt(p.substring(2, 4)));
                card.setNumber(switchStringToInt(p.substring(4)));
                cards.add(card);
            }
            String nun="";
            int grade = zha.getGrade(cards);
            switch (grade) {
                case 1:
                    nun = "单张";
                    break;
                case 2:
                    nun = "对子";
                    break;
                case 3:
                    nun = "顺子";
                    break;
                case 4:
                    nun = "同花";
                    break;
                case 5:
                    nun = "同花顺";
                    break;
                case 6:
                    nun = "豹子";
                    break;
            }
            System.out.println(nun);
        }*/
    }

    public static int switchStringToInt(String s){
        int flower = 0;
        switch (s) {
            case "方块":
                flower = 1;
                break;
            case "梅花":
                flower = 2;
                break;
            case "红桃":
                flower = 3;
                break;
            case "黑桃":
                flower = 4;
                break;
            case 2+"":
                flower = 2;
                break;
            case 3+"":
                flower = 3;
                break;
            case 4+"":
                flower = 4;
                break;
            case 5+"":
                flower = 5;
                break;
            case 6+"":
                flower = 6;
                break;
            case 7+"":
                flower = 7;
                break;
            case 8+"":
                flower = 8;
                break;
            case 9+"":
                flower = 9;
                break;
            case 10+"":
                flower = 10;
                break;
            case "J":
                flower = 11;
                break;
            case "Q":
                flower = 12;
                break;
            case "K":
                flower = 13;
                break;
            case "A":
                flower = 14;
                break;
        }
        return flower;
    }
}
