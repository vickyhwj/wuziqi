package service;

import java.util.ArrayList;





import po.User;
import mapper.RelationshipMapper;

public class RelationshipService {
	RelationshipMapper relationshipMapper;

	public void setRelationshipMapper(RelationshipMapper relationshipMapper) {
		this.relationshipMapper = relationshipMapper;
	}
	public ArrayList<User> selectUserListbyUserA(String userA){
		return (ArrayList<User>) relationshipMapper.selectUserListbyUserA(userA);
	}
	
}
