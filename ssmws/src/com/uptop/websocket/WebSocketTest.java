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
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * @author uptop
 */
@ServerEndpoint("/websocket")
public class WebSocketTest {
	RelationshipMapper relationshipMapper=(RelationshipMapper) ContextLoader.getCurrentWebApplicationContext().getBean("relationshipMapper"); 
   
	String username;
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet<WebSocketTest>();
    public static HashMap<String,WebSocketTest> socketMap=new HashMap<String, WebSocketTest>();
    public static HashMap<String, GameState> gameMap=new HashMap<String, GameState>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
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
        
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
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
    	
        webSocketSet.remove(this);  //从set中删除
        socketMap.remove(username);
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
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
    	
        System.out.println("来自客户端的消息:" + message);
        //群发消息
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
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
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
