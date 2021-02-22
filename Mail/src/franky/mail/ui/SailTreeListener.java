package franky.mail.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 导航树监听器
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class SailTreeListener extends MouseAdapter {
	private MainFrame mainFrame;
	public SailTreeListener(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	public void mousePressed(MouseEvent e) {
		mainFrame.select();
	}
}
