package edu.cauc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetConnection {

  private Socket socket;
  ObjectInputStream ois;
  ObjectOutputStream oos;

  public NetConnection(Socket socket) throws IOException {
    this.socket = socket;
    // 将socket的输入流和输出流分别封装成对象输入流和对象输出流
    oos = new ObjectOutputStream(socket.getOutputStream());
    ois = new ObjectInputStream(socket.getInputStream());
  }

  public NetConnection(String host, int port) throws IOException {
    socket = new Socket(host, port);
    // 将socket的输入流和输出流分别封装成对象输入流和对象输出流
    oos = new ObjectOutputStream(socket.getOutputStream());
    ois = new ObjectInputStream(socket.getInputStream());
  }

  public void disConnect() throws IOException {
    oos.close();
    ois.close();
    socket.close();
  }

  public void sendObject(Object obj) throws IOException {
    synchronized (oos) {
      oos.writeObject(obj);
      oos.flush();
    }
  }

  public Object receiveObject() throws IOException, ClassNotFoundException {
    synchronized (ois) {
      return ois.readObject();
    }
  }

  public Socket getSocket() {
    return socket;
  }

  public ObjectInputStream getOis() {
    return ois;
  }

  public ObjectOutputStream getOos() {
    return oos;
  }
}
