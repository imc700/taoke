package com.ks.jdfen.zha;

import java.util.*;

public class WinThreePoker {
    private Cards cards;
    private ArrayList<Player> players;

    public WinThreePoker(Player... players) throws Exception {
        cards = new Cards(false);
        if (players.length > 8 || players.length < 2) {
            throw new Exception(String.valueOf("人数异常,当前人数为:"+players.length));
        }
        this.players = new ArrayList<Player>(Arrays.asList(players));
    }

    // 开始游戏到游戏结束
    public void startPlayingCards() {
        if (players.get(0).getPlayerCards() != null) {
            for (Player player : players) {
                player.getPlayerCards().clear();
            }
        }
        // 分配一组牌
        Cards cards = this.cards;
        // 洗牌
        cards.shuffle();
        // 给玩家发牌，轮着发三张
        // 外层循环控制发牌次数
        for (int i = 0; i < 3; i++) {
            // 内层循环控制给哪位玩家发牌
            for (int j = 0; j < players.size(); j++) {
                players.get(j).getPlayerCards().add(cards.dealCard());
            }
        }
        // 用重写sort方法中的比较器cmparator实现给玩家手牌排序
        for (Player player : players) {
            player.getPlayerCards().sort(new Comparator<Card>() {

                @Override
                public int compare(Card c1, Card c2) {
                    return c1.getNumber() - c2.getNumber();
                }
            });

            if (getNumber(player, 0) == 2 && getNumber(player, 2) == 14 && getNumber(player, 1) == 3) {
                Card card1 = player.getPlayerCards().get(0);
                Card card2 = player.getPlayerCards().get(1);
                Card card3 = player.getPlayerCards().get(2);
                player.getPlayerCards().set(0, card3);
                player.getPlayerCards().set(1, card1);
                player.getPlayerCards().set(2, card2);
            }
            // 调用getGrade()方法给玩家手牌定一个级别
            player.setGrade(getGrade(player.getPlayerCards()));
            // 输出玩家情况与手牌级别，测试
            System.out.println(player + "\t手牌级别：" + player.getGrade());
        }
        System.out.println();
        // 判断哪位玩家胜出
        judge(players);
    }

    // 炸金花规则， 并在控制台输出获胜玩家！
    private void judge(ArrayList<Player> players) {
        // 先得到所有玩家中级别最高且相等的玩家们
        ArrayList<Player> list = getPlayersOfMaxGrade(players);
        // 输出级别相等的玩家
//        for (Player player : list) {
//            System.out.println(player);
//        }
        // 如果可能获胜玩家数是一个，直接判定他为赢家，否则继续判定
        if (list.size() == 1) {
            System.out.println("恭喜玩家" + list.get(0).getName() + "获胜！");
        } else {
            // 如果可能获胜的玩家不止一个，得到所有获胜玩家
            list = getWinners(list);
            // 遍历获胜玩家动态数组，并输出
            for (Player player : list) {
                System.out.println("恭喜玩家" + player.getName() + "获胜！");
            }
        }
        System.out.println();
    }

    //得出所有获胜的玩家
    private ArrayList<Player> getWinners(ArrayList<Player> list) {
        ArrayList<Player> winners = new ArrayList<Player>();
        // 获取当前级别
        int grade = list.get(0).getGrade();
        // 用来作比较牌大小的变量
        int cardNumber = 0;
        // 不同的grade会执行对应分支的语句体
        switch (grade) {
            // 豹子手牌
            case 6:
                // 遍历玩家Arraylist比较手牌中的第二张手牌，获取豹子最大玩家
                for (Player player : list) {
                    if (getNumber(player, 1) > cardNumber) {
                        cardNumber = getNumber(player, 1);
                        winners.clear();
                        winners.add(player);
                    }
                }
                break;
            // 同花顺或顺子手牌玩家，三张牌连在一起，所以只有比较一张牌就行
            case 3:
            case 5:
                for (Player player : list) {
                    if (player.getPlayerCards().get(1).getNumber() > cardNumber) {
                        cardNumber = getNumber(player, 1);
                        winners.clear();
                        winners.add(player);
                    } else if (player.getPlayerCards().get(1).getNumber() == cardNumber) {
                        winners.add(player);
                    }
                }
                break;
            // 同花与散牌
            case 1:
            case 4:
                winners = getWinnerForSomeCase(2, 1, list);
                break;
            // 对子
            case 2:
                winners = getWinnerForSomeCase(1, 2, list);
                break;
            default:
                break;
        }
        return winners;
    }

