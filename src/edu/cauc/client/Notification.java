package edu.cauc.client;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * 客户端除 用户上下线、聊天消息 等显示在 messageWindow 上外，其他消息全部采用弹窗的形式。 服务器所有消息均输出在
 * messageWindow 上。
 */
public class Notification {

    private String title;
    private String contentText;
    private Alert.AlertType alertType;

    public Notification(int type, String contentText) {
        if (type == 1) { // 1 代表确认对话框
            alertType = Alert.AlertType.CONFIRMATION;
            title = "提示";
        } else if (type == 2) { // 2 代表错误弹窗
            alertType = Alert.AlertType.ERROR;
            title = "啊嘞？出错了";
        } else if (type == 3) { // 3 代表消息弹窗
            alertType = Alert.AlertType.INFORMATION;
            title = "提示消息";
        }
        this.contentText = contentText;
    }

    public boolean show() {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);

        if (alertType == Alert.AlertType.CONFIRMATION) {
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) // 如果用户选择了 OK
                return true;
            else
                return false;
        } else {
            alert.showAndWait();
            return true;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

}
