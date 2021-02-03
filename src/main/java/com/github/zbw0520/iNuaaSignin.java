package com.github.zbw0520;

import com.alibaba.fastjson.JSON;
import com.github.kevinsawicki.http.HttpRequest;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class iNuaaSignin {
    //Plug in your username
    static String username = "xxxxxx";
    //Plug in your password
    static String password = "xxxxxx";

    public static void main(String[] args) throws Exception {
        Map<String, String> info = new HashMap<String, String>(50);
        info.put("username", username);
        info.put("password", password);
        String jsonString = new String(
                Files.readAllBytes(
                        Paths.get("/data/iNuaaSigninZX/header.json")));
        Map map = JSON.parseObject(jsonString, Map.class);
        HttpRequest request = HttpRequest.post("https://m.nuaa.edu.cn/uc/wap/login/check");
        Map<String, List<String>> header = request.form(info).headers();
        /*
        获取cookie
         */
        System.out.println(header);
        List<String> cookie = header.get("Set-Cookie");
        map.put("Cookie", cookie.get(1));
        System.out.println(cookie.get(1));
        jsonString = new String(
                Files.readAllBytes(
                        Paths.get("/data/iNuaaSigninZX/Info.json")));
        Map packet = JSON.parseObject(jsonString, Map.class);
        String resultString = HttpRequest.post("https://m.nuaa.edu.cn/ncov/wap/default/save")
                .headers(map)
                .form(packet)
                .body();
        Map result = JSON.parseObject(resultString);
        System.out.println(
                result.get("m")
        );
        sendEmail((String) result.get("m"));
    }

    public static void sendEmail(String status) {
        try {

            //设置发件人
            String from = "1620419196@qq.com";

            //设置收件人
            String to = "593128040@qq.com";

            //设置邮件发送的服务器，这里为QQ邮件服务器
            String host = "smtp.qq.com";

            //获取系统属性
            Properties properties = System.getProperties();

            //SSL加密
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);

            //设置系统属性
            properties.setProperty("mail.smtp.host", host);
            properties.put("mail.smtp.auth", "true");

            //获取发送邮件会话、获取第三方登录授权码
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, "atllzwxyyxtgdbib");
                }
            });

            Message message = new MimeMessage(session);

            //防止邮件被当然垃圾邮件处理，披上Outlook的马甲
            message.addHeader("X-Mailer","Microsoft Outlook Express 6.00.2900.2869");

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            //邮件标题
            message.setSubject("南航古惑仔已经成功帮您签到啦！【请勿回复】");

            BodyPart bodyPart = new MimeBodyPart();

            bodyPart.setText("尊敬的客户zx您好！\n\n" +
                    "这是一封由南航古惑仔服务器自动发送的邮件，这封邮件是告知您，您的签到结果为"+"【"+status+"】"+"。\n\n" +
                    "如果您对签到结果有任何疑问，请联系南航古惑仔的邮箱1620419196@qq.com。\n\n\n\n" +
                    "谢谢！\n\n\n" +
                    "张博闻\n" +
                    "南航古惑仔");

            Multipart multipart = new MimeMultipart();

            multipart.addBodyPart(bodyPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("mail transports successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
