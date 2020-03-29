package com.ks.jdfen.myutil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class URLConnection {
	 

    /**
	 * post请求封装 参数为{"a":1,"b":2,"c":3}
	 * @param path 接口地址
	 * @param Info 参数
	 * @return
	 * @throws IOException
	 */
    public static JSONObject postResponse(String path,JSONObject Info) throws IOException{
    	HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(path);
        
        post.setHeader("Host", "union.jd.com");
        post.addHeader("Connection", "Keep-Alive");
        post.addHeader("Accept", "application/json, text/plain, */*");
        post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
        post.addHeader("Content-Type", "application/json;charset=UTF-8");
        post.addHeader("Origin", "https://union.jd.com");
        post.addHeader("Sec-Fetch-Site", "same-origin");
        post.addHeader("Sec-Fetch-Mode", "cors");
        post.addHeader("Referer", "https://union.jd.com/proManager/index?pageNo=1&keywords=12514244696");
        post.addHeader("Accept-Encoding", "gzip, deflate, br");
        post.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
        post.addHeader("Cookie", "shshshfpa=2ec8c32c-4014-9006-9c04-cba91b358403-1560593957; shshshfpb=pngh4UzYedJVIaQvA8AJY3Q%3D%3D; pinId=4AzYpL5oOAlzzZlqIIaK1A; __jdu=1102692930; ipLoc-djd=19-1607-4773-0; user-key=f15a079f-b7d9-4b61-8af8-b0ceac812ac8; unick=imc700; _tp=N%2FWw6vyqMUG740PqQGV%2F3g%3D%3D; _pst=18627783779_p; cn=0; ceshi3.com=201; __jdv=239834264|baidu|-|organic|not set|1584796771926; ssid=\"3XQi5aXHRrij/eg4dlGUHw==\"; TrackID=101XXktQm0iyX0d03Y5YHot8iuRZPXdiJOR2ULFVzWiwItO3hXp58piyNyfbNB7gk8zh_x8x55MMcFQeGYwWqGrEdl7JmXd6wdPuuOdJ2oDo; pin=18627783779_p; login=true; MMsgId18627783779_p=177807496; MNoticeId18627783779_p=283; 3AB9D23F7A4B3C9B=4EXS34BEADVKBH2RARH7VP2LQQW4THYVRAKKZIGS4LOOCQTRNNTUKZUEK5DPPNAYJHEAS5T4G4UPOMCDM2F6WJVQQU; areaId=19; shshshfp=77befb85204084ba0bf6501f389b4c73; mba_muid=1102692930; __jd_ref_cls=Mnpm_ComponentApplied; sidebarStatus=1; retina=0; cid=3; wxa_level=1; webp=1; wq_logid=1584805139.2063174072; thor=D3B7A4E104301A8394CA2F828DCEDA7B71FB89F2FCDA1D723BC6CBCF20F97835989523457C15873F47B359EE9C4878DB21B82836687FF3D670C87544616F4D40F9827CB5DD38C6E54DEBE4A484DFFF381CB809B38D315874AC6B7ADC8155DB10BDD8B1B23BFC85B1F98276FC318916E25DAF3950F7F42B3B7E7B8ADA31FA7BF20CDD2B0673E9BAD35169215252E5E048; shshshsID=00739f70192c2ff552fa42acb7643261_3_1584866246590; __jda=209449046.1102692930.1560593955.1584802737.1584865735.45; __jdb=209449046.14.1102692930|45.1584865735; __jdc=209449046; RT=\"z=1&dm=jd.com&si=4qli4kh9iq5&ss=k82s4y5k&sl=9&tt=6o6&ld=bcdd&nu=3d2f85352c70c73af100bf1449967844&cl=95ph\"");
        // 发送POST请求必须设置如下两行
        String result = "";
        
        try {
            StringEntity s = new StringEntity(Info.toString(), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
            post.setEntity(s);

            // 发送请求
            HttpResponse httpResponse = client.execute(post);

            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            strber.append(line + "\n");
            inStream.close();

            result = strber.toString();
            System.out.println(result);
            
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求服务器成功，做相应处理");
            } else {
                System.out.println("请求服务端失败");
            }

        } catch (Exception e) {
            System.out.println("请求异常");
            throw new RuntimeException(e);
        }
        return new JSONObject(result);
    }
	
}
