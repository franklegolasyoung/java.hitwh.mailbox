package franky.mail.system;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import franky.mail.object.FileObject;
import franky.mail.object.Mail;
import franky.mail.ui.MailContext;
import franky.mail.util.FileUtil;

/**
 * 本地系统邮件处理类
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class SystemHandler {

	//保存Mail对象到草稿箱
	public void saveDraftBox(Mail mail, MailContext ctx) {
		//保存Mail的附件
		saveFiles(mail, ctx);
		FileUtil.writeToXML(ctx, mail, FileUtil.DRAFT);
	}

	//保存Mail对象到收件箱
	public void saveInBox(Mail mail, MailContext ctx) {
		FileUtil.writeToXML(ctx, mail, FileUtil.INBOX);
	}

	//保存Mail对象到发件箱
	public void saveOutBox(Mail mail, MailContext ctx) {
		//保存Mail的附件
		saveFiles(mail, ctx);
		FileUtil.writeToXML(ctx, mail, FileUtil.OUTBOX);
	}

	//保存到发送成功
	public void saveSent(Mail mail, MailContext ctx) {
		saveFiles(mail, ctx);//保存附件
		FileUtil.writeToXML(ctx, mail, FileUtil.SENT);//为Mail对象生成xml文件
	}
	
	//保存Mail对象中的附件
	private void saveFiles(Mail mail, MailContext ctx) {
		List<FileObject> files = mail.getFiles();
		List<FileObject> newFiles = new ArrayList<FileObject>();
		int byteSize = mail.getContent().getBytes().length;
		for (FileObject f : files) {
			String sentBoxPath = FileUtil.getBoxPath(ctx, FileUtil.FILE);
			String fileName = UUID.randomUUID().toString();
			//get文件的后缀
			String sufix = FileUtil.getFileSufix(f.getFile().getName());
			File targetFile = new File(sentBoxPath + fileName + sufix);
			//复制到file
			FileUtil.copy(f.getFile(), targetFile);
			//设置Mail对象中附件集合的文件对象为新的文件对象(在file目录中)
			newFiles.add(new FileObject(f.getSourceName(), targetFile));
			byteSize += targetFile.length();
		}
		mail.setSize(Mail.getSize(byteSize));
		mail.setFiles(newFiles);
	}
}
