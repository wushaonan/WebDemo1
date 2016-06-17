package org.snow.demo1.controller;

import org.snow.demo1.model.Customer;
import org.snow.demo1.service.CustomerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Sn_Wu on 2016/6/15.
 * 进入客户列表 界面
 */
@WebServlet(name="customer",urlPatterns={"/customer"})
public class CustomerServlet extends HttpServlet {

    private CustomerService customerServicec;

    @Override
    public void init() throws ServletException{
        customerServicec = new CustomerService();
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Customer> customerList = customerServicec.getCustomerList();
        request.setAttribute("customerList",customerList);
        request.getRequestDispatcher("/customer.jsp").forward(request,response);
    }
}
