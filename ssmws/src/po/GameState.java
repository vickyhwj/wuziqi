package po;

public class GameState {
	char[][] state=new char[20][20];
	String A;
	String B;
	String turn;
	public char[][] getState() {
		return state;
	}
	public void setState(char[][] state) {
		this.state = state;
	}
	public String getA() {
		return A;
	}
	public void setA(String a) {
		A = a;
	}
	public String getB() {
		return B;
	}
	public void setB(String b) {
		B = b;
	}
	public String getTurn() {
		return turn;
	}
	public void setTurn(String turn) {
		this.turn = turn;
	}
	public void changeTurn(){
		if(turn==A){
			turn=B;
		}
		else turn=A;
	}
	public int play(String username,int x,int y){
		if(x==-1) return 0;
		state[x][y]='b';
		if(username==A)
			state[x][y]='a';
		int h=1;
		for(int i=1;x-i>=0&&state[x-i][y]==state[x][y];++i){
			++h;
			if(h>=5) return 1;
		}
		for(int i=1;x+i<state.length&&state[x+i][y]==state[x][y];++i){
			++h;
			if(h>=5) return 1;
		}
		h=1;
		for(int i=1;x-i>=0&&y-i>=0&&state[x-i][y-i]==state[x][y];++i){
			++h;
			if(h>=5) return 1;
		}
		for(int i=1;x+i<state.length&&y+i<state.length&&state[x+i][y+i]==state[x][y];++i){
			++h;
			if(h>=5) return 1;
		}
		h=1;
		for(int i=1;x-i>=0&&y+i<state.length&&state[x-i][y+i]==state[x][y];++i){
			++h;
			if(h>=5) return 1;
		}
		for(int i=1;x+i<state.length&&y-i>=0&&state[x+i][y-i]==state[x][y];++i){
			++h;
			if(h>=5) return 1;
		}
		return 0;
			
	}

}
