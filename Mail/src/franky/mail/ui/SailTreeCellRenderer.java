package franky.mail.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.*;

import franky.mail.box.MailBox;

/**
 * 继承DefaultTreeCellRenderer，实现每个节点都有不同的图标
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class SailTreeCellRenderer extends DefaultTreeCellRenderer {

	//树节点被选中时的字体
	private Font selectFont;
	
	public SailTreeCellRenderer() {
		this.selectFont = new Font(null, Font.BOLD, 16);
	}
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		MailBox box = (MailBox)node.getUserObject();
		this.setText(box.getText());
		//判断是否选中
		if (isSelected(node, tree)) {
			this.setFont(this.selectFont);
		} else {
			this.setFont(null);
		}
		this.setIcon(box.getImageIcon());
		return this;
	}
	
	//判断node是否被选中
	private boolean isSelected(DefaultMutableTreeNode node, JTree tree) {
		TreePath treePath = tree.getSelectionPath();
		if (treePath == null) return false;
		DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
		if (node.equals(selectNode)) {
			return true;
		}
		return false;
	}
	
}
