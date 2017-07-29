<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>

<head>
<title>index Page</title>
<link href="css/bootstrap.css" rel="stylesheet" />
<style>
li:hover {
	background-color: #bab0b0;
}

#gameshow {
	width: 1040px;
	overflow: auto;
}

#gameshow>button[type=button] {
	float: left;
	width: 50px;
	height: 50px;
}

#userList {
	position: absolute;
	width: 250px;
	z-index: 100;
}

.head {
	height: 80px;
	width: 100%;
}

.head>* {
	float: left;
}

.h1vs {
	padding-left: 100px;
	padding-right: 100px;
}
.mydiv {
    width: 40px;
    height: 40px;
    background-color: blue;
    margin-top: 20px;
    margin-left: 10px;
}
.oppdiv {
    width: 40px;
    height: 40px;
    background-color: blue;
    margin-top: 20px;
    margin-right: 10px;
}
.middiv {
    width: 100%;
    overflow: auto;
    height: 1900px;
}
.middiv>div{
	float:left;
}
.leftside{
	position: relative;
}

#userList {
    left: 40px;
    top: 0px;
}
.btn.btn-default {
    border-radius: 0px;
}


</style>
</head>

<body style="padding-left: 10%">





	<div class="head">
		<h1>${username }</h1>
		<div  class="mydiv"></div>
		<h1 class="h1vs">vs</h1>
		<div class="oppdiv"></div>
		<h1 id="opp">jb</h1>
	</div>
	<h2 id="time"></h2>
	<h3 id="turn"></h3>
	<div class="middiv"> 
		<div class="leftside">
			<div>
				<button type="button" class="btn btn-default" aria-label="Left Align"
					onclick="$('#userList').css('display')=='block'?  $('#userList').hide():$('#userList').show()">
				<span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>
				</button>
			</div>
			<div>
				<button type="button" class="btn btn-default" aria-label="Left Align"
					onclick="$('#userList').css('display')=='block'?  $('#userList').hide():$('#userList').show()">
					<span class="glyphicon glyphicon-heart-empty" aria-hidden="true"></span>
				</button>
			</div>
			<ul class="list-group" id="userList" >
				<c:forEach items="${list }" var="keyword">
					<li tag="${keyword.userid }" class="list-group-item" >${keyword.userid }</li>
		
				</c:forEach>
			</ul>
		</div>


	<div id="gameshow"></div>
	</div>
</body>

</html>

