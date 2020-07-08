package edu.cauc.server;

import edu.cauc.Const;
import edu.cauc.NetConnection;
import edu.cauc.Util;
import edu.cauc.message.*;
import edu.cauc.server.db.DBConnection;
import edu.cauc.server.db.User;
import edu.cauc.server.db.UserDao;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

public class Session {

	private NetConnection conn = null;
	private OnlineUser currentUser = null;
	private Controller controller = null;
	private boolean sessionStop = false;

	public Session(NetConnection conn) {
		this.conn = conn;
		this.controller = Controller.getCurrentInstance();

		try {
			while (!sessionStop) {
				Object obj = conn.receiveObject();
				if (obj == null)
					continue;
				Message msg = (Message) obj;
				if (msg instanceof RegisterMessage) {
					processRegisterMessage((RegisterMessage) msg); // 处理用户注册消息
				} else if (msg instanceof UserStateMessage) {
					processUserStateMessage((UserStateMessage) msg); // 处理用户状态消息
				} else if (msg instanceof ChatMessage) {
					processChatMessage((ChatMessage) msg); // 处理聊天消息
				} else if (msg instanceof FileMessage) {
					processFileMessage((FileMessage) msg); // 处理文件消息
				} else {
					// 这种情况对应着用户发来的消息格式 错误，应该发消息提示用户，这里从略
					System.err.println("用户发来的消息格式错误!");
				}
			}
		} catch (IOException e) {
			if (e.toString().endsWith("Connection reset")) {
				System.out.println("客户端退出");
				// 如果用户未发送下线消息就直接关闭了客户端，应该在这里补充代码，删除用户在线信息
			} else {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.disConnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 向其它用户转发消息
	private void transferMsgToOtherUsers(Message msg) {
		for (OnlineUser user : UserManager.getOnlineUserList()) {
			if (!currentUser.getUsername().equals(user.getUsername())) {
				try {
					user.getConn().sendObject(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 处理用户注册消息
	 */
	private void processRegisterMessage(RegisterMessage msg) {
		String username = msg.getUsername();
		String password = msg.getPassword();

		Connection dbConn = DBConnection.getConnection();
		UserDao userDao = new UserDao();
		User user = new User();
		user.setUsername(username);
		user.setHashedpwd(Util.calcSha1(password));
		msg.setPassword(null);
		if (!userDao.isUserExisted(dbConn, user)) {
			userDao.insertUser(dbConn, user);
			msg.setSuccess(true);
			String msgRecord = String.format("用户 %s 注册成功！\r\n", username);
			controller.setMessageWindowText(controller.getMessageWindowText() + msgRecord);
		} else {
			msg.setSuccess(false);
		}

		try {
			conn.sendObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sessionStop = true;
	}

	/**
	 * 处理用户状态消息
	 */
	private void processUserStateMessage(UserStateMessage msg) {
		String srcUser = msg.getSrcUser();

		String userIp = conn.getSocket().getInetAddress().getHostAddress();
		int userPort = conn.getSocket().getPort();
		String userLoginTime = Const.dateFormat.format(new Date());
		OnlineUser onlineUser = new OnlineUser( // 创建一个“在线用户”对象
				new SimpleStringProperty(srcUser), new SimpleStringProperty(userIp),
				new SimpleStringProperty(String.valueOf(userPort)), new SimpleStringProperty(userLoginTime), conn);

		if (msg.isUserOnline()) {
			String password = msg.getPassword();

			Connection dbConn = DBConnection.getConnection();
			UserDao userDao = new UserDao();
			User user = new User();
			user.setUsername(srcUser);
			user.setHashedpwd(Util.calcSha1(password));

			msg.setPassword(null); // 密码用完后要设为空，因为这个消息还要转发给其他client

			if (UserManager.hasUser(onlineUser)) {
				msg.setSuccess(false);
			} else if (!userDao.isUserExisted(dbConn, user)) {
				msg.setSuccess(false);
			} else {
				User dbUser = new User(srcUser);
				userDao.loadUser(dbConn, dbUser);
				if (!Arrays.equals(Util.calcSha1(password), dbUser.getHashedpwd())) {
					msg.setSuccess(false);
				} else {
					msg.setSuccess(true);
				}
			}

			try {
				conn.sendObject(msg); // 将消息返回给 client，作为响应
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (!msg.isSuccess())
				return; // 如果不成功，不进行下一步，直接退出

			currentUser = onlineUser;
			msg.setSuccess(null); // 转发前将 success 设为 null
			transferMsgToOtherUsers(msg); // 向所有其它在线用户转发用户上线消息

			/* 向新上线的用户转发当前在线用户列表 */
			Iterator<OnlineUser> iter = UserManager.getOnlineUserList().iterator();
			try {
				while (iter.hasNext()) {
					UserStateMessage userStateMessage = new UserStateMessage(iter.next().getUsername(), true);
					conn.sendObject(userStateMessage);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			UserManager.addUser(onlineUser); // 将用户加入到“在线用户”列表中

			String msgRecord = String.format("%s %s(%s)上线了！\r\n", userLoginTime, srcUser, userIp);
			controller.setMessageWindowText(controller.getMessageWindowText() + msgRecord); // 用绿色文字将用户名和用户上线时间添加到“消息记录”文本框中
		} else { // 用户下线消息
			String msgRecord = String.format("%s %s(%s)下线了！\r\n", userLoginTime, srcUser, userIp);
			controller.setMessageWindowText(controller.getMessageWindowText() + msgRecord); // 用绿色文字将用户名和用户下线时间添加到“消息记录”文本框中
			UserManager.removeUser(onlineUser); // 在“在线用户列表”中删除下线用户
			transferMsgToOtherUsers(msg); // 将用户下线消息转发给所有其它在线用户
			msg.setSuccess(true);
			try {
				conn.sendObject(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 处理用户发来的聊天消息
	private void processChatMessage(ChatMessage msg) {
		String srcUser = msg.getSrcUser();
		String dstUser = msg.getDstUser();
		String msgContent = msg.getMsgContent();

		String userIp = conn.getSocket().getInetAddress().getHostAddress();
		int userPort = conn.getSocket().getPort();
		String userLoginTime = Const.dateFormat.format(new Date());
		OnlineUser user = new OnlineUser( // 创建一个“在线用户”对象
				new SimpleStringProperty(srcUser), new SimpleStringProperty(userIp),
				new SimpleStringProperty(String.valueOf(userPort)), new SimpleStringProperty(userLoginTime), conn);

		if (UserManager.hasUser(user)) {
			String msgRecord = String.format("%s %s说：%s\r\n", userLoginTime, srcUser, msgContent);
			controller.setMessageWindowText(controller.getMessageWindowText() + msgRecord); // 用黑色文字将收到消息的时间、发送消息的用户名和消息内容添加到“消息记录”文本框中
			if (msg.isPubChatMessage()) {
				// 将公聊消息转发给所有其它在线用户
				transferMsgToOtherUsers(msg);
			} else {
				// 将私聊消息转发给目标用户
				try {
					UserManager.getOnlineUserMap().get(dstUser).getConn().sendObject(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// 这种情况对应着用户未发送上线消息就直接发送了聊天消息，应该发消息提示客户端，这里从略
			System.err.println("用启未发送上线消息就直接发送了聊天消息");
		}
	}

	/**
	 * 处理文件消息
	 */
	private void processFileMessage(FileMessage msg) {
	}

}
