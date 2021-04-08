package com.imooc.jvm.objectpool.commonspool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 自定义PoolObjectFactory
 */
public class MoneyPooledObjectFactory implements PooledObjectFactory<Money> {

    public static final Logger LOGGER = LoggerFactory.getLogger(MoneyPooledObjectFactory.class);

    @Override
    public PooledObject<Money> makeObject() throws Exception {
        DefaultPooledObject<Money> pooledObject = new DefaultPooledObject<>(new Money("USD", new BigDecimal("1")));
        LOGGER.info("makeObject...state={}", pooledObject.getState());
        return pooledObject;
    }

    @Override
    public void destroyObject(PooledObject<Money> pooledObject) throws Exception {
        LOGGER.info("destroyObject...state={}", pooledObject.getState());
    }

    @Override
    public boolean validateObject(PooledObject<Money> pooledObject) {
        LOGGER.info("validateObject...state={}", pooledObject.getState());
        return true;
    }

    @Override
    public void activateObject(PooledObject<Money> pooledObject) throws Exception {
        LOGGER.info("activateObject...state={}", pooledObject.getState());
    }

    @Override
    public void passivateObject(PooledObject<Money> pooledObject) throws Exception {
        LOGGER.info("passivateObject...state={}", pooledObject.getState());
    }
}
