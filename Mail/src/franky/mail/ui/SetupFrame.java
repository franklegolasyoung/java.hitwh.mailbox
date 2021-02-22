package franky.mail.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import franky.mail.exception.ValidateException;
import franky.mail.util.FileUtil;
import franky.mail.util.PropertiesUtil;

/**
 * 邮箱配置界面
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class SetupFrame extends JFrame {

	private JLabel accountLabel = new JLabel("邮箱地址：");
	private JLabel passwordLabel = new JLabel("邮箱密码：");
	private JTextField accountField = new JTextField(20);
	private JPasswordField passwordField = new JPasswordField(20);
	private JButton confirmButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");
	//Box
	private Box accountBox = Box.createHorizontalBox();
	private Box passwordBox = Box.createHorizontalBox();
	private Box smtpBox = Box.createHorizontalBox();
	private Box pop3Box = Box.createHorizontalBox();
	private Box buttonBox = Box.createHorizontalBox();
	private Box main = Box.createVerticalBox();
	private MainFrame mainFrame;
	//smtp
	private JLabel smtpLabel = new JLabel("发送邮件服务器（SMTP）：");
	private JTextField smtpField = new JTextField(10);
	private JLabel smtpPortLabel = new JLabel("端口：");
	private JTextField smtpPortField = new JTextField(5);
	//pop
	private JLabel pop3Label = new JLabel("接收邮件服务器（POP3）：");
	private JTextField pop3Field = new JTextField(10);
	private JLabel pop3PortLabel = new JLabel("端口：");
	private JTextField pop3PortField = new JTextField(5);
	
	public SetupFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		//初始化界面组件
		initFrame(this.mainFrame.getMailContext());
		//初始化监听器
		initListener();
	}
	
	//初始化监听器
	private void initListener() {
		this.confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				confirm();
			}
		});
		this.cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hideFrame();
			}
		});
	}

	private void hideFrame() {
		this.setVisible(false);
	}

	private String getPassword() {
		char[] passes = this.passwordField.getPassword();
		StringBuffer password = new StringBuffer();
		for (char c : passes) {
			password.append(c);
		}
		return password.toString();
	}

	private void confirm() {
		try {
			//重新设置系统上下文的信息
			MailContext ctx = getMailContext(this.mainFrame.getMailContext());
			ctx.setReset(true);
			PropertiesUtil.store(ctx);
			//设置主界面的MailContext对象
			this.mainFrame.setMailContext(ctx);
			//创建存放的目录
			FileUtil.createFolder(ctx);
			this.setVisible(false);
		} catch (Exception e) {
			JOptionPane.showConfirmDialog(this, e.getMessage(), "警告", 
					JOptionPane.OK_CANCEL_OPTION);
			return;
		}
	}
	
	//根据界面的值封装MailContext
	private MailContext getMailContext(MailContext ctx) {
		String account = this.accountField.getText();
		String password = getPassword();
		String smtpHost = this.smtpField.getText();
		String smtpPortStr = this.smtpPortField.getText();
		String pop3Host = this.pop3Field.getText();
		String pop3PortStr = this.pop3PortField.getText();
		String[] values = new String[]{account, password, smtpHost, smtpPortStr, 
				pop3Host, pop3Host, pop3PortStr};
		validateRequire(values);
		Integer smtpPort = Integer.valueOf(smtpPortStr);
		Integer pop3Port = Integer.valueOf(pop3PortStr);
		ctx.setAccount(account);
		ctx.setPassword(password);
		ctx.setSmtpHost(smtpHost);
		ctx.setSmtpPort(smtpPort);
		ctx.setPop3Host(pop3Host);
		ctx.setPop3Port(pop3Port);
		//重新设置连接信息
		ctx.setReset(true);
		return ctx;
	}
	
	//验证
	private void validateRequire(String[] values) {
		for (String s :values) {
			if (s.trim().equals("")) {
				throw new ValidateException("请输入完整的信息");
			}
		}
	}
	
	//初始化
	private void initFrame(MailContext ctx) {

		this.accountBox.add(Box.createHorizontalStrut(30));
		this.accountBox.add(this.accountLabel);
		this.accountBox.add(Box.createHorizontalStrut(13));
		this.accountBox.add(this.accountField);
		this.accountBox.add(Box.createHorizontalStrut(30));

		this.passwordBox.add(Box.createHorizontalStrut(30));
		this.passwordBox.add(this.passwordLabel);
		this.passwordBox.add(Box.createHorizontalStrut(13));
		this.passwordBox.add(this.passwordField);
		this.passwordBox.add(Box.createHorizontalStrut(30));

		this.smtpBox.add(Box.createHorizontalStrut(30));
		this.smtpBox.add(this.smtpLabel);
		this.smtpBox.add(this.smtpField);
		this.smtpBox.add(Box.createHorizontalStrut(5));
		this.smtpBox.add(this.smtpPortLabel);
		this.smtpBox.add(this.smtpPortField);
		this.smtpBox.add(Box.createHorizontalStrut(30));

		this.pop3Box.add(Box.createHorizontalStrut(31));
		this.pop3Box.add(this.pop3Label);
		this.pop3Box.add(this.pop3Field);
		this.pop3Box.add(Box.createHorizontalStrut(5));
		this.pop3Box.add(this.pop3PortLabel);
		this.pop3Box.add(this.pop3PortField);
		this.pop3Box.add(Box.createHorizontalStrut(30));

		this.buttonBox.add(Box.createHorizontalStrut(30));
		this.buttonBox.add(this.confirmButton);
		this.buttonBox.add(Box.createHorizontalStrut(20));
		this.buttonBox.add(this.cancelButton);
		this.buttonBox.add(Box.createHorizontalStrut(30));

		this.main.add(Box.createVerticalStrut(20));
		this.main.add(this.accountBox);
		this.main.add(Box.createVerticalStrut(10));
		this.main.add(this.passwordBox);
		this.main.add(Box.createVerticalStrut(10));
		this.main.add(this.smtpBox);
		this.main.add(Box.createVerticalStrut(10));
		this.main.add(this.pop3Box);
		this.main.add(Box.createVerticalStrut(10));
		this.main.add(this.buttonBox);
		this.main.add(Box.createVerticalStrut(20));

		this.accountField.setText(ctx.getAccount());
		this.passwordField.setText(ctx.getPassword());
		this.smtpField.setText(ctx.getSmtpHost());
		this.pop3Field.setText(ctx.getPop3Host());
		this.smtpPortField.setText(String.valueOf(ctx.getSmtpPort()));
		this.pop3PortField.setText(String.valueOf(ctx.getPop3Port()));

		this.setTitle("设置");
		this.setLocation(300, 200); 
		this.setResizable(false);
		this.add(this.main);
		getRootPane().setDefaultButton(confirmButton);//回车按下按钮
		this.pack();
	}
	
}
