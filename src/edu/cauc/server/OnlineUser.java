package edu.cauc.server;

import edu.cauc.NetConnection;
import javafx.beans.property.SimpleStringProperty;

public class OnlineUser {

  private SimpleStringProperty username;
  private SimpleStringProperty ip;
  private SimpleStringProperty port;
  private SimpleStringProperty loginTime;
  private NetConnection conn;

  public OnlineUser(SimpleStringProperty username, SimpleStringProperty ip, SimpleStringProperty port,
      SimpleStringProperty loginTime, NetConnection conn) {
    this.username = username;
    this.ip = ip;
    this.port = port;
    this.loginTime = loginTime;
    this.conn = conn;
  }

  public String getUsername() {
    return username.get();
  }

  public SimpleStringProperty usernameProperty() {
    return username;
  }

  public void setUsername(String username) {
    this.username.set(username);
  }

  public String getIp() {
    return ip.get();
  }

  public SimpleStringProperty ipProperty() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip.set(ip);
  }

  public String getPort() {
    return port.get();
  }

  public SimpleStringProperty portProperty() {
    return port;
  }

  public void setPort(String port) {
    this.port.set(port);
  }

  public String getLoginTime() {
    return loginTime.get();
  }

  public SimpleStringProperty loginTimeProperty() {
    return loginTime;
  }

  public void setLoginTime(String loginTime) {
    this.loginTime.set(loginTime);
  }

  public NetConnection getConn() {
    return conn;
  }

  public void setConn(NetConnection conn) {
    this.conn = conn;
  }
}
