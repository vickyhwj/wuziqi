import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import service.GoodService;


public class CopyOftest {

	public static void main(String[] args) {
		ApplicationContext ac=new ClassPathXmlApplicationContext("applicationContext-*.xml");
		GoodService goodService= (GoodService) ac.getBean("goodService");
		System.out.print(goodService);
	}

}
