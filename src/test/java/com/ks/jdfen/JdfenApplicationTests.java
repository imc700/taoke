package com.ks.jdfen;

import com.jd.open.api.sdk.JdException;
import com.ks.jdfen.controller.FirstController;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JdfenApplicationTests {

    @Autowired
    FirstController firstController;
    @Test
    void testLink() throws JdException {
//        firstController.getLink2();
    }
    @Test
    private void test2(){
        System.out.println(1);
    }

}
