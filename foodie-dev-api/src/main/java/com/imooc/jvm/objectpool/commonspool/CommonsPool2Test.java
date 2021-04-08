package com.imooc.jvm.objectpool.commonspool;

import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * 测试commons pool
 */
public class CommonsPool2Test {

    public static void main(String[] args) throws Exception {
        // 创建对象池
        GenericObjectPool<Money> moneyGenericObjectPool = new GenericObjectPool<>(new MoneyPooledObjectFactory());
        Money money = moneyGenericObjectPool.borrowObject();
        money.setType("RMB");
        moneyGenericObjectPool.returnObject(money);
    }
}
