package controller;

import java.util.ArrayList;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import po.Good;
import po.GoodCustom;
import po.GoodQueryVo;
import service.GoodService;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext ac=new ClassPathXmlApplicationContext("applicationContext-*.xml");
		GoodService goodService= (GoodService) ac.getBean("goodService");
		GoodQueryVo goodQueryVo=new GoodQueryVo();
		ArrayList<GoodCustom> list=goodService.findGoodUser(goodQueryVo);
		System.out.print(list.get(0).getUsers().size());
	}

}
