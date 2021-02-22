package franky.mail.object;

import java.util.Date;
import java.util.List;

/**
 * 邮件对象
 *
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class Mail {

    private String xmlName;
    //发送人
    private String sender;
    //收件人
    private List<String> receivers;
    //邮件标题
    private String subject;
    //日期
    private Date receiveDate;
    //邮件大小
    private String size;
    //是否已经被阅读
    private boolean hasRead;
    //邮件正文
    private String content;
    //附件
    private List<FileObject> files;
    //邮件来源
    private String from;

    public Mail(String xmlName, List<String> receivers, String sender, String subject, Date receiveDate, String size,
                boolean hasRead, String content, String from) {
        this.xmlName = xmlName;
        this.receivers = receivers;
        this.sender = sender;
        this.subject = subject;
        this.receiveDate = receiveDate;
        this.size = size;
        this.hasRead = hasRead;
        this.content = content;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public String getXmlName() {
        return xmlName;
    }

    public void setXmlName(String xmlName) {
        this.xmlName = xmlName;
    }

    public boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<FileObject> getFiles() {
        return files;
    }

    public void setFiles(List<FileObject> files) {
        this.files = files;
    }

    private String handleString(StringBuffer sb) {
        String result = sb.toString();
        if ("".equals(result)) return result;
        return result.substring(0, result.lastIndexOf(", "));
    }

    public String getReceiverString() {
        StringBuffer sb = new StringBuffer();
        for (String rec : this.receivers) {
            sb.append(rec + ", ");
        }
        return handleString(sb);
    }

    //参数单位为byte
    public static String getSize(int size) {
        double d = Double.valueOf(size);
        double result = d / 1024;
        return (new java.text.DecimalFormat("#.##")).format(result);
    }
}
