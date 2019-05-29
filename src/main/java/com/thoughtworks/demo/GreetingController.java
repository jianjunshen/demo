package com.thoughtworks.demo;

import java.sql.*;

import java.util.List;

import java.util.ArrayList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Value("${username}")
    private String userName;
    @Value("${password}")
    private String passWord;

    public Connection getCon() {
        //数据库连接名称
        String username = "root";
        //数据库连接密码
        String password = "12345678";
        String driver = "com.mysql.jdbc.Driver";
        //其中test为数据库名称
        String url = "jdbc:mysql://localhost:3306/demo";
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public List<String> getSelect(String userName) {
        String sql = "select * from user where FIND_IN_SET('"+userName+"', usrname)";
        //select * from user where FIND_IN_SET("ss", usrname) ;
        //String sql = "select * from user";
        Connection conn = getCon();
        PreparedStatement pst = null;
        // 定义一个list用于接受数据库查询到的内容
        List<String> list = new ArrayList<String>();
        try {
            pst = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // 将查询出的内容添加到list中，其中userName为数据库中的字段名称
                list.add(rs.getString("usrname"));
                list.add(rs.getString("password"));
            }
            System.out.println(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }


    @RequestMapping("/login")
    public boolean login(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password
    ) {
        //System.out.println(getSelect().get(0));
        //

        if (getSelect(username).size() == 2) {
            return getSelect(username).get(0).equals(username) && getSelect(username).get(1).equals(password);
        }
        return false;
        //return true;

    }
}
