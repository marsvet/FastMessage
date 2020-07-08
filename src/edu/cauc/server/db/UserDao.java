package edu.cauc.server.db;

import java.sql.*;
import java.util.LinkedList;

public class UserDao {

	public boolean isUserExisted(Connection conn, User user) {
		String username = user.getUsername();

		try {
			PreparedStatement psTest = conn.prepareStatement("select * from USERTABLE where USERNAME=?",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			psTest.setString(1, username);
			ResultSet rs = psTest.executeQuery();
			rs.last(); // 光标移动到最后一行
			int n = rs.getRow(); // 获取当前行号
			psTest.close();
			rs.close();
			if (n != 0)
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// Insert a new user into the USERTABLE table
	public boolean insertUser(Connection conn, User user) {
		String username = user.getUsername();
		byte[] hashedPwd = user.getHashedpwd();

		try {
			PreparedStatement psInsert = conn.prepareStatement("insert into USERTABLE values (?,?,?)");
			psInsert.setString(1, username);
			psInsert.setBytes(2, hashedPwd);
			psInsert.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			psInsert.executeUpdate();
			psInsert.close();
			System.out.println("成功注册新用户" + username);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("用户" + username + "已经存在");

		return false;
	}

	public boolean deleteUser(Connection conn, User user) {
		String username = user.getUsername();

		try {
			PreparedStatement psDelete = conn.prepareStatement("delete from USERTABLE where USERNAME=?");
			psDelete.setString(1, username);
			int n = psDelete.executeUpdate();
			psDelete.close();
			if (n > 0) {
				System.out.println("成功删除用户" + username);
				return true;
			} else {
				System.out.println("删除用户" + username + "失败");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean loadUser(Connection conn, User user) {
		String username = user.getUsername();

		try {
			PreparedStatement ps = conn.prepareStatement("select * from USERTABLE where USERNAME=?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			boolean h = rs.next();
			if (h) {
				user.setUsername(rs.getString("USERNAME"));
				user.setHashedpwd(rs.getBytes("HASHEDPWD"));
				user.setRegistertime(rs.getTimestamp("REGISTERTIME"));
				ps.close();
				rs.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public LinkedList<User> getAllUsers(Connection conn) {
		LinkedList<User> users = new LinkedList<>();

		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("select USERNAME, HASHEDPWD, REGISTERTIME from USERTABLE order by REGISTERTIME");
			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("USERNAME"));
				user.setHashedpwd(rs.getBytes("HASHEDPWD"));
				user.setRegistertime(rs.getTimestamp("REGISTERTIME"));
				users.add(user);
			}
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return users;
	}

}
