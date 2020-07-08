package edu.cauc.message;

public class FileMessage extends Message {

  private byte[] fileContent;

  public FileMessage(byte[] fileContent) {
    this.fileContent = fileContent;
  }

  public byte[] getFileContent() {
    return fileContent;
  }

  public void setFileContent(byte[] fileContent) {
    this.fileContent = fileContent;
  }
}
