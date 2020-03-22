package com.ks.jdfen;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class test {
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
}
