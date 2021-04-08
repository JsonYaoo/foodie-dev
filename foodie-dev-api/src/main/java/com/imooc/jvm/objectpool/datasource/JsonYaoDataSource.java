package com.imooc.jvm.objectpool.datasource;

import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class JsonYaoDataSource implements DataSource {

    private GenericObjectPool<MyConnection> connectionPool;

    public JsonYaoDataSource() {
        ConnectionPooledObjectFactory connectionPooledObjectFactory = new ConnectionPooledObjectFactory();
        this.connectionPool = new GenericObjectPool<>(connectionPooledObjectFactory);
        connectionPooledObjectFactory.setPool(connectionPool);
    }

    public GenericObjectPool<MyConnection> getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(GenericObjectPool<MyConnection> connectionPool) {
        this.connectionPool = connectionPool;
    }

    // 1. 数据库连接复用的关键方法: 被连接池管理, 从连接池拿出Connection
    @Override
    public Connection getConnection() throws SQLException {
        try {
            return this.connectionPool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("获取连接失败!");
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("不支持该操作!");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("不支持该操作!");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("不支持该操作!");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("不支持该操作!");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("不支持该操作!");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("不支持该操作!");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("不支持该操作!");
    }
}
