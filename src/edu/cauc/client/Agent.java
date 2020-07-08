package edu.cauc.client;

import edu.cauc.NetConnection;
import edu.cauc.message.ChatMessage;
import edu.cauc.message.Message;
import edu.cauc.message.RegisterMessage;
import edu.cauc.message.UserStateMessage;

import java.io.IOException;

/**
 * 用户代理。代替用户与服务器进行交互
 */
public class Agent {

    private NetConnection conn = null;
    private String username = null;
    private String password = null;

    public void disConnect() {
        try {
            conn.disConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRegisterMsg() {
        RegisterMessage msg = new RegisterMessage(username, password);
        try {
            conn.sendObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserOnlineMsg() {
        UserStateMessage msg = new UserStateMessage(username, password, true);
        try {
            conn.sendObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserOfflineMsg() {
        UserStateMessage msg = new UserStateMessage(username, false);
        try {
            conn.sendObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPublicChatMsg(String msgContent) {
        ChatMessage msg = new ChatMessage(username, null, msgContent);
        try {
            conn.sendObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPrivateChatMsg(String dstUser, String msgContent) {
        ChatMessage msg = new ChatMessage(username, dstUser, msgContent);
        try {
            conn.sendObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message receiveMessage() {
        Message msg = null;

        try {
            msg = (Message) conn.receiveObject();
        } catch (IOException e) {
            if (e.toString().endsWith("Connection reset")) {
                System.out.println("服务器端退出");
            } else {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return msg;
    }

    public void setConn(NetConnection conn) {
        this.conn = conn;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
