package com.ks.jdfen.controller.myutil;

import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdUtils {

    public static String getItemId(String itemId){
        try {
            if (itemId.contains("jd.com")){
                String[] split = itemId.split("html");
                String pattern = "\\d+";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(split[0]);
                if (m.find( )) {
                   return m.group(0) ;
                } else {
                    return "找不到商品id";
                }
            }else if(Long.parseLong(itemId)>1){
                itemId=itemId;
                return itemId;
            }else{
                return "输入异常.";
            }
        }catch (Exception e){
            System.out.println("输入异常.");
        }
        return "输入异常";
    }

    private String buildSign(String timestamp, String version, String signMethod ,String format ,  String method , String paramJson , String accessToken ,String appKey, String appSecret)  throws Exception {

//第一步，按照顺序填充参数

        Map<String, String> map = new TreeMap();

        map.put("timestamp", timestamp);

        map.put("v", version);

        map.put("sign_method", signMethod);

        map.put("format", format);

        map.put("method", method);

        //param_json为空的时候需要写成 "{}"

        map.put("param_json", paramJson);

        map.put("access_token", accessToken);

        map.put("app_key", appKey);

        StringBuilder sb = new StringBuilder(appSecret);

        //按照规则拼成字符串

        for (Map.Entry entry : map.entrySet()) {

            String name = (String) entry.getKey();

            String value = (String) entry.getValue();

            //检测参数是否为空

            if (this.areNotEmpty(new String[]{name, value})) {

                sb.append(name).append(value);

            }

        }

        sb.append(appSecret);

        //MD5

        return this.md5(sb.toString());

    }

    public static String md5(String source)

            throws Exception

    {

        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] bytes = md.digest(source.getBytes("utf-8"));

        return byte2hex(bytes);

    }

    private static String byte2hex(byte[] bytes) {

        StringBuilder sign = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {

            String hex = Integer.toHexString(bytes[i] & 0xFF);

            if (hex.length() == 1) {

                sign.append("0");

            }

            sign.append(hex.toUpperCase());

        }

        return sign.toString();

    }

    public static boolean areNotEmpty(String[] values) {

        boolean result = true;

        if ((values == null) || (values.length == 0))

            result = false;

        else {

            for (String value : values) {

                result &= !isEmpty(value);

            }

        }

        return result;

    }



    public static boolean isEmpty(String value) {

        int strLen;

        if ((value == null) || ((strLen = value.length()) == 0))

            return true;

        for (int i = 0; i < strLen; i++) {

            if (!Character.isWhitespace(value.charAt(i))) {

                return false;

            }

        }

        return true;

    }

    /*    private String getTimestamp(){
        Date now = new Date();
        String time = sdf.format(now);
        return time;
    }*/
}
