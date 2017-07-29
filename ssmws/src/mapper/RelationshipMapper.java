package mapper;

import java.util.List;

import po.User;

public interface RelationshipMapper {
	List<User> selectUserListbyUserA(String userA);
}
