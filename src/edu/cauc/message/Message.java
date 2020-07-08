package edu.cauc.message;

import java.io.Serializable;

public class Message implements Serializable {
  private String srcUser;
  private String dstUser;

  public Message() {
    this.srcUser = null;
    this.dstUser = null;
  }

  public Message(String srcUser, String dstUser) {
    this.srcUser = srcUser;
    this.dstUser = dstUser;
  }

  public String getSrcUser() {
    return srcUser;
  }

  public void setSrcUser(String srcUser) {
    this.srcUser = srcUser;
  }

  public String getDstUser() {
    return dstUser;
  }

  public void setDstUser(String dstUser) {
    this.dstUser = dstUser;
  }

}
