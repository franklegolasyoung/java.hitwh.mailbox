package franky.mail.mail;

import java.util.Date;
import java.util.List;

import javax.mail.*;
import javax.mail.internet.*;

import franky.mail.ui.MailContext;
import franky.mail.exception.SendMailException;
import franky.mail.object.FileObject;
import franky.mail.object.Mail;

/**
 * 邮件发送实现类
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class MailSender {

	public Mail send(Mail mail, MailContext ctx) {
		try {
			Session session = ctx.getSession();
			Message mailMessage = new MimeMessage(session);
			//设置发件人地址
			Address from = new InternetAddress(ctx.getUser() + " <" + ctx.getAccount() + ">");
			mailMessage.setFrom(from);
			//设置所有收件人的地址
			Address[] to = getAddress(mail.getReceivers());
			mailMessage.setRecipients(Message.RecipientType.TO, to);
			//设置主题
			mailMessage.setSubject(mail.getSubject());
			//发送日期
			mailMessage.setSentDate(new Date());
			//构建整封邮件的容器
			Multipart main = new MimeMultipart();
			//正文的body
			BodyPart body = new MimeBodyPart();
			body.setContent(mail.getContent(), "text/html; charset=utf-8");
			main.addBodyPart(body);
			//处理附件
			for (FileObject f : mail.getFiles()) {
				//每个附件的body
				MimeBodyPart fileBody = new MimeBodyPart();
				fileBody.attachFile(f.getFile());
				//为文件名进行转码
				fileBody.setFileName(MimeUtility.encodeText(f.getSourceName()));
				main.addBodyPart(fileBody);
			}
			//将正文的Multipart对象设入Message中
			mailMessage.setContent(main);
			Transport.send(mailMessage);
			return mail;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SendMailException("发送邮件错误, 请检查邮箱配置及邮件的相关信息");
		}
	}

	//获得收件人地址
	private Address[] getAddress(List<String> addList) throws Exception {
		Address[] result = new Address[addList.size()];
		for (int i = 0; i < addList.size(); i++) {
			if (addList.get(i) == null || "".equals(addList.get(i))) {
				continue;
			}
			result[i] = new InternetAddress(addList.get(i));
		}
		return result;
	}
}