<script src="<%=request.getContextPath()%>/js/jquery-2.1.3.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.myConfirm.js"></script>
<script type="text/javascript">
	var websocket = null;
	var A, B;
	var username = "${username}";
	var timer;

	var state, turn;
	function clear(gameState) {
		window.clearInterval(timer)
		if (gameState.winner != null) {
			//alert("win " + gameState.winner);
			$.confirm({
				title : gameState.winner + "赢了"
			})
		}
		A = gameState.A;
		B = gameState.B;
		if(A==username){
			$(".mydiv").css("background-color",'red');
			$(".oppdiv").css("background-color",'blue');
			$("#opp").text(B)
		}
		else{
			$(".mydiv").css("background-color",'blue');
			$(".oppdiv").css("background-color",'red');
			$("#opp").text(A)
		}
		state = gameState.state;
		turn = gameState.turn;
		$("#turn").text("轮到" + turn);
		$('#gameshow').html('');
		for (var i = 0; i < state.length; ++i)
			for (var j = 0; j < state[i].length; ++j) {
				$('#gameshow')
						.append(
								"<button type='button' value='" + state[i][j] + "' x='" + i + "' y='" + j + "'></button>");
				var by = $("#gameshow")
						.find('button[x=' + i + '][y=' + j + ']');
				if (by.val() == 'a') {
					by.css('background-color', 'red');
					by.attr("class", "glyphicon glyphicon-ok")
				} else if (by.val() == 'b') {
					by.css('background-color', 'blue');
					by.attr("class", "glyphicon glyphicon-remove")
				}
			}
		//if(turn=="${username}")
		timer = daojishi();

	}
	//判断当前浏览器是否支持WebSocket
	if ('WebSocket' in window) {
		websocket = new WebSocket(
				"ws://localhost:8080/ssmws/websocket?username=${username}");
		//连接发生错误的回调方法
		websocket.onerror = function() {
			//  setMessageInnerHTML("WebSocket连接发生错误");
		};

		//连接成功建立的回调方法
		websocket.onopen = function() {
			// setMessageInnerHTML("WebSocket连接成功");
			alert("success")
		}

		//接收到消息的回调方法
		websocket.onmessage = function(event) {

			event = JSON.parse(event.data);

			//  setMessageInnerHTML(event.data);
			if (event.type == 2) {
				var list = event.list;
				for (var i = 0; i < list.length; ++i)
					$('li[tag=' + list[i] + ']').append(
							'<span class="badge">online</span>')
			} else if (event.type == 1) {
				$('li[tag=' + event.from + ']').append(
						'<span class="badge">online</span>')
			} else if (event.type == 3) {
				$('li[tag=' + event.from + ']').find('span').remove();
			} else if (event.type == 5) {

				//var r = confirm(event.from + " add");
				$.confirm({
					title : event.from + "邀请你对战",
					content : 'Simple confirm!',
					confirmButton : 'Yes',
					cancelButton : 'NO',
					confirm : function() {
						var message = new Object();
						message['type'] = 7;
						message['to'] = event.from;
						message = JSON.stringify(message);
						websocket.send(message);
					},
					cancel : function() {
						var message = new Object();
						message['type'] = 6;
						message['to'] = event.from;
						message = JSON.stringify(message);
						websocket.send(message);
					}
				});
				// if (r == true) {
				// 	var message = new Object();
				// 	message['type'] = 7;
				// 	message['to'] = event.from;
				// 	message = JSON.stringify(message);
				// 	websocket.send(message);

				// } else {
				// 	var message = new Object();
				// 	message['type'] = 6;
				// 	message['to'] = event.from;
				// 	message = JSON.stringify(message);
				// 	websocket.send(message);

				// }
			} else if (event.type == 71) {
				$.confirm({
					title : event.from + "同意了",
					content : 'Simple confirm!'
				})
				//alert(event.from + "同意了")
			} else if (event.type == 61) {
				$.confirm({
					title : event.from + "拒绝了",
					content : 'Simple confirm!'
				})
				//alert(event.from + "拒绝了")
			} else if (event.type == 8) {
				//	alert(JSON.stringify(event))
				clear(event);
			} else if (event.type == 11) {

				$.confirm({
					title : event.from + " 离开了",
					content : 'Simple confirm!'
				})
				$("#gameshow").html('');
			}

		}

		//连接关闭的回调方法
		websocket.onclose = function() {
			// setMessageInnerHTML("WebSocket连接关闭");
		}

		//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
		window.onbeforeunload = function() {

			closeWebSocket();
		}
	} else {
		alert('当前浏览器 Not support websocket')
	}

	//关闭WebSocket连接
	function closeWebSocket() {
		var x;
		if (A != undefined && B != undefined) {
			x = (A == "${username}" ? B : A);
			var message = new Object();
			message['type'] = 11;
			message['to'] = x;
			message = JSON.stringify(message);
			websocket.send(message);
		}
		websocket.close();
	}

	//发送消息
	function send() {
		var message = document.getElementById('text').value;
		websocket.send(message);
	}
</script>
<script>
	$('li').click(function(e) {
		e.preventDefault();
		var tag = $(this).attr('tag');
		var message = new Object();
		message['type'] = 4;
		message['to'] = tag;
		message = JSON.stringify(message);
		websocket.send(message);

	});
	$('#gameshow').on('click', 'button[type=button]', function() {
		if (turn != "${username}")
			return;
		if ($(this).val() != 'o')
			return;
		var x = $(this).attr('x');
		var y = $(this).attr('y');
		var message = new Object();
		message['type'] = 9;
		message['to'] = (A == "${username}" ? B : A);
		message['x'] = parseInt(x);
		message['y'] = parseInt(y);
		message = JSON.stringify(message);
		websocket.send(message);
		window.clearInterval(timer);

	})
</script>
<script src="<%=request.getContextPath()%>/js/index1.js"
	charset="gb2312"></script>