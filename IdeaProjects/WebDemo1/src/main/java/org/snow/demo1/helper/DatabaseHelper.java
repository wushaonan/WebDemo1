package org.snow.demo1.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snow.demo1.util.CollectionUtil;
import org.snow.demo1.util.PropsUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Sn_Wu on 2016/6/16.
 * 数据库操作助手类
 */
public class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);
    private static  final QueryRunner QUERY_RUNNER;//可以面向实体查询
    private static final ThreadLocal<Connection> CONNECTION_THREAD_LOCAL ;
    private static final BasicDataSource DATA_SOURCE;//数据库连接池

    static{
        QUERY_RUNNER = new QueryRunner();//可以面向实体查询
        CONNECTION_THREAD_LOCAL = new ThreadLocal<Connection>();//隔离线程的容器


        Properties conf = PropsUtil.loadProps("config.properties");
        String driver = conf.getProperty("jdbc.driver");
        String url = conf.getProperty("jdbc.url");
        String userName = conf.getProperty("jdbc.username");
        String password = conf.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(userName);
        DATA_SOURCE.setPassword(password);
    }
    /**
     * 获取数据库连接
     */
    public static Connection getConnection(){
        Connection conn = CONNECTION_THREAD_LOCAL.get();
        if (conn == null){
            try {
                conn = DATA_SOURCE.getConnection();
            }catch (SQLException e){
                LOGGER.error("get connection failure",e);
            }finally {
                CONNECTION_THREAD_LOCAL.set(conn);
            }
        }
        return conn;
    }
    /**
     * 查询实体类列表
     */
    public static <T>List<T> queryEntityList(Class<T> entityClass,String sql,Object... params){
        List<T> entityList;
        try {
            Connection conn = getConnection();
            entityList = QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass),params);
        }catch (SQLException e){
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        }
        return entityList;
    }
    /**
     * 查询实体类
     */
    public static <T> T queryEntity(Class<T> entityClass,String sql,Object... params){
        T entity;
        try {
            Connection conn = getConnection();
            entity = QUERY_RUNNER.query(conn,sql,new BeanHandler<T>(entityClass),params);
        }catch (SQLException e){
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        }
        return entity;
    }
    /**
     * 连表执行查询语句
     */
    public static List<Map<String,Object>> executeQuery(String sql,Object...params){
        List<Map<String,Object>> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn,sql,new MapListHandler(),params);
        }catch (SQLException e){
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        }
        return result;
    }
    /**
     * 执行更新语句（包括update,inset,delete）
     */
    public static int executeUpdate(String sql,Object...params){
        int rows = 0;
        try {
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn,sql,params);
        }catch (SQLException e){
            LOGGER.error("execute sql list failure",e);
            throw new RuntimeException(e);
        }
        return rows;
    }
    /**
     * 插入实体
     */
    public static <T> boolean insertEntity(Class<T> entityClass,Map<String,Object>fieldMap){
        if (CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can not insert entity:fieldMap is empty");
            return  false;
        }
        String sql = "insert into "+getTableName(entityClass);
        StringBuffer columns = new StringBuffer("(");
        StringBuffer values = new StringBuffer("(");
        for (String fieldName : fieldMap.keySet()){
            columns.append(fieldName).append(",");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "),columns.length(),")");
        values.replace(values.lastIndexOf(", "),values.length(),")");
        sql += columns + "values " + values;
        LOGGER.info(sql);
        Object[] params = fieldMap.values().toArray();
        return executeUpdate(sql,params) == 1;
    }
    /**
     * 更新实体类
     */
    public static <T> boolean updateEntity(Class<T> entityClass,long id,Map<String,Object> fieldMap){
        if (CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can not insert entity:fieldMap is empty");
            return  false;
        }
        String sql = "update "+getTableName(entityClass) + " set ";
        StringBuffer columns = new StringBuffer();
        for (String fieldName : fieldMap.keySet()){
            columns.append(fieldName).append("=?, ");
        }
        sql += columns.substring(0,columns.lastIndexOf(", "))+ "where id=?";

        List<Object> paramsList = new ArrayList<Object>();
        paramsList.addAll(fieldMap.values());
        paramsList.add(id);
        Object[] params = paramsList.toArray();

        return executeUpdate(sql,params) == 1;
    }
    /**
     * 删除实体类
     */
    public static <T> boolean deleteEntity(Class<T> entityClass,long id){
        String sql = "delete from " + getTableName(entityClass) + " where id=?";
        return executeUpdate(sql,id) == 1;
    }
//    /**
//     * 关闭数据库连接
//     */
//    public static void closeConnection(){
//        Connection conn = CONNECTION_THREAD_LOCAL.get();
//        if (conn != null){
//         try {
//             conn.close();
//         }catch (SQLException e){
//             LOGGER.error("close connection failure",e);
//         }finally {
//             CONNECTION_THREAD_LOCAL.remove();
//         }
//        }
//    }
    private static String getTableName(Class<?> entityClass){
        return entityClass.getSimpleName();
    }
}
