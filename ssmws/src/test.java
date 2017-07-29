import java.util.ArrayList;

import mapper.RelationshipMapper;
import mapper.UserMapper;

import org.aspectj.asm.internal.RelationshipMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import po.User;
import service.GoodService;


public class test {

	public static void main(String[] args) {
		ApplicationContext ac=new ClassPathXmlApplicationContext("applicationContext-*.xml");
		RelationshipMapper relationshipMapper= (RelationshipMapper) ac.getBean("relationshipMapper");
		ArrayList<User> list=(ArrayList<User>) relationshipMapper.selectUserListbyUserA("1");
		System.out.print(list.size());
	}

}
