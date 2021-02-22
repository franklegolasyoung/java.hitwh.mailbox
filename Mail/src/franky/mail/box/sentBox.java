package franky.mail.box;

import javax.swing.*;

/**
 * @Author FrankY
 * @Date 2019-12-17 00:08
 * @Contact frank1045325433@outlook.com
 */
public class sentBox extends abstractBox {
    @Override
    public String getText() {
        return "已发送";
    }

    public ImageIcon getImageIcon() {
        return super.getImageIcon("image/sentbox1.png");
    }
}
