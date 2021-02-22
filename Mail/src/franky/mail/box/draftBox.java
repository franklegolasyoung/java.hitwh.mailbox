package franky.mail.box;

import javax.swing.*;

/**
 * @Author FrankY
 * @Date 2019-12-17 00:04
 * @Contact frank1045325433@outlook.com
 */
public class draftBox extends abstractBox {
    @Override
    public String getText() {
        return "草稿箱";
    }

    public ImageIcon getImageIcon() {
        return super.getImageIcon("image/draft1.png");
    }
}
