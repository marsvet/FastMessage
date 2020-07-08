package edu.cauc.message;

public class UserStateMessage extends Message {
  private boolean userOnline;
  private String password;
  private Boolean success; // 这里用包装类 Boolean。默认为 null。当为 true 或 false 时，证明该消息是服务器返回的响应消息，为 null
                           // 时，证明这是服务器转发过来的其他用户的状态消息。

  public UserStateMessage(String srcUser, boolean userOnline) {
    super(srcUser, "");
    this.userOnline = userOnline;
    this.success = null;
  }

  public UserStateMessage(String srcUser, String password, boolean userOnline) {
    super(srcUser, "");
    this.userOnline = userOnline;
    this.password = password;
    this.success = null;
  }

  public boolean isUserOnline() {
    return userOnline;
  }

  public boolean isUserOffline() {
    return !userOnline;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean isSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  @Override
  public String toString() {
    return "UserStateMessage{" + "userOnline=" + userOnline + ", password='" + password + '\'' + ", success=" + success
        + '}';
  }
}
