package com.imooc.jvm.objectpool.datasource;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.sql.*;

/**
 * 自定义数据库连接池
 */
public class ConnectionPooledObjectFactory implements PooledObjectFactory<MyConnection> {

    private ObjectPool<MyConnection> connectionPool;

    public ObjectPool<MyConnection> getPool() {
        return connectionPool;
    }

    public void setPool(ObjectPool<MyConnection> connectionPool) {
        this.connectionPool = connectionPool;
    }

    // 2. 数据库连接复用的关键方法: 连接池创建Connection
    @Override
    public PooledObject<MyConnection> makeObject() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodie_shop_dev?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true",
                "root",
                "root");
        MyConnection myConnection = new MyConnection();
        myConnection.setConnection(connection);
        myConnection.setObjectPool(connectionPool);
        return new DefaultPooledObject<>(myConnection);
    }

    @Override
    public void destroyObject(PooledObject<MyConnection> pooledObject) throws Exception {
        pooledObject.getObject().close();
    }

    /**
     * 数据源健康检查
     * @param pooledObject
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<MyConnection> pooledObject) {
        Connection connection = pooledObject.getObject();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            int result = resultSet.getInt(1);
            return result == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<MyConnection> pooledObject) throws Exception {
        // 可以把connection额外的配置放在这里 => 定制化connection
    }

    // 4. 数据库连接复用的关键方法: 归还连接后, 关闭连接中的其他资源(即钝化操作)
    @Override
    public void passivateObject(PooledObject<MyConnection> pooledObject) throws Exception {
        // 钝化connection: 即关闭里面底层不能复用的资源, 使得资源能够被复用
        MyConnection connection = pooledObject.getObject();
        Statement statement = connection.getStatement();
        if(statement != null) {
            statement.close();
        }
    }
}
