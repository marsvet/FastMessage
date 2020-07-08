package edu.cauc.server;

import edu.cauc.Const;
import edu.cauc.NetConnection;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Controller {

	public TextArea messageWindow;
	public TableView<OnlineUser> userInfoTable;
	public TableColumn<OnlineUser, String> usernameCol;
	public TableColumn<OnlineUser, String> ipCol;
	public TableColumn<OnlineUser, String> portCol;
	public TableColumn<OnlineUser, String> loginTimeCol;

	private final int port = 9999;
	private ServerSocket serverSocket = null;
	private boolean serverStop = false;
	private static Controller currentInstance = null;

	/**
	 * UI 载入后会自动调用
	 */
	public void initialize() {
		currentInstance = this;
		usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
		ipCol.setCellValueFactory(new PropertyValueFactory<>("ip"));
		portCol.setCellValueFactory(new PropertyValueFactory<>("port"));
		loginTimeCol.setCellValueFactory(new PropertyValueFactory<>("loginTime"));

		userInfoTable.setItems(UserManager.getOnlineUserList());

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		new Thread(() -> {
			while (!serverStop) {
				try {
					Socket socket = serverSocket.accept(); // 调用serverSocket.accept()方法接受用户连接请求
					NetConnection conn = new NetConnection(socket);
					new Thread(() -> {
						new Session(conn); // 为新来的用户创建并启动一个“会话线程”
					}).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		String msgRecord = Const.dateFormat.format(new Date()) + " 服务器启动成功" + "\r\n";
		messageWindow.setText(msgRecord);
	}

	/*********** 下面几个方法是为了方便其他类调用该类，以更新 UI ***********/
	public static Controller getCurrentInstance() {
		return currentInstance;
	}

	public String getMessageWindowText() {
		return messageWindow.getText();
	}

	public void setMessageWindowText(String text) {
		messageWindow.setText(text);
	}
	/************************************************************************/

}
