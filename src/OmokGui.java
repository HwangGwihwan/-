
import java.awt.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;

public class OmokGui extends JFrame{
	private OmokGame game;
	protected int lineNum;
	private OmokBoard gameBoard;

	public OmokGui(int gLineNum, OmokGame game) {
		// TODO Auto-generated constructor stub
		super("NeOP's Super Awesome Omok Game");
		setLayout(new BorderLayout());

		this.game = game;
		this.lineNum = gLineNum++; // lineNum = 15, gLineNum = 16
		gameBoard = new OmokBoard(this.lineNum, game);
		add(gameBoard, "Center"); // gameBoard를 중앙에 배치
		setVisible(true);

		gLineNum *= 30; // 16*30 = 480
		// getInsets() : 창의 내부 여백 정보를 가져오는 메서드
		// 좌30+오목판420+우30 size=480
		this.setSize(this.getInsets().left + gLineNum, this.getInsets().top + gLineNum);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}

	public void put(int x, int y){
		gameBoard.put(x, y);
	}

	public void gameEnd(){
		System.out.println("Game End");
		// turn을 상대방한테 넘긴 상태에서 내가 오목을 완성했는지 검사하므로 turn 1이면 백 승/ -1이면 흑 승
		JOptionPane.showMessageDialog(this, (game.whoIsTurnIsIt() == 1 ? "White" : "Black") 
				+ " Win!", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
	}
}