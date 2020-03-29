package com.ks.jdfen;

import com.ks.jdfen.Entity.User;
import com.ks.jdfen.dao.UserDao;
import com.ks.jdfen.myutil.RedisUtil;
import com.ks.jdfen.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
public class test {

    @Resource
    UserService userService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    UserDao userDao;

    @Test
    public void test2(){
        String itemId = "https://item.jd.com/11944842773.html#none";
        try {
            if (itemId.contains("jd.com")){
                String[] split = itemId.split("html");
                String pattern = "\\d+";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(split[0]);
                if (m.find( )) {
                    System.out.println("Found value: " + m.group(0) );
                } else {
                    System.out.println("找不到商品id");
                }
            }else if(Long.parseLong(itemId)>1){
                itemId=itemId;
            }else{
                System.out.println("输入异常.");
            }
        }catch (Exception e){
            System.out.println("输入异常.");
        }


    }

    @Test
    public void testSql(){
        redisUtil.del();

        redisUtil.set("one","hi1");
        redisUtil.set("two","hi2");
        redisUtil.set("three","hi3");
        System.out.println(redisUtil.get("three"));
        System.out.println(redisUtil.get("two"));
        System.out.println(redisUtil.get("one"));
        System.out.println(redisUtil.keys("*"));

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        redisUtil.set("firstList",list);
        System.out.println(redisUtil.get("firstList"));

        List<Integer>  arr = (List<Integer>) redisUtil.get("firstList");


        User user = userService.findUserByName("imc700");
        System.out.println(user);

        List<User> all = userDao.findAll();
        System.out.println();
    }
}
