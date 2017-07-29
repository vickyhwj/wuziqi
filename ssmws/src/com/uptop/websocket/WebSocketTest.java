package com.uptop.websocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import mapper.RelationshipMapper;
import net.sf.json.JSONObject;

import org.springframework.web.context.ContextLoader;

import po.GameState;
import po.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * @ServerEndpoint ע����һ�����ε�ע�⣬���Ĺ�����Ҫ�ǽ�Ŀǰ���ඨ���һ��websocket��������,
 * ע���ֵ�������ڼ����û����ӵ��ն˷���URL��ַ,�ͻ��˿���ͨ�����URL�����ӵ�WebSocket��������
 * @author uptop
 */
@ServerEndpoint("/websocket")
public class WebSocketTest {
	RelationshipMapper relationshipMapper=(RelationshipMapper) ContextLoader.getCurrentWebApplicationContext().getBean("relationshipMapper"); 
   
	String username;
	//��̬������������¼��ǰ������������Ӧ�ð�����Ƴ��̰߳�ȫ�ġ�
    private static int onlineCount = 0;

    //concurrent�����̰߳�ȫSet���������ÿ���ͻ��˶�Ӧ��MyWebSocket������Ҫʵ�ַ�����뵥һ�ͻ���ͨ�ŵĻ�������ʹ��Map����ţ�����Key����Ϊ�û���ʶ
    public static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet<WebSocketTest>();
    public static HashMap<String,WebSocketTest> socketMap=new HashMap<String, WebSocketTest>();
    public static HashMap<String, GameState> gameMap=new HashMap<String, GameState>();
    //��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
    private Session session;

    /**
     * ���ӽ����ɹ����õķ���
     *
     * @param session ��ѡ�Ĳ�����sessionΪ��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        Map<String,List<String>> map=session.getRequestParameterMap();
        List<String> list=map.get("username");
        this.username=list.get(0);
        socketMap.put(this.username, this);
        ArrayList<User> userList=(ArrayList<User>) relationshipMapper.selectUserListbyUserA(username);
        ArrayList<String> friendOnlineList=new ArrayList<String>();
        for(User user :userList){
        	if(socketMap.containsKey(user.getUserid())){
        		friendOnlineList.add(user.getUserid());
        	}
        }
        JSONObject result = new JSONObject();
		result.element("type", 2);
		result.element("list", friendOnlineList);
		System.out.print(result.toString());
        try {
			sendMessage(result.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        JSONObject comResult = new JSONObject();
        comResult.element("type", 1);
        comResult.element("from", username);
        for(User user :userList){
        	if(socketMap.containsKey(user.getUserid())){
        		WebSocketTest socketTest=socketMap.get(user.getUserid());
        		socketTest.sendMsg(comResult.toString());
        	}
        }
        
        webSocketSet.add(this);     //����set��
        addOnlineCount();           //��������1
        System.out.println("�������Ӽ��룡��ǰ��������Ϊ" + getOnlineCount());
    }

    /**
     * ���ӹرյ��õķ���
     */
    @OnClose
    public void onClose() {
    	 JSONObject comResult = new JSONObject();
         comResult.element("type", 3);
         comResult.element("from", username);
        ArrayList<User> userList=(ArrayList<User>) relationshipMapper.selectUserListbyUserA(username);
        for(User user:userList){
        	try{
        		WebSocketTest socketTest=socketMap.get(user.getUserid());
        		socketTest.sendMessage(comResult.toString());
        	}catch(Exception e){
        		
        	}
        }
    	
        webSocketSet.remove(this);  //��set��ɾ��
        socketMap.remove(username);
        subOnlineCount();           //��������1
        System.out.println("��һ���ӹرգ���ǰ��������Ϊ" + getOnlineCount());
    }

