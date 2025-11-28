package cn.edu.seig.vibemusic.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Random;

/**
 * 邮件服务类
 * 用于发送验证码邮件
 */
public class EmailService {

    // 邮件服务器配置
    private static final String SMTP_HOST = "smtp.qq.com";
    private static final String SMTP_PORT = "465";
    private static final String EMAIL_USERNAME = "2191684957@qq.com"; // 请修改为你的邮箱
    private static final String EMAIL_PASSWORD = "bphgxcglttecdjdi";   // 请修改为你的邮箱授权码

    /**
     * 发送验证码邮件
     * @param toEmail 收件人邮箱
     * @return 生成的验证码，发送失败返回null
     */
    public String sendVerificationCode(String toEmail) {
        // 生成6位随机验证码
        String code = generateCode();

        // 配置邮件服务器属性
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);

        // 创建认证会话
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            // 创建邮件消息
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Vibe Music - 验证码");
            message.setText("您的验证码是：" + code + "\n\n该验证码5分钟内有效，请勿泄露给他人。");

            // 发送邮件
            Transport.send(message);
            return code;
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成6位随机验证码
     * @return 验证码字符串
     */
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}


