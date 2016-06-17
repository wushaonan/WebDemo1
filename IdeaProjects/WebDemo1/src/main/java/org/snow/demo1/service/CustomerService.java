package org.snow.demo1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snow.demo1.helper.DatabaseHelper;
import org.snow.demo1.model.Customer;
import org.snow.demo1.util.PropsUtil;

import java.sql.*;
import java.util.*;

/**
 * Created by Sn_Wu on 2016/6/15.
 */
public class CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    /**
     * 获取客户列表
     */
    public List<Customer> getCustomerList() {
        String sql = "select * from customer";
        return  DatabaseHelper.queryEntityList(Customer.class,sql);
    }

    /**
     * 获取客户
     */
    public Customer getCustomer(Long id) {
        String sql = "select * from customer where id = "+id;
        return DatabaseHelper.queryEntity(Customer.class,sql);
    }

    /**
     * 创建客户
     */
    public boolean createCustomer(Map<String, Object> fieldMap) {
        return DatabaseHelper.insertEntity(Customer.class,fieldMap);
    }

    /**
     * 更新客户
     */
    public boolean updateCustomer(Long id, Map<String, Object> fieldMap) {
        return DatabaseHelper.updateEntity(Customer.class,id,fieldMap);
    }

    /**
     * 删除客户
     */
    public boolean deleteCustomer(Long id) {
        return DatabaseHelper.deleteEntity(Customer.class,id);
    }
}
