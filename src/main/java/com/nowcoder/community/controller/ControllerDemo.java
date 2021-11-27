package com.nowcoder.community.controller;

import com.nowcoder.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author : Real
 * @date : 2021/11/4 16:16
 * @description :
 */
@Controller
@RequestMapping("/Hello")
public class ControllerDemo {

    @RequestMapping("/test")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot";
    }

    /**
     * 获取Http请求的参数，并打印
     *
     * @param request
     * @param response
     */
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + " : " + value);
        }
        // 获取请求的参数
        System.out.println(request.getParameter("Code"));

        // 设置返回的数据类型
        response.setContentType("text/html;charset=utf-8");

        // 这样的写法，最终会自动关闭 () 里面的读写流，可以简化开发，避免手动close流
        try (PrintWriter writer = response.getWriter()) {
            writer.write("<h1>牛客网</h1>");       // 写标题
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // ①GET 请求的处理 /students?current=1&limit=20 当前是第一页，一共显示20条数据
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "1") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // ②查询单个学生，id为123，不使用参数的时候，直接编排到url中时：/student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return " a student";
    }

    /**
     * 请求的参数填写的应该和html表单中的名字一致
     *
     * @param name
     * @param age
     * @return
     */
    // POST 请求参数的处理
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println("name : " + name);
        System.out.println("age : " + age);
        return "success";
    }

    // 响应HTML请求，直接使用ModelAndView完成
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        // 添加的数据是一个键值对的形式，返回就是直接返回一个对象
        modelAndView.addObject("name", "张三");
        modelAndView.addObject("age", 30);
        // 设置一个视图，规定返回的是哪一个html页面。html页面通常放在templates目录下
        // 下面的路径名，则表示返回的页面是 templates 目录下的 demo 目录下的 view.html
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    // 响应HTML请求，使用简化的方式，直接返回String
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        // 这里直接使用 Model 对象添加数据，完成数据的添加
        model.addAttribute("name", "HUT");
        model.addAttribute("age", "60");
        return "/demo/view";
    }

    // 响应 JSON 数据，处理异步请求
    // Java 对象 -> JSON 字符串 -> JS 对象，JSON 只是起到一个中间值的作用，方便将 Java 对象转换为其他语言对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody       // 不加这个注解，会认为返回一个html页面
    public Map<String, Object> getEmp() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 20);
        map.put("salary", 8000.00);
        return map;
    }

    // 响应 JSON 数据，处理异步请求
    // Java 对象 -> JSON 字符串 -> JS 对象，JSON 只是起到一个中间值的作用，方便将 Java 对象转换为其他语言对象
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody       // 不加这个注解，会认为返回一个html页面
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 20);
        map.put("salary", 8000.00);
        list.add(map);
        map = new HashMap<>();
        map.put("name", "李四");
        map.put("age", 30);
        map.put("salary", 9000.00);
        list.add(map);
        return list;
    }

    // Cookie 实例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建 Cookie 对象
        Cookie cookie = new Cookie("Code", CommunityUtil.generatorUUID());
        // 设置 Cookie 的生效范围
        cookie.setPath("/community");
        // 设置 Cookie 的生效时间
        cookie.setMaxAge(60 * 10);
        // 添加 Cookie
        response.addCookie(cookie);

        return "set Cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("Code") String code) {
        System.out.println(code);
        return "get Cookie";
    }

    // Session 示例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

}
