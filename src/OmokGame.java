
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class OmokGame {
	protected OmokSocket mySocket;
	OmokGui gameGui;
	protected boolean end;
	protected int[][] board; //0 non, -1 white, 1 black
	//protected int ver,hor; // 쓰인곳이 없음
	protected int turn;
	public boolean myTurn;
	public boolean solo;
	public boolean start;
	private static final String[] answer = {"YES", "NO"};

	public OmokGame(int lineNum){ // lineNum = 15로 설정
		start = false;
		//solo = false;
		dataInit(lineNum);
		gameGui = new OmokGui(lineNum, this);
		baseSettingsWithPane();
		start = true;
		if(!solo && !myTurn){ // 2인용게임 & 내차례가 아니면
			otherPut();
		}
	}

	public OmokGame(int lineNum, boolean solo){ // 1인용, false를 줬을 경우 처리방법
		start = false;
		dataInit(lineNum);
		this.solo = solo;
		gameGui = new OmokGui(lineNum, this);
		start = true;
	}

	public OmokGame(int lineNum, boolean host, int portNum, String ipNum){ // 2인용, 실행안됨
		start = false;
		dataInit(lineNum);
		socketInit(host, portNum, ipNum);
		gameGui = new OmokGui(lineNum, this);
		start = true;
		if(!solo && !myTurn){
			otherPut();
		}
	}

	private void baseSettingsWithPane(){ // 1인용 2인용 게임 선택용
		String input = (String) JOptionPane.showInputDialog(null, "Do you want solo play?", 
				"Input", JOptionPane.QUESTION_MESSAGE, null, answer, answer[0]);
		if(input.equals("YES")){
			solo = true;
		}
		if(!solo){
			socketInitWithPane();
		}
	}

	private void socketInitWithPane(){ // 2인용일 경우 실행
		mySocket = new OmokSocket();
		String input = (String) JOptionPane.showInputDialog(null, "Do you want to be a host.", 
				"Input", JOptionPane.QUESTION_MESSAGE, null, answer, answer[0]);
		if(input.equals("YES")){
			String portNum = JOptionPane.showInputDialog("Enter portNum");
			mySocket.beServer(Integer.parseInt(portNum));
			myTurn = true;
		}
		else{
			String ipNum = JOptionPane.showInputDialog("Enter ServerIP");
			String portNum = JOptionPane.showInputDialog("Enter portNum");
			mySocket.beClient(ipNum, Integer.parseInt(portNum));
			myTurn = false;
		}
	}

	private void socketInit(boolean host, int portNum, String ipNum){
		mySocket = new OmokSocket();
		if(host){
			mySocket.beServer(portNum);
			myTurn = true;
		}
		else{
			mySocket.beClient(ipNum, portNum);
			myTurn = false;
		}
	}

	private void dataInit(int lineNum){
		end = false;
		turn = 1;
		// board (0,0) ~ (16, 16)
		board = new int[lineNum+2][lineNum+2];
		// (1,1) ~ (15,15) 는 0으로 초기화
		for(int i = 1; i <= lineNum; i++){
			for(int j = 1; j<= lineNum; j++){
				board[i][j] = 0;
			}
		}
		// 4, 3으로 초기화한 이유...? / 1 흑 0 빈 상태 -1 백
		// (0,0) ~ (0,16) and (16,0) ~ (16,16)까지는 4로 초기화 (왼쪽, 오른쪽)
		// lineNum + 2로 고치기
		for (int i = 0; i < lineNum; i++){
			board[0][i] = 4;
			board[lineNum + 1][i] = 4;
		}
		// (1,0) ~ (15,0) and (1,16) ~ (15,16)까지는 3으로 초기화 (위, 아래)
		// lineNum + 1로 고치기
		for (int i = 0; i <lineNum; i++){
			board[i][0] = 3;
			board[i][lineNum + 1] = 3;
		}
	}

	public void put(int x, int y){
		if(board[x][y] != 0){ // 빈 상태가 아니면 넘김
			return;
		}
		
		// 내가 놓은 위치를 내 화면에 표시
		// turn = 흑 1, 백 -1
		board[x][y] = turn;
		turn *= -1;
		myTurn = false;
		gameGui.put(x, y);
		
		if(!solo){
			try{ // x,y 좌표를 전송
				mySocket.sender.writeInt(x);
				mySocket.sender.writeInt(y);
			}
			catch(IOException ioex){
				System.out.println(ioex);
			}
		}
		
		if(WOL(x,y)){
			end = true;
			gameGui.gameEnd();
		}
		
		if(!solo){
			otherPut();
		}
	}

	public void otherPut(){
		int x = 0, y = 0;
		try{ // 상대방이 보낸 x, y좌표를 받아옴
			x = mySocket.reciever.readInt();
			y = mySocket.reciever.readInt();
		}
		catch(IOException ioex){
			System.out.println(ioex);
		}
		
		// 상대방이 놓은 위치를 내 화면에 표시
		// 흑 1, 백 -1
		board[x][y] = turn;
		turn *= -1;
		myTurn = true;
		gameGui.put(x, y);
		
		if(WOL(x,y)){
			end = true;
			gameGui.gameEnd();
		}
	}

	private boolean WOL(int x, int y){
		int[] ex = new int[2];
		int[] ey = new int[2];
		int i, a, b, k;

		for (i = 0; i< 2; i++){
			ex[i] = x;
			ey[i] = y;
		}
		k = 1;

		// b = -1, a = 0 : 놓은 돌 기준으로 11시 방향 확인
		// b = -1, a = 1 : 놓은 돌 기준으로 5시 방향 확인
		// b = 0, a = 0 : 놓은 돌 기준으로 9시 방향 확인
		// b = 0, a = 1 : 놓은 돌 기준으로 3시 방향 확인
		// b = 1, a = 0 : 놓은 돌 기준으로 7시 방향 확인
		// b = 1, a = 1 : 놓은 돌 기준으로 1시 방향 확인
		for (b = -1; b < 2; b++){
			for (a = 0; a < 2; a++){
				for (i = 1; i < 5; i++){
					if (board[ex[a] + (-1)*k][ey[a] + b*k] == turn*(-1)){ // 내가 놓은 돌 확인
						ex[a] = ex[a] + (-1)*k;
						ey[a] = ey[a] + b*k;
					}
					else{   break;  }
				} 
				k = k*(-1);
			}
			
			if (ex[1] - ex[0] + 1 >= 5){ // x값의 차이가 4이상 나면 ex) (1,1) ~ (5,5) 의 경우 오목 성립
				return true;
			}
			
			for (i = 0; i< 2; i++){
				ex[i] = x;
				ey[i] = y;
			}
			k = 1;
		}

		for (i = 0; i< 2; i++){
			ex[i] = x;
			ey[i] = y;
		}
		k = -1;
		
		// a = 0 : 놓은 돌 기준으로 12시 방향 확인
		// a = 1 : 놓은 돌 기준으로 6시 방향 확인
		for (a = 0; a < 2; a++){
			for (i = 1; i < 5; i++){
				if (board[ex[a]][ey[a] + k] == turn*(-1)){
					ex[a] = ex[a];
					ey[a] = ey[a] + k;
				}
				else{ break; }
			}
			k = k*-1;
		}
		
		if (ey[1] - ey[0] + 1 >= 5){ // y값의 차가 4 이상 나면 ex) (5,3) ~ (5,7) 의 경우 오목 성립
			return true;
		}
		
		return false;
	}

	public int whoIsTurnIsIt(){
		return turn;
	}
	
	public boolean isItend(){
		return end;
	}
}