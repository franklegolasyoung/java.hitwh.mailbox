package franky.mail.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;

import franky.mail.box.*;
import franky.mail.mail.MailLoader;
import franky.mail.mail.MailSender;
import franky.mail.object.Mail;
import franky.mail.system.SystemHandler;
import franky.mail.system.SystemLoader;

/**
 * 主界面
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class MainFrame extends JFrame {

	private JLabel welcome = new JLabel("欢迎您：");
	//树与邮件信息
	private JSplitPane mailSplitPane;
	//列表与邮件
	private JSplitPane mailListInfoPane;
	//正文与附件
	private JSplitPane mailInfoPane;
	//邮件列表
	private MailListTable mailListTable;
	private JScrollPane tablePane;
	//导航树
	private JScrollPane treePane;
	private JTree tree;
	//邮件显示JTextArea
	private JTextArea mailTextArea = new JTextArea(10, 8);
	private JScrollPane mailScrollPane;
	//邮件附件列表
	private JScrollPane filePane;
	private JList fileList;
	//工具栏
	private JToolBar toolBar = new JToolBar();
	
	//各个箱的Mail
	private List<Mail> inMails;
	private List<Mail> outMails;
	private List<Mail> sentMails;
	private List<Mail> draftMails;
	//当前界面
	private List<Mail> currentMails;
	
	//写邮件的JFrame
	private MailFrame mailFrame;
	//配置界面
	private SetupFrame setupFrame;
	//邮箱加载对象
	private MailLoader mailLoader = new MailLoader();
	private SystemHandler systemHandler = new SystemHandler();
	private SystemLoader systemLoader = new SystemLoader();
	private MailSender mailSender = new MailSender();
	//当前打开的文件对象
	private Mail currentMail;
	
	//收取邮件
	private Action in = new AbstractAction("收取邮件", new ImageIcon("image/inbox.png")) {
		public void actionPerformed(ActionEvent e) {
			receive();
		}
	};
	
	//发送邮件
	private Action sent = new AbstractAction("发送邮件", new ImageIcon("image/send.png")) {
		public void actionPerformed(ActionEvent e) {
			send();
		}
	};
	
	//新邮件
	private Action write = new AbstractAction("新邮件", new ImageIcon("image/write.png")) {
		public void actionPerformed(ActionEvent e) {
			write();
		}
	};
	
	//回复邮件
	private Action reply = new AbstractAction("回复邮件", new ImageIcon("image/reply.png")) {
		public void actionPerformed(ActionEvent e) {
			reply();
		}
	};
	
	//转发邮件
	private Action retweet = new AbstractAction("转发邮件", new ImageIcon("image/retweet.png")) {
		public void actionPerformed(ActionEvent e) {
			retweet();
		}
	};
	
	//配置邮箱
	private Action settings = new AbstractAction("配置邮箱d", new ImageIcon("image/settings.png")) {
		public void actionPerformed(ActionEvent e) {
			settings();
		}
	};

	private MailContext ctx;
	
	public MainFrame(MailContext ctx) {
		this.ctx = ctx;
		this.mailFrame = new MailFrame(this);
		initMails();//初始化
		this.currentMails = this.inMails;//显示收件箱
		this.tree = createTree();
		//邮件列表
		DefaultTableModel tableMode = new DefaultTableModel();
		this.mailListTable = new MailListTable(tableMode);
		tableMode.setDataVector(createViewDatas(this.currentMails), getListColumn());
		setTableFace();
		this.tablePane = new JScrollPane(this.mailListTable);
		this.tablePane.setBackground(Color.WHITE);
		//附件列表
		this.fileList = new JList();
		this.fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.filePane = new JScrollPane(fileList);
		//正文
		this.mailTextArea.setLineWrap(true);
		this.mailTextArea.setEditable(false);
		this.mailTextArea.setFont(new Font(null, Font.BOLD, 14));
		this.mailScrollPane =  new JScrollPane(this.mailTextArea);
		//附件和正文
		this.mailInfoPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				this.filePane, this.mailScrollPane);
		this.mailInfoPane.setDividerSize(3);
		this.mailInfoPane.setDividerLocation(100);
		//列表和邮件
		this.mailListInfoPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				this.tablePane, mailInfoPane);
		this.mailListInfoPane.setDividerLocation(300);
		this.mailListInfoPane.setDividerSize(10);
		//导航树
		this.treePane = new JScrollPane(this.tree);
		this.tree.setRowHeight(45);
		this.tree.setFont(new Font(null, Font.PLAIN,16));
		//树和邮件信息
		this.mailSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				this.treePane, this.mailListInfoPane);
		this.mailSplitPane.setDividerLocation(120);
		this.mailSplitPane.setDividerSize(0);
		//工具栏
		this.welcome.setText(this.welcome.getText() + ctx.getUser());
		createToolBar();
		this.add(mailSplitPane);
		this.setTitle("电子邮件客户端");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(900,600);
		this.setLocation(200,100);
		initListeners();
	}
	
	public SystemHandler getSystemHandler() {
		return this.systemHandler;
	}
	
	public MailSender getMailSender() {
		return this.mailSender;
	}
	
	private void initListeners() {
		//列表选择监听器
		this.mailListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				//鼠标释放时执行
				if (!event.getValueIsAdjusting()) {
					if (mailListTable.getSelectedRowCount() != 1) return;
					viewMail();
				}
			}
		});
	}

	private void send() {
		Mail mail = getSelectMail();
		if (isReceive(mail)) {
			showMessage("收件箱的邮件不能发送");
			return;
		}
		this.mailFrame.sendInit(mail);
	}

	private void reply() {
		Mail mail = getSelectMail();
		if (!isReceive(mail)) {
			showMessage("只能回复收件箱中的邮件");
			return;
		}
		this.mailFrame.replyInit(mail);
	}
	
	//判断邮件是否在收件箱中
	private boolean isReceive(Mail mail) {
		for (Mail m : this.inMails) {
			if (m.getXmlName().equals(mail.getXmlName())) return true;
		}
		return false;
	}

	private void retweet() {
		Mail mail = getSelectMail();
		this.mailFrame.retweetInit(mail);
	}
	
	//获得所选行的xmlName
	private String getSelectXmlName() {
		int row = this.mailListTable.getSelectedRow();
		int column = this.mailListTable.getColumn("xmlName").getModelIndex();
		if (row == -1) return null;
		String xmlName = (String)this.mailListTable.getValueAt(row, column);
		return xmlName;
	}

	private void viewMail() {
		this.mailTextArea.setText("");
		Mail mail = getSelectMail();
		this.mailTextArea.append("发送人：  " + mail.getSender());
		this.mailTextArea.append("\n");
		this.mailTextArea.append("收件人:   " + mail.getReceiverString());
		this.mailTextArea.append("\n");
		this.mailTextArea.append("主题：  " + mail.getSubject());
		this.mailTextArea.append("\n");
		this.mailTextArea.append("接收日期：  " + dateFormat.format(mail.getReceiveDate()));
		this.mailTextArea.append("\n\n");
		this.mailTextArea.append("邮件正文：  ");
		this.mailTextArea.append("\n\n");
		this.mailTextArea.append(mail.getContent());
		this.fileList.setListData(mail.getFiles().toArray());
		this.currentMail = mail;
	}

	private Mail getSelectMail() {
		String xmlName = getSelectXmlName();
		return getMail(xmlName, this.currentMails);
	}

	//依据xmlName找Mail
	private Mail getMail(String xmlName, List<Mail> mails) {
		for (Mail m : mails) {
			if (m.getXmlName().equals(xmlName))return m;
		}
		return null;
	}

	private void initMails() {
		this.inMails = this.systemLoader.getInBoxMails(this.ctx);
		this.draftMails = this.systemLoader.getDraftBoxMails(this.ctx);
		this.outMails = this.systemLoader.getOutBoxMails(this.ctx);
		this.sentMails = this.systemLoader.getSentBoxMails(this.ctx);
	}
	
	//到服务器中收取邮件
	public void receive() {
		try {
			System.out.println("开始收取邮件");
			List<Mail> newMails = this.mailLoader.getMessages(this.ctx);
			//得到Mail对象, 添加到inMails
			this.inMails.addAll(0, newMails);
			//保存到本地的收件箱中
			saveToInBox(newMails);
			refreshTable();
		} catch (Exception e) {
			e.printStackTrace();
			showMessage(e.getMessage());
		}
	}
	
	private int showMessage(String s) {
		return JOptionPane.showConfirmDialog(this, s, "提示",
				JOptionPane.OK_CANCEL_OPTION);
	}
	
	private void saveToInBox(List<Mail> newMails) {
		for (Mail mail : newMails) {
			systemHandler.saveInBox(mail, this.ctx);
		}
	}

	public void addSentMail(Mail mail) {
		this.sentMails.add(0, mail);
		refreshTable();
	}

	public void addOutMail(Mail mail) {
		this.outMails.add(0, mail);
		refreshTable();
	}
	
	public void addDraftMail(Mail mail) {
		this.draftMails.add(0, mail);
		refreshTable();
	}
	
	//刷新列表
	public void refreshTable() {
		DefaultTableModel tableModel = (DefaultTableModel)this.mailListTable.getModel();
		tableModel.setDataVector(createViewDatas(this.currentMails), getListColumn());
		setTableFace();
	}
	
	//设置界面
	private void settings() {
		if (this.setupFrame == null) {
			this.setupFrame = new SetupFrame(this);
		}
		this.setupFrame.setVisible(true);
	}

	public void setMailContext(MailContext ctx) {
		this.ctx = ctx;
	}
	
	public MailContext getMailContext() {
		return this.ctx;
	}
	
	public MailLoader getMailLoader() {
		return mailLoader;
	}

	public void setMailLoader(MailLoader mailLoader) {
		this.mailLoader = mailLoader;
	}

	private void write() {
		this.mailFrame.setVisible(true);
	}

	//时间格式
	private DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	
	//将邮件数据集合转换成视图的格式
	private Vector<Vector> createViewDatas(List<Mail> mails) {
		Vector<Vector> views = new Vector<Vector>();
		for (Mail mail : mails) {
			Vector view = new Vector();
			view.add(mail.getXmlName());
			view.add(new ImageIcon("image/mail.png"));
			view.add(mail.getSender());
			view.add(mail.getSubject());
			view.add(formatDate(mail.getReceiveDate()));
			view.add(mail.getSize() + "k");
			views.add(view);
		}
		return views;
	}

	private String formatDate(Date date) {
		return dateFormat.format(date);
	}
	
	//获得邮件列表的列名
	private Vector getListColumn() {
		Vector columns = new Vector();
		columns.add("xmlName");
		columns.add("");
		columns.add("发件人");
		columns.add("主题");
		columns.add("日期");
		columns.add("大小");
		return columns;
	}
	
	//设置邮件列表的样式
	private void setTableFace() {
		//隐藏邮件对应的xml文件的名字
		this.mailListTable.getColumn("xmlName").setMinWidth(0);
		this.mailListTable.getColumn("xmlName").setMaxWidth(0);
		this.mailListTable.getColumn("").setCellRenderer(new MailTableCellRenderer());
		this.mailListTable.getColumn("").setMaxWidth(45);
		this.mailListTable.getColumn("").setMinWidth(45);
		this.mailListTable.getColumn("发件人").setMinWidth(200);
		this.mailListTable.getColumn("主题").setMinWidth(200);
		this.mailListTable.getColumn("日期").setMinWidth(130);
		this.mailListTable.getColumn("大小").setMinWidth(80);
		this.mailListTable.setRowHeight(30);
	}
	
	//初始化工具栏
	private void createToolBar() {
		this.toolBar.add(this.in).setToolTipText("收取邮件");
		this.toolBar.add(this.sent).setToolTipText("发送邮件");
		this.toolBar.add(this.write).setToolTipText("写邮件");
		this.toolBar.addSeparator();
		this.toolBar.add(this.reply).setToolTipText("回复邮件");
		this.toolBar.add(this.retweet).setToolTipText("转发邮件");
		this.toolBar.addSeparator();
		this.toolBar.add(this.settings).setToolTipText("设置");
		this.toolBar.addSeparator(new Dimension(450, 0));
		this.toolBar.add(this.welcome);
		this.toolBar.setFloatable(false);//设置工具栏不可移动
		this.toolBar.setMargin(new Insets(5, 10, 5, 10));//设置工具栏的边距
		this.add(this.toolBar, BorderLayout.NORTH);
	}
	
	//创建导航树
	private JTree createTree() {
		//创建节点
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		root.add(new DefaultMutableTreeNode(new inBox()));
		root.add(new DefaultMutableTreeNode(new outBox()));
		root.add(new DefaultMutableTreeNode(new sentBox()));
		root.add(new DefaultMutableTreeNode(new draftBox()));
		//创建树
		JTree tree = new JTree(root);
		tree.addMouseListener(new SailTreeListener(this));
		//隐藏根节点
		tree.setRootVisible(false);
		//设置节点处理类
		SailTreeCellRenderer cellRenderer = new SailTreeCellRenderer();
		tree.setCellRenderer(cellRenderer);
		return tree;
	}

	private Object[] emptyListData = new Object[]{};
	
	public void select() {
		MailBox box = getSelectBox();
		if (box instanceof inBox) {
			this.currentMails = this.inMails;
		} else if (box instanceof outBox) {
			this.currentMails = this.outMails;
		} else if (box instanceof sentBox) {
			this.currentMails = this.sentMails;
		} else if (box instanceof draftBox) {
			this.currentMails = this.draftMails;
		}
		//刷新列表并清空
		refreshTable();
		cleanMailInfo();
	}
	
	//清空当前
	public void cleanMailInfo() {
		this.currentMail = null;
		this.mailTextArea.setText("");
		this.fileList.setListData(this.emptyListData);
	}
	
	//获得选中的box
	private MailBox getSelectBox() {
		TreePath treePath = this.tree.getSelectionPath();
		if (treePath == null) return null;
		//获得选中的TreeNode
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();
		return (MailBox)node.getUserObject();
	}
}
