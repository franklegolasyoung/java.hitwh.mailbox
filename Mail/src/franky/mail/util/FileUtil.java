package franky.mail.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.mail.Part;
import javax.mail.internet.MimeUtility;

import franky.mail.ui.MailContext;
import franky.mail.exception.FileException;
import franky.mail.object.FileObject;
import franky.mail.object.Mail;

import com.thoughtworks.xstream.XStream;

/**
 * 文件工具类
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class FileUtil {

	//所有用户
	public static final String DATE_FOLDER = "datas" + File.separator;
	//properties配置
	public static final String CONFIG_FILE = File.separator + "mail.properties";

	public static final String INBOX = "inbox";
	public static final String OUTBOX = "outbox";
	public static final String SENT = "sent";
	public static final String DRAFT = "draft";
	public static final String FILE = "file";
	
	/**
	 * 创建用户的帐号目录和相关的子目录
	 */
	public static void createFolder(MailContext ctx) {
		String accountRoot = getAccountRoot(ctx);
		//邮箱目录
		mkdir(new File(accountRoot));
		mkdir(new File(accountRoot + INBOX));
		mkdir(new File(accountRoot + OUTBOX));
		mkdir(new File(accountRoot + SENT));
		mkdir(new File(accountRoot + DRAFT));
		mkdir(new File(accountRoot + FILE));
	}

	//创建目录的工具方法, 判断目录是否存在
	private static void mkdir(File file) {
		if (!file.exists()) file.mkdir();
	}

	//得到邮件帐号的根目录
	private static String getAccountRoot(MailContext ctx) {
		String accountRoot = DATE_FOLDER + ctx.getUser() + 
		File.separator + ctx.getAccount() + File.separator;
		return accountRoot;
	}
	
	//得到目录名
	public static String getBoxPath(MailContext ctx, String folderName) {
		return getAccountRoot(ctx) + folderName + File.separator;
	}
	
	//附件存到file
	public static FileObject createFileFromPart(MailContext ctx, Part part) {
		try {
			String fileRepository = getBoxPath(ctx, FILE);
			String serverFileName = MimeUtility.decodeText(part.getFileName());
			//生成UUID
			String fileName = UUID.randomUUID().toString();
			File file = new File(fileRepository + fileName + 
					getFileSufix(serverFileName));
			//读写文件
			FileOutputStream fos = new FileOutputStream(file);
			InputStream is = part.getInputStream();
			BufferedOutputStream outs = new BufferedOutputStream(fos);
			//如果附件内容为空，part.getSize为-1，直接new byte会抛出异常
			int size = (part.getSize() > 0) ? part.getSize() : 0;
			byte[] b = new byte[size];
			is.read(b);
			outs.write(b);
			outs.close();
			is.close();
			fos.close();
			//封装
			FileObject fileObject = new FileObject(serverFileName, file);
			return fileObject;
		} catch (Exception e) {
			e.printStackTrace();
			throw new FileException(e.getMessage());
		}
	}
	
	//从相应的box中得到xml文件
	public static List<File> getXMLFiles(MailContext ctx, String box) {
		String rootPath = getAccountRoot(ctx);
		String boxPath = rootPath + box;
		//得到box目录
		File boxFolder = new File(boxPath);
		List<File> files = addFiles(boxFolder);
		return files;
	}

	private static List<File> addFiles(File folder) {
		List<File> result = new ArrayList<File>();
		File[] files = folder.listFiles();
		if (files == null) return new ArrayList<File>();
		for (File f : files) {
			result.add(f);
		}
		return result;
	}

	//得到文件名的后缀
	public static String getFileSufix(String fileName) {
		if (fileName == null || fileName.trim().equals("")) return "";
		if (fileName.indexOf(".") != -1) {
			return fileName.substring(fileName.indexOf("."));
		}
		return "";
	}
	
	//创建XStream对象
	private static XStream xstream = new XStream();
	
	//邮件对象写入xml
	public static void writeToXML(MailContext ctx, Mail mail, String boxFolder) {
		String xmlName = mail.getXmlName();
		String boxPath = getAccountRoot(ctx) + boxFolder + File.separator;
		File xmlFile = new File(boxPath + xmlName);
		writeToXML(xmlFile, mail);
	}
	public static void writeToXML(File xmlFile, Mail mail) {
		try {
			if (!xmlFile.exists()) xmlFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(xmlFile);
			OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF8");
			xstream.toXML(mail, writer);
			writer.close();
			fos.close();
		} catch (Exception e) {
			throw new FileException("写入文件异常: " + xmlFile.getAbsolutePath());
		}
	}
	
	//将一份xml文档转换成Mail对象
	public static Mail fromXML(MailContext ctx, File xmlFile) {
		try {
			FileInputStream fis = new FileInputStream(xmlFile);
			xstream.ignoreUnknownElements();
			Mail mail = (Mail)xstream.fromXML(fis);
			fis.close();
			return mail;
		} catch (Exception e) {
			throw new FileException("转换数据异常: " + xmlFile.getAbsolutePath());
		}
	}

	//复制文件的方法
	public static void copy(File sourceFile, File targetFile) {
		try {
			Process process = Runtime.getRuntime().exec("cp " +
					sourceFile.getAbsolutePath() + "\" \"" + 
					targetFile.getAbsolutePath() + "\"");
			process.waitFor();
		} catch (Exception e) {
			throw new FileException("另存文件错误: " + targetFile.getAbsolutePath());
		}
	}

}
