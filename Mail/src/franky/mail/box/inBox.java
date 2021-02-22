package franky.mail.box;

import javax.swing.*;

/**
 * @Author FrankY
 * @Date 2019-12-16 23:59
 * @Contact frank1045325433@outlook.com
 */
public class inBox extends abstractBox {
    @Override
    public String getText() {
        return "收件箱";
    }

    public ImageIcon getImageIcon() {
        return super.getImageIcon("image/inbox1.png");
    }
}
