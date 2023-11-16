
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class OmokSocket {
	private ServerSocket myServerSocket;
	private Socket mySocket;
	public DataOutputStream sender;
	public DataInputStream reciever;
	private int portNum;
	private String serverIP;

	public OmokSocket() {

	}

	public void beServer(int portNum){
		try{
			myServerSocket = new ServerSocket(portNum); // 서버소켓 생성
			mySocket = myServerSocket.accept(); // 클라이언트 연결 요청 대기
			sender = new DataOutputStream(mySocket.getOutputStream()); // 데이터 보내기
			reciever = new DataInputStream(mySocket.getInputStream()); // 데이터 받기
		}
		catch(IOException ioex){
			System.out.println(ioex);
		}
	}

	public void beClient(String serverIP, int portNum){
		this.serverIP = serverIP;
		try{
			mySocket = new Socket(serverIP, portNum); // 클라이언트소켓 생성, 서버와 연결
			sender = new DataOutputStream(mySocket.getOutputStream()); // 데이터 보내기
			reciever = new DataInputStream(mySocket.getInputStream()); // 데이터 받기
		}
		catch(IOException ioex){
			System.out.println(ioex);
		}
	}
}