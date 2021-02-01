import com.alibaba.fastjson.JSON;
import com.github.kevinsawicki.http.HttpRequest;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class iNuaaSignin {
    public static void main(String[] args) throws IOException {
        Map<String, String> info = new HashMap<String, String>(50);
        /*
        此处写入用户名和密码
         */
        info.put("username", "041800512");
        info.put("password", "St123456");
        String jsonString = new String(
                Files.readAllBytes(
                        Paths.get("E:\\Code\\project_java\\iNuaaSignin\\header.json")));
        Map map = JSON.parseObject(jsonString,Map.class);
        HttpRequest request = HttpRequest.post("https://m.nuaa.edu.cn/uc/wap/login/check");
        Map<String, List<String>> header=request.form(info).headers();
        System.out.println("Host: https://m.nuaa.edu.cn/uc/wap/login/check Body: "
                + header.toString());
        /*
        获取cookie
         */
        List<String> cookie = header.get("Set-Cookie");
        map.put("Cookie",cookie.get(1));
        System.out.println(map);
        jsonString = new String(
                Files.readAllBytes(
                        Paths.get("E:\\Code\\project_java\\iNuaaSignin\\Info.json")));
        Map packet = JSON.parseObject(jsonString,Map.class);
        System.out.println(
                (HttpRequest.post("https://m.nuaa.edu.cn/ncov/wap/default/save")
                        .headers(map)
                        .form(packet)
                        .body()));
    }
}
