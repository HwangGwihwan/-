
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class OmokBoard extends JPanel implements MouseListener{
	protected int lineNum;
    OmokGame game;
	int x0, y0;

	public OmokBoard(int lineNum, OmokGame game){ // lineNum 15
		this.lineNum = lineNum;
		this.x0 = 0;
		this.y0 = 0;
		this.game = game;
		addMouseListener(this);
	}

	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		for(int i = 0; i<lineNum; i++){
			// 오목 줄과 줄 사이 간격 30 시작라인:30, 끝라인:450
			g.drawLine(x0+30, 30+y0+i*30, 30+x0+30*(lineNum-1), 30+y0+i*30); // 가로줄
			g.drawLine(x0+30+i*30, 30+y0, 30+x0+i*30, 30+y0+30*(lineNum-1)); // 세로줄
		}
	}

	public void mousePressed(MouseEvent e){
		// (게임솔로가 아님& 내턴이 거짓) 혹은 게임이 시작하지 않으면
		if((!game.solo && !game.myTurn) || !game.start){
			return;
		}
		
		int x, y;
		int outerBoundary = 15 + lineNum * 30; // 465를 넘어가면 경계아웃

		if(!game.isItend()){ // 게임이 안끝났으면
			x = e.getX() + 15;
			y = e.getY() + 15;
			
			// 30으로 바꿔야하지 않나..?
			if(x < 15 || y < 15 || x >= outerBoundary || y >= outerBoundary) {
				return;
			}
			
			x = (x - (x % 30)) / 30;
			y = (y - (y % 30)) / 30;
			//System.out.println(x + " " + y);
			//System.out.println("a");
			game.put(x, y);
		}
	}

	public void put(int x, int y){
		circleDraw(this.getGraphics(), x, y);
	}

	private void circleDraw(Graphics g,int x,int y){
		x = x * 30 - 15;
		y = y * 30 - 15;
		if(game.whoIsTurnIsIt() == 1){
			g.setColor(Color.WHITE);
		}
		else{
			g.setColor(Color.BLACK);
		}
		g.fillOval(x, y, 30, 30);
	}

  @Override
  	public void mouseClicked(MouseEvent e) {
   // TODO Auto-generated method stub
  	}

  @Override
  	public void mouseEntered(MouseEvent e) {
   // TODO Auto-generated method stub
  	}

 @Override
 	public void mouseExited(MouseEvent e) {
   // TODO Auto-generated method stub
 	}

 @Override
 	public void mouseReleased(MouseEvent e) {
   // TODO Auto-generated method stub
 	}
}