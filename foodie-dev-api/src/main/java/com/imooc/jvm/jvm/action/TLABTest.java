package com.imooc.jvm.jvm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试TLAB对性能的影响
 * => -XX:+UseTLAB: 1219ms
 *    -XX:-UseTLAB: 3686ms
 * => 测试结论: TLAB对性能的提升还是比较可观的, 而且在多线程竞争激烈的场景下性能提升更加明显
 */
public class TLABTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TLABTest.class);
    private TLABObj tlabObj;

    public static void main(String[] args) {
        TLABTest test = new TLABTest();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1_0000_0000; i++) {
            test.tlabObj = new TLABObj();
        }
        System.out.println(test.tlabObj);
        long end = System.currentTimeMillis();
        LOGGER.info("花费{}ms", end - start);
    }
}

class TLABObj {
}