package edu.cauc.server.db;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

public class User {

  private String username;
  private byte[] hashedpwd;
  private Timestamp registertime;

  public User() {
    this.username = null;
    this.hashedpwd = null;
    this.registertime = null;
  }

  public User(String username) {
    this.username = username;
    this.hashedpwd = null;
    this.registertime = null;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public byte[] getHashedpwd() {
    return hashedpwd;
  }

  public void setHashedpwd(byte[] hashedpwd) {
    this.hashedpwd = hashedpwd;
  }

  public Date getRegistertime() {
    return registertime;
  }

  public void setRegistertime(Timestamp registertime) {
    this.registertime = registertime;
  }

  @Override
  public String toString() {
    return "User{" + "username='" + username + '\'' + ", hashedpwd=" + Arrays.toString(hashedpwd) + ", registertime="
        + registertime + '}';
  }
}