    /**
     * �յ��ͻ�����Ϣ����õķ���
     *
     * @param message �ͻ��˷��͹�������Ϣ
     * @param session ��ѡ�Ĳ���
     */
    @OnMessage
    public void onMessage(String message, Session session) {
    	JSONObject jsonObject=JSONObject.fromObject(message);
    	int type=jsonObject.getInt("type");
    	if(type==4){
    		String to=jsonObject.getString("to");
    		JSONObject comResult = new JSONObject();
            comResult.element("type", 5);
            comResult.element("from", username);
    		try{
    			WebSocketTest webSocketTest=socketMap.get(to);
    			webSocketTest.sendMessage(comResult.toString());
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	else if(type==7){
    		String to=jsonObject.getString("to");
    		JSONObject comResult = new JSONObject();
            comResult.element("type", 71);
            comResult.element("from", username);
            try{
    			WebSocketTest webSocketTest=socketMap.get(to);
    			webSocketTest.sendMessage(comResult.toString());
    			GameState gameState=new GameState();
    			char[][] state=gameState.getState();
    			for(int i=0;i<state.length;++i)
    				for(int j=0;j<state[i].length;++j){
    					state[i][j]='o';
    				}
    			gameState.setA(username);
    			gameState.setB(to);
    			gameState.setTurn(username);
    			String key=username+" "+to;
    			gameMap.put(key, gameState);
    			WebSocketTest w1=socketMap.get(username),w2=socketMap.get(to);
    			JSONObject gameObj=new JSONObject();
    			gameObj.element("state", gameState.getState());
    			gameObj.element("A", gameState.getA());
    			gameObj.element("B", gameState.getB());
    			gameObj.element("type", 8);
    			gameObj.element("turn", gameState.getTurn());
    			System.out.println(gameObj.toString());
    			w1.sendMessage(gameObj.toString());
    			w2.sendMessage(gameObj.toString());
    			
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	else if(type==6){
    		String to=jsonObject.getString("to");
    		JSONObject comResult = new JSONObject();
            comResult.element("type", 61);
            comResult.element("from", username);
            try{
    			WebSocketTest webSocketTest=socketMap.get(to);
    			webSocketTest.sendMessage(comResult.toString());
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	else if(type==9){
    		String to=jsonObject.getString("to");
    		int x=jsonObject.getInt("x");
    		int y=jsonObject.getInt("y");
    		GameState gameState=gameMap.get(username+" "+to);
    		if(gameState==null) gameState=gameMap.get(to+" "+username);
    		gameState.changeTurn();
    		
    		try{
	    		WebSocketTest w1=socketMap.get(username),w2=socketMap.get(to);
				JSONObject gameObj=new JSONObject();
				if(gameState.play(username, x, y)==1)
					gameObj.element("winner", username);
				gameObj.element("state", gameState.getState());
				gameObj.element("A", gameState.getA());
				gameObj.element("B", gameState.getB());
				gameObj.element("type", 8);
				gameObj.element("turn", gameState.getTurn());
				System.out.println(gameObj.toString());
				w1.sendMessage(gameObj.toString());
				w2.sendMessage(gameObj.toString());
    		}catch(Exception e){
    			
    		}
    	}
    	else if(type==11){
    		String to=jsonObject.getString("to");
    		gameMap.remove(username+" "+to);
    		gameMap.remove(to+" "+username);
    		WebSocketTest socketTest=socketMap.get(to);
    		jsonObject.element("from", username);
    		System.out.print("buwan"+jsonObject.toString());
    		try {
				socketTest.sendMessage(jsonObject.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
        System.out.println("���Կͻ��˵���Ϣ:" + message);
        //Ⱥ����Ϣ
      /*  for (WebSocketTest item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }*/
    }

    /**
     * ��������ʱ����
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("��������");
        error.printStackTrace();
    }

    /**
     * ������������漸��������һ����û����ע�⣬�Ǹ����Լ���Ҫ��ӵķ�����
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketTest.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketTest.onlineCount--;
    }


    public void sendMsg(String msg) {
        for (WebSocketTest item : webSocketSet) {
            try {
                item.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }


}
