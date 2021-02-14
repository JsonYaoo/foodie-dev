package com.jsonyao.cs.test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.junit.Test;

import java.nio.charset.Charset;

public class BloomFilterTest {

    @Test
    public void test01(){
        // 2、防止缓存穿透方法二: 构造布隆过滤器: 漏斗、期待插入的长度、期望的精度
        // => 可见, 设置的数组越长, 误判率就越低; 同等长度, 设置精度可有效降低误判率
        int length = 100000;
        BloomFilter<CharSequence> charSequenceBloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), length, 0.000001);

        // 添加数据
        for(int i = 0; i < 100000; i++){
            charSequenceBloomFilter.put(String.valueOf(i));
        }
        // 测试精度
        int count = 0;
        for(int i = 0; i < length; i++){
            // 返回true代表可能存在, 因为存在误判; 但返回false, 则说明一定不存在, 没有误判(因为连别人的1都没有)
            if(charSequenceBloomFilter.mightContain("imooc" + i)){
                count++;
            }
        }
        System.out.println("精度为: " + count);

    }

}
