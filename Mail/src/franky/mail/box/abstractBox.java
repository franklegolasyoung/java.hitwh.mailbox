package franky.mail.box;

import javax.swing.*;

/**
 * @Author FrankY
 * @Date 2019-12-16 23:53
 * @Contact frank1045325433@outlook.com
 */
public abstract class abstractBox implements MailBox {

    private ImageIcon icon;//所对应的图标

    public ImageIcon getImageIcon(String imagePath) {
        if (this.icon == null) {
            this.icon = new ImageIcon(imagePath);
        }
        return this.icon;
    }//实现接口的方法

    public String toString() {
        return getText();
    }
}