package com.imooc.jvm.objectpool.datasource;

import com.google.common.collect.Maps;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义数据库监控端点
 */
@Endpoint(id = "jsonYaoDataSource")
public class DataSourceEndpoint {

    private JsonYaoDataSource dataSource;

    public DataSourceEndpoint(JsonYaoDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 数据库连接池监控: 调用连接池提供的API查询监控数据
    @ReadOperation
    public Map<String, Object> pool() {
        GenericObjectPool<MyConnection> connectionPool = dataSource.getConnectionPool();
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("numActive", connectionPool.getNumActive());
        map.put("numIdle", connectionPool.getNumIdle());
        map.put("createdCount", connectionPool.getCreatedCount());
        return map;
    }
}
