package edu.cauc.client;

import edu.cauc.Const;
import edu.cauc.NetConnection;
import edu.cauc.message.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Date;

public class Controller {

	public TextField usernameTextField;
	public PasswordField passwordTextField;
	public Button loginButton;
	public Button registerButton;
	public TextArea messageWindow;
	public ListView<String> onlineUsersWindow;
	public TextField messageInputField;
	public Button sendMessageButton;
	public Button sendFileButton;

	private final Agent agent = new Agent();
	private final ObservableList<String> userList = FXCollections.observableArrayList();
	private final String host = "localhost";
	private final int port = 9999;
	private boolean listenerStop = false;
	private String selectedUser = null;

	/**
	 * UI 载入后会自动调用
	 */
	public void initialize() {
		onlineUsersWindow.setItems(userList);

		// 监听 listView，当选定的值改变时，将新值存入 selectedUser 中。
		onlineUsersWindow.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null)
					selectedUser = newValue;
			}
		});
	}

	/**
	 * 响应用户注册事件
	 */
	public void userRegister() {
		listenerStop = false;
		loginButton.setDisable(true); // 禁用登录按钮
		String username = usernameTextField.getText().trim();
		String password = passwordTextField.getText().trim();
		if (!"".equals(username) && !"".equals(password)) {
			NetConnection conn = null;
			try {
				conn = new NetConnection(host, port);
			} catch (IOException e) {
				new Notification(2, "服务器I/O错误，或服务器未启动").show();
				e.printStackTrace();
				System.exit(0);
			}
			agent.setConn(conn);
			agent.setUsername(username);
			agent.setPassword(password);
			agent.sendRegisterMsg();

			new Thread(new Listener()).start();
		} else {
			new Notification(2, "用户名或密码不能为空！").show();
		}
	}

	/**
	 * 响应用户登录事件
	 */
	public void userLogin() {
		String loginButtonText = loginButton.getText();
		if ("登录".equals(loginButtonText)) {
			String username = usernameTextField.getText().trim();
			String password = passwordTextField.getText().trim();
			if (!"".equals(username) && !"".equals(password)) {
				NetConnection conn = null;
				try {
					conn = new NetConnection(host, port);
				} catch (IOException e) {
					new Notification(2, "服务器I/O错误，或服务器未启动").show();
					e.printStackTrace();
					System.exit(0);
				}
				agent.setConn(conn);
				agent.setUsername(username);
				agent.setPassword(password);
				agent.sendUserOnlineMsg(); // 向服务器发送用户上线信息

				// userList.add(username);

				listenerStop = false;
				new Thread(new Listener()).start(); // 创建“后台监听线程”,监听并处理服务器传来的信息
			} else {
				new Notification(2, "用户名或密码不能为空！").show();
			}
		} else if ("退出".equals(loginButtonText)) {
			boolean confirm = new Notification(1, "确认退出程序吗？").show();
			if (confirm) {
				agent.sendUserOfflineMsg(); // 发送用户下线消息
			}
		}
	}

	/**
	 * 响应发送消息事件
	 */
	public void sendChatMessage() {
		String msgContent = messageInputField.getText();
		if (messageInputField.getText() == null)
			return;
		messageInputField.setText(null);
		System.out.println(selectedUser);
		if (!"".equals(msgContent)) {
			if (selectedUser == null) {
				agent.sendPublicChatMsg(msgContent); // 将消息文本框中的内容作为公聊消息发送给服务器

				String msgRecord = Const.dateFormat.format(new Date()) + " 向大伙儿说:" + msgContent + "\n"; // 在“消息记录”文本框中用蓝色显示发送的消息及发送时间
				messageWindow.setText(messageWindow.getText() + msgRecord);
			} else {
				System.out.println("111");
				agent.sendPrivateChatMsg(selectedUser, msgContent);

				String msgRecord = Const.dateFormat.format(new Date()) + " 向" + selectedUser + "说:" + msgContent + "\n"; // 在“消息记录”文本框中用蓝色显示发送的消息及发送时间
				messageWindow.setText(messageWindow.getText() + msgRecord);
			}
		}
	}

	/**
	 * 后台监听线程类
	 */
	class Listener implements Runnable {

		@Override
		public void run() {
			while (!listenerStop) {
				Message msg = agent.receiveMessage();
				if (msg == null)
					continue;
				if (msg instanceof RegisterMessage) {
					processRegisterMessage((RegisterMessage) msg);
				} else if (msg instanceof UserStateMessage) {
					processUserStateMessage((UserStateMessage) msg); // 处理用户状态消息
				} else if (msg instanceof ChatMessage) {
					processChatMessage((ChatMessage) msg); // 处理聊天消息
				} else if (msg instanceof FileMessage) {
					processFileMessage((FileMessage) msg);
				} else {
					System.err.println("服务器返回了未知的消息类型！");
				}
			}
		}

		private void processRegisterMessage(RegisterMessage msg) {
			Platform.runLater(() -> { // 其他线程要修改 UI 线程的代码必须放到这个方法里
				if (msg.isSuccess()) {
					new Notification(3, "注册成功！").show();
				} else {
					new Notification(2, "注册失败！").show();
				}
				loginButton.setDisable(false); // 解禁
				listenerStop = true;
				agent.disConnect();
			});

		}

		/**
		 * 处理用户状态消息
		 */
		private void processUserStateMessage(UserStateMessage msg) {
			Platform.runLater(() -> { // 其他线程要修改 UI 线程的代码必须放到这个方法里
				if (msg.isUserOnline()) {
					if (msg.isSuccess() == null) {
						String srcUser = msg.getSrcUser();
						userList.add(srcUser);
						String msgRecord = Const.dateFormat.format(new Date()) + " " + srcUser + "上线了!\r\n";
						messageWindow.setText(messageWindow.getText() + msgRecord);
					} else {
						if (msg.isSuccess() == true) { // 用户登录成功时服务器返回的消息
							sendMessageButton.setDisable(false);
							sendFileButton.setDisable(false);
							loginButton.setText("退出");
							new Notification(3, "登录成功！").show();
						} else if (msg.isSuccess() == false) { // 用户登录失败时服务器返回的消息
							new Notification(2, "登录失败！").show();
							listenerStop = true;
							agent.disConnect();
						} else
							return;
					}
				} else if (msg.isUserOffline()) { // 用户下线消息
					// if (userList.contains(srcUser)) {
					String srcUser = msg.getSrcUser();
					userList.remove(srcUser); // 在“在线用户”列表中删除下线的用户名
					String msgRecord = Const.dateFormat.format(new Date()) + " " + srcUser + "下线了!\r\n"; // 用绿色文字将用户名和用户下线时间添加到“消息记录”文本框中
					messageWindow.setText(messageWindow.getText() + msgRecord);
					// }
				}
			});
		}

		/**
		 * 处理服务器转发来的聊天消息
		 */
		private void processChatMessage(ChatMessage msg) {
			if (msg.isPubChatMessage()) {
				String msgRecord = Const.dateFormat.format(new Date()) + " " + msg.getSrcUser() + "说: " + msg.getMsgContent()
						+ "\r\n";
				messageWindow.setText(messageWindow.getText() + msgRecord);
			} else {
				String msgRecord = Const.dateFormat.format(new Date()) + " " + msg.getSrcUser() + "私聊你说: " + msg.getMsgContent()
						+ "\r\n";
				messageWindow.setText(messageWindow.getText() + msgRecord);
			}
		}

		private void processFileMessage(FileMessage msg) {
		}

	}

}
