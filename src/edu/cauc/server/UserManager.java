package edu.cauc.server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理在线用户列表
 */
public class UserManager {

  private static ObservableList<OnlineUser> onlineUserList = null;
  private static HashMap<String, OnlineUser> onlineUserMap = null;

  private UserManager() {
  }

  public static HashMap<String, OnlineUser> getOnlineUserMap() {
    if (onlineUserMap == null) {
      onlineUserList = FXCollections.observableArrayList();
      onlineUserMap = new HashMap<>();
    }
    return onlineUserMap;
  }

  public static ObservableList<OnlineUser> getOnlineUserList() {
    if (onlineUserList == null) {
      onlineUserList = FXCollections.observableArrayList();
      onlineUserMap = new HashMap<>();
    }
    return onlineUserList;
  }

  /**
   * 添加一个 OnlineUser
   */
  public static void addUser(OnlineUser user) {
    onlineUserList.add(user);
    onlineUserMap.put(user.getUsername(), user);
  }

  /**
   * 删除一个 OnlineUser
   */
  public static void removeUser(OnlineUser user) {
    onlineUserList.remove(onlineUserMap.get(user.getUsername()));
    onlineUserMap.remove(user.getUsername());
  }

  /**
   * 获取所有在线用户名
   */
  public String[] getAllUsername() {
    String[] users = new String[onlineUserMap.size()];
    int i = 0;
    for (Map.Entry<String, OnlineUser> entry : onlineUserMap.entrySet()) {
      users[i++] = entry.getKey();
    }
    return users;
  }

  /**
   * 获取在线用户个数
   */
  public static int getUsersCount() {
    return onlineUserMap.size();
  }

  /**
   * 判断某个用户是否存在
   */
  public static boolean hasUser(OnlineUser user) {
    return onlineUserMap.containsKey(user.getUsername());
  }

  /**
   * 判断是否为空
   */
  public static boolean isEmpty() {
    return onlineUserMap.isEmpty();
  }

}
