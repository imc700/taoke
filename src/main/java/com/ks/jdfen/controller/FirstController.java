package com.ks.jdfen.controller;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.ks.jdfen.controller.myutil.JdUtils;
import com.ks.jdfen.controller.myutil.URLConnection;
import jd.union.open.goods.promotiongoodsinfo.query.request.UnionOpenGoodsPromotiongoodsinfoQueryRequest;
import jd.union.open.goods.promotiongoodsinfo.query.response.UnionOpenGoodsPromotiongoodsinfoQueryResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/")
public class FirstController {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式


    private static final String SERVER_URL = "https://router.jd.com/api";
    private static final String APP_KEY = "dff4a0d857c088bd4ea188570e6392ce";
    private static final String APP_SECRET = "123142efeec44717876ef0a5b4865e94";
    JdClient client = new DefaultJdClient(SERVER_URL, "", APP_KEY, APP_SECRET);

    //因为京东联盟高级api需要流量达到级别才能申请,此方法暂时无效
/*    @RequestMapping("/getLink")
    public UnionOpenPromotionByunionidGetResponse getLink() throws JdException {
        UnionOpenPromotionByunionidGetRequest request = new UnionOpenPromotionByunionidGetRequest();
        PromotionCodeReq promotionCodeReq = new PromotionCodeReq();
        promotionCodeReq.setMaterialId("https://wqitem.jd.com/item/view?sku=23484023378");
        promotionCodeReq.setUnionId(Long.parseLong("1000046951"));
        request.setPromotionCodeReq(promotionCodeReq);
        UnionOpenPromotionByunionidGetResponse response = client.execute(request);
        return response;
    }*/


    @RequestMapping("/")
    public String jdSearch() {
        return "jdSearch";
    }


    @RequestMapping("/query/{itemId}")
    @ResponseBody
    public UnionOpenGoodsPromotiongoodsinfoQueryResponse query(@PathVariable("itemId") String itemId) throws JdException {
        itemId = JdUtils.getItemId(itemId);
        UnionOpenGoodsPromotiongoodsinfoQueryRequest request=new UnionOpenGoodsPromotiongoodsinfoQueryRequest();
        request.setSkuIds(itemId);
        UnionOpenGoodsPromotiongoodsinfoQueryResponse response=client.execute(request);
        return response;
    }

    @RequestMapping("/getLink/{itemId}")
    @ResponseBody
    public String getLink(@PathVariable("itemId") String itemId) throws JdException, IOException {
        itemId = JdUtils.getItemId(itemId);
        JSONObject paramJson = new JSONObject("{\"data\":{\"isPinGou\":0,\"materialId\":"+itemId+",\"materialType\":1,\"planId\":409135135,\"promotionType\":15,\"receiveType\":\"cps\",\"wareUrl\":\"http://item.jd.com/"+itemId+".html\",\"isSmartGraphics\":0,\"requestId\":\"s_bc151a82901a4c0fab9140e518abfe33\"}}");
        JSONObject jsonObject = URLConnection.postResponse("https://union.jd.com/api/receivecode/getCode", paramJson);
        return jsonObject.toString();
    }

}
