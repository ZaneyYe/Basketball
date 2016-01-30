package com.crawler.main;

import com.crawler.pojo.Crawler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by yzy mia5121@163.com on 2016/1/27.
 */
public class Main {
	public static void main(String[] args) throws IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring/*.xml");
//		String s = context.getBean(ApiService.class).doGet("http://www.baidu.com");
//		System.out.println(s);
		context.getBean(Crawler.class).begin();
	}
}
