package franky.mail.ui;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 邮件列表的单元格渲染对象
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class MailTableCellRenderer extends DefaultTableCellRenderer {
	public Component getTableCellRendererComponent(JTable arg0, Object value,
			boolean arg2, boolean arg3, int arg4, int arg5) {
		//判断该单元格的值是否图片
		if (value instanceof Icon) {
			this.setIcon((Icon)value);
		} else {
			this.setText(value.toString());
		}
		return this;
	}
}
