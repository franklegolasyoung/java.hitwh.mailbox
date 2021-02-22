package franky.mail.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.mail.*;
import javax.mail.internet.MimeUtility;

import franky.mail.exception.LoadMailException;
import franky.mail.ui.MailContext;
import franky.mail.object.FileObject;
import franky.mail.object.Mail;
import franky.mail.util.FileUtil;

/**
 * 读取邮件实现类
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class MailLoader {

	public List<Mail> getMessages(MailContext ctx) {
		//得到INBOX对应的Folder
		Folder inbox = getINBOXFolder(ctx);
		try {
			inbox.open(Folder.READ_WRITE);
			//得到INBOX里的所有Message
			Message[] messages = inbox.getMessages();
			//将Message封装成Mail集合
			List<Mail> result = getMailList(ctx, messages);
			return result;
		} catch (Exception e) {
			throw new LoadMailException(e.getMessage());
		}
	}

	//将Message对象转换成Mail对象
	private List<Mail> getMailList(MailContext ctx, Message[] messages) {
		List<Mail> result = new ArrayList<Mail>();
		try{
			for (Message ms : messages) {
				String xmlName = UUID.randomUUID().toString() + ".xml";
				String content = getContent(ms, new StringBuffer()).toString();
				//得到邮件的各个值
				Mail mail = new Mail(xmlName, getAllRecipients(ms), getSender(ms),
						ms.getSubject(), getReceivedDate(ms), Mail.getSize(ms.getSize()), hasRead(ms),
						content, FileUtil.INBOX);
				mail.setFiles(getFiles(ctx, ms));//附件集合
				result.add(mail);
			}
			return result;
		} catch (Exception e) {
			throw new LoadMailException("得到邮件的信息出错: " + e.getMessage());
		}
	}
	
	//得到日期
	private Date getReceivedDate(Message m) throws Exception {
		if (m.getSentDate() != null) return m.getSentDate();
		if (m.getReceivedDate() != null) return m.getReceivedDate();
		return new Date();
	}
	
	//邮件附件
	private List<FileObject> getFiles(MailContext ctx, Message m) throws Exception {
		List<FileObject> files = new ArrayList<FileObject>();
		//处理混合类型
		if (m.isMimeType("multipart/mixed")) {
			Multipart mp = (Multipart)m.getContent();
			//得到邮件内容的Multipart对象并得到内容中Part的数量
			int count = mp.getCount();
			for (int i = 1; i < count; i++) {
				Part part = mp.getBodyPart(i);
				//在本地创建文件并添加到结果中
				files.add(FileUtil.createFileFromPart(ctx, part));
			}
		}
		return files;
	}
	
	
	//返回邮件正文
	private StringBuffer getContent(Part part, StringBuffer result) throws Exception {
		if (part.isMimeType("multipart/*")) {
			Multipart p = (Multipart)part.getContent();
			int count = p.getCount();
			//Multipart的第一部分是text/plain, 第二部分是text/html的格式, 只需要解析第一部分即可
			if (count > 1) count = 1; 
			for(int i = 0; i < count; i++) {
				BodyPart bp = p.getBodyPart(i);
				//递归调用
				getContent(bp, result);
			}
		} else if (part.isMimeType("text/*")) {
			//遇到文本格式或者html格式, 直接得到内容
			result.append(part.getContent());
		}
		return result;
	}	
	
	//判断一封邮件是否已读, true表示已读取, false表示没有读取
	private boolean hasRead(Message m) throws Exception {
		Flags flags = m.getFlags();
		if (flags.contains(Flags.Flag.SEEN)) return true;
		return false;
	}
	
	//得到所有收件人
	private List<String> getAllRecipients(Message m) throws Exception {
		Address[] addresses = m.getAllRecipients();
		return getAddresses(addresses);
	}
	
	//将参数的地址字符串封装
	private List<String> getAddresses(Address[] addresses) {
		List<String> result = new ArrayList<String>();
		if (addresses == null) return result;
		for (Address a : addresses) {
			result.add(a.toString());
		}
		return result;
	}
	
	//得到发送人的地址
	private String getSender(Message m) throws Exception  {
		Address[] addresses = m.getFrom();
		return MimeUtility.decodeText(addresses[0].toString());
	}
	
	
	//得到邮箱INBOX
	private Folder getINBOXFolder(MailContext ctx) {
		Store store = ctx.getStore();
		try {
			return store.getFolder("INBOX");
		} catch (Exception e) {
			throw new LoadMailException("加载邮箱错误，请检查配置");
		}
	}

}
