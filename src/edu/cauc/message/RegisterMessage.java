package edu.cauc.message;

public class RegisterMessage extends Message {
  private String username;
  private String password;
  private boolean success;

  public RegisterMessage(String username, String password) {
    super(username, "");
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }
}
