package com.ks.jdfen.myutil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class GetJson {
    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     *            请求参数，请求参数应该是 json 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String jsonPost(String url, JSONObject json) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Host", "union.jd.com");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("accept", "application/json, text/plain, */*");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Origin", "https://union.jd.com");
            conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
            conn.setRequestProperty("Sec-Fetch-Mode", "cors");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
            conn.setRequestProperty("Cookie", "shshshfpa=2ec8c32c-4014-9006-9c04-cba91b358403-1560593957; shshshfpb=pngh4UzYedJVIaQvA8AJY3Q%3D%3D; pinId=4AzYpL5oOAlzzZlqIIaK1A; __jdu=1102692930; ipLoc-djd=19-1607-4773-0; user-key=f15a079f-b7d9-4b61-8af8-b0ceac812ac8; unick=imc700; _tp=N%2FWw6vyqMUG740PqQGV%2F3g%3D%3D; _pst=18627783779_p; cn=0; ceshi3.com=201; __jdv=239834264|baidu|-|organic|not set|1584796771926; ssid=\"3XQi5aXHRrij/eg4dlGUHw==\"; TrackID=101XXktQm0iyX0d03Y5YHot8iuRZPXdiJOR2ULFVzWiwItO3hXp58piyNyfbNB7gk8zh_x8x55MMcFQeGYwWqGrEdl7JmXd6wdPuuOdJ2oDo; pin=18627783779_p; login=true; MMsgId18627783779_p=177807496; MNoticeId18627783779_p=283; 3AB9D23F7A4B3C9B=4EXS34BEADVKBH2RARH7VP2LQQW4THYVRAKKZIGS4LOOCQTRNNTUKZUEK5DPPNAYJHEAS5T4G4UPOMCDM2F6WJVQQU; areaId=19; shshshfp=77befb85204084ba0bf6501f389b4c73; mba_muid=1102692930; __jd_ref_cls=Mnpm_ComponentApplied; sidebarStatus=1; retina=0; cid=3; wxa_level=1; webp=1; wq_logid=1584805139.2063174072; thor=D3B7A4E104301A8394CA2F828DCEDA7B71FB89F2FCDA1D723BC6CBCF20F97835989523457C15873F47B359EE9C4878DB21B82836687FF3D670C87544616F4D40F9827CB5DD38C6E54DEBE4A484DFFF381CB809B38D315874AC6B7ADC8155DB10BDD8B1B23BFC85B1F98276FC318916E25DAF3950F7F42B3B7E7B8ADA31FA7BF20CDD2B0673E9BAD35169215252E5E048; shshshsID=00739f70192c2ff552fa42acb7643261_3_1584866246590; __jda=209449046.1102692930.1560593955.1584802737.1584865735.45; __jdb=209449046.14.1102692930|45.1584865735; __jdc=209449046; RT=\"z=1&dm=jd.com&si=4qli4kh9iq5&ss=k82s4y5k&sl=9&tt=6o6&ld=bcdd&nu=3d2f85352c70c73af100bf1449967844&cl=95ph\"");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数

            out.print(json);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}

