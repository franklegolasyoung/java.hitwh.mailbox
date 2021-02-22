package franky.mail.system;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import franky.mail.object.Mail;
import franky.mail.ui.MailContext;
import franky.mail.util.FileUtil;

/**
 * 本地系统邮件加载实现类
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class SystemLoader {

	public List<Mail> getInBoxMails(MailContext ctx) {
		return getMails(ctx, FileUtil.INBOX);
	}

	public List<Mail> getOutBoxMails(MailContext ctx) {
		return getMails(ctx, FileUtil.OUTBOX);
	}

	public List<Mail> getSentBoxMails(MailContext ctx) {
		return getMails(ctx, FileUtil.SENT);
	}

	public List<Mail> getDraftBoxMails(MailContext ctx) {
		return getMails(ctx, FileUtil.DRAFT);
	}

	//将xml文件转换成Mail对象
	private List<Mail> convert(List<File> xmlFiles, MailContext ctx) {
		List<Mail> result = new ArrayList<Mail>();
		for (File file : xmlFiles) {
			//将xml转换成Mail对象
			Mail mail = FileUtil.fromXML(ctx, file);
			result.add(mail);
		}
		return result;
	}

	private List<Mail> getMails(MailContext ctx, String box) {
		List<File> xmlFiles = FileUtil.getXMLFiles(ctx, box);
		List<Mail> result = convert(xmlFiles, ctx);
		return result;
	}
}
