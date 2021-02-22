package franky.mail.exception;

/**
 * 发送邮件异常
 * 
 * @Author FrankY
 * @Contact frank1045325433@outlook.com
 */
public class SendMailException extends RuntimeException {

	public SendMailException(String s) {
		super(s);
	}
}
