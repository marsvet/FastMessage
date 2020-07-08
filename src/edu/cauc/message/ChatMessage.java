package edu.cauc.message;

public class ChatMessage extends Message {
    private String msgContent;

    public ChatMessage(String srcUser, String dstUser, String msgContent) {
        super(srcUser, dstUser);
        this.msgContent = msgContent;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public boolean isPubChatMessage() {
        if (getDstUser() == null)
            return true;
        else
            return false;
    }

}