    //获取玩家手牌的大小
    private int getNumber(Player player, int i) {
        return player.getPlayerCards().get(i).getNumber();
    }

    // 同花，对子，散牌不同状况下，获胜玩家的获取
    private ArrayList<Player> getWinnerForSomeCase(int m, int n, ArrayList<Player> list) {
        ArrayList<Player> winners = new ArrayList<Player>();
        int cardNumber = 0;
        for (Player player : list) {
            if (!winners.isEmpty()) {
                cardNumber = getNumber(winners.get(0), m);
            }
            if (getNumber(player, m) > cardNumber) {
                cardNumber = getNumber(player, m);
                winners.clear();
                winners.add(player);
            } else if (getNumber(player, m) == cardNumber) {
                cardNumber = getNumber(winners.get(0), n);
                if (getNumber(player, n) > cardNumber) {
                    cardNumber = getNumber(player, n);
                    winners.clear();
                    winners.add(player);
                } else if (getNumber(player, n) == cardNumber) {
                    cardNumber = getNumber(winners.get(0), 0);
                    if (getNumber(player, 0) > cardNumber) {
                        cardNumber = getNumber(player, 0);
                        winners.clear();
                        winners.add(player);
                    } else if (getNumber(player, 0) == cardNumber) {
                        winners.add(player);
                    }
                }
            }
        }
        return winners;
    }

    //给玩家手牌定义一个级别 豹子(三条)：6;同花顺：5;同花：4;顺子：3;对子：2;普通散牌：1;
    public int getGrade(ArrayList<Card> list) {
        Card c1 = list.get(0);
        Card c2 = list.get(1);
        Card c3 = list.get(2);
        if (isTheSameNumber(c1, c2, c3)) {
            return 6;
        } else if (isFlushColor(c1, c2, c3) && isStraight(c1, c2, c3)) {
            return 5;
        } else if (isFlushColor(c1, c2, c3)) {
            return 4;
        } else if (isStraight(c1, c2, c3)) {
            return 3;
        } else if (isPair(c1, c2, c3)) {
            return 2;
        }
        return 1;
    }

    //判断是否是对子
    private boolean isPair(Card c1, Card c2, Card c3) {
        if (c2.getNumber() == c1.getNumber() || c3.getNumber() == c2.getNumber() || c3.getNumber() == c1.getNumber()) {
            return true;
        }
        return false;
    }

    //判断是否是顺子
    private boolean isStraight(Card c1, Card c2, Card c3) {
//        if ((c1.getNumber() + 1 == c2.getNumber() && c2.getNumber() + 1 == c3.getNumber()) || (c2.getNumber() == 2 && c3.getNumber() == 3 && c1.getNumber() == 14)) return true;


        List<Integer> list = new ArrayList<>();
        list.add(c1.getNumber());
        list.add(c2.getNumber());
        list.add(c3.getNumber());
        Collections.sort(list);
        if ((list.get(1) - list.get(0) == 1 && list.get(2) - list.get(0) == 2) || (list.get(1) - list.get(0) == 1 && list.get(2) - list.get(0) == 12))
            return true;
        return false;
    }

    // 判断是否是豹子
    private boolean isTheSameNumber(Card c1, Card c2, Card c3) {
        if (c1.getNumber() == c2.getNumber() && c2.getNumber() == c3.getNumber()) {
            return true;
        }
        return false;
    }

    // 判断是否是同花
    private boolean isFlushColor(Card c1, Card c2, Card c3) {
        if (c1.getColor() == c2.getColor() && c2.getColor() == c3.getColor()) {
            return true;
        }
        return false;
    }

    // 获取所有玩家手牌中级别最高的玩家们
    private ArrayList<Player> getPlayersOfMaxGrade(ArrayList<Player> players) {
        int maxGrade = 0;
        // 初始化一个动态数组保存级别最大的玩家
        ArrayList<Player> maxPlayers = new ArrayList<>();
        for (Player player : players) {
            if (maxGrade < player.getGrade()) {
                maxPlayers.clear();
                maxGrade = player.getGrade();
                maxPlayers.add(player);
            } else if (maxGrade == player.getGrade()) {
                maxPlayers.add(player);
            }
        }
        return maxPlayers;
    }
}
