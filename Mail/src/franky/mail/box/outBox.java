package franky.mail.box;

import javax.swing.*;

/**
 * @Author FrankY
 * @Date 2019-12-17 00:01
 * @Contact frank1045325433@outlook.com
 */
public class outBox extends abstractBox {
    @Override
    public String getText() {
        return "发件箱";
    }

    public ImageIcon getImageIcon() {
        return super.getImageIcon("image/outbox1.png");
    }
}
