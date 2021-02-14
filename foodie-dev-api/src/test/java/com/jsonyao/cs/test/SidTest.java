package com.jsonyao.cs.test;

import com.imooc.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class SidTest {

    @Autowired
    private Sid sid;

    @Test
    public void testSid(){
        System.out.println(sid.nextShort());
    }
}
