package franky.mail.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import franky.mail.exception.LoginException;
import franky.mail.util.FileUtil;
import franky.mail.util.PropertiesUtil;

/**
 * 登录界面
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class LoginUI extends JFrame {

	//登录界面
	private JLabel userL = new JLabel("用户名：");
	private JLabel pd = new JLabel("通用密钥：");
	private JTextField userTd = new JTextField(15);
	private JPasswordField userPd = new JPasswordField(15);
	private JButton confirmBtn = new JButton("确认");
	private JButton cancelBtn = new JButton("取消");
	//Box
	private Box btnBox = Box.createHorizontalBox();
	private Box labelBox = Box.createVerticalBox();
	private Box pdBox = Box.createVerticalBox();
	private Box userBox = Box.createHorizontalBox();
	private Box mainBox = Box.createVerticalBox();
	//主界面
	private MainFrame mainFrame;

	public LoginUI() {

		//this.labelBox.add(Box.createVerticalStrut(10));
		this.labelBox.add(userL);
		this.labelBox.add(Box.createVerticalStrut(20));
		this.labelBox.add(pd);

		//this.pdBox.add(Box.createVerticalStrut(10));
		this.pdBox.add(userTd);
		this.pdBox.add(Box.createVerticalStrut(10));
		this.pdBox.add(userPd);

		this.userBox.add(Box.createHorizontalStrut(20));
		this.userBox.add(labelBox);
		this.userBox.add(Box.createHorizontalStrut(10));
		this.userBox.add(pdBox);
		this.userBox.add(Box.createHorizontalStrut(20));

		this.btnBox.add(Box.createHorizontalStrut(30));
		this.btnBox.add(this.confirmBtn);
		this.btnBox.add(Box.createHorizontalStrut(50));
		this.btnBox.add(this.cancelBtn);
		this.btnBox.add(Box.createHorizontalStrut(30));

		this.mainBox.add(this.mainBox.createVerticalStrut(20));
		this.mainBox.add(this.userBox);
		this.mainBox.add(this.mainBox.createVerticalStrut(10));
		this.mainBox.add(this.btnBox);
		this.mainBox.add(this.mainBox.createVerticalStrut(10));
		this.add(mainBox);
		this.setLocation(500, 300);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("电子邮件客户端 By FrankY");
		initListener();
		getRootPane().setDefaultButton(confirmBtn);//回车按下按钮
	}

	private void initListener() {
		this.confirmBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				confirm();
			}
		});
		this.cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
	}

	private void confirm() {
		String user = this.userTd.getText();
		char[] pass = userPd.getPassword();
		String passwd = new String(pass);
		if (user.trim().equals("")) {
			JOptionPane.showConfirmDialog(this, "请输入用户名", "提示",
					JOptionPane.OK_CANCEL_OPTION);
			return;
		}
		if (!(passwd.trim().equals("1"))) {
			JOptionPane.showConfirmDialog(this, "密钥错误", "警告",
					JOptionPane.OK_CANCEL_OPTION);
			return;
		}
		//JOptionPane.showConfirmDialog(this, "登录成功", "成功",JOptionPane.OK_CANCEL_OPTION);

		//用户名对应的目录
		File folder = new File(FileUtil.DATE_FOLDER + user);
		//第一次创建目录
		if (!folder.exists()) {
			folder.mkdir();
		}
		//配置文件
		File config = new File(folder.getAbsolutePath() + FileUtil.CONFIG_FILE);
		try {
			//创建配置文件
			if (!config.exists()) {
				config.createNewFile();
			}
			//转换为MailContext
			MailContext ctx = PropertiesUtil.createContext(config);
			ctx.setUser(user);
			//创建系统界面主对象
			this.mainFrame = new MainFrame(ctx);
			this.mainFrame.setVisible(true);
			this.setVisible(false);
		} catch (IOException e) {
			e.printStackTrace();
			throw new LoginException("配置文件错误");
		}
	}
}
