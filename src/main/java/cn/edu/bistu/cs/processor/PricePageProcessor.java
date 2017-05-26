package cn.edu.bistu.cs.processor;

import com.alibaba.fastjson.*;

import cn.edu.bistu.cs.util.PageType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;

public class PricePageProcessor implements SubPageProcessor {

	public boolean match(Request request) {
		return request.getUrl().startsWith("http://p.3.cn/prices");
	}

	/**
	 * 价格Json处理办法
	 */
	/*
	 * 其中一个对象的示例 {"id":"J_2967929","p":"2499.00","m":"3222.00","op":"2499.00"}
	 * 
	 */
	public MatchOther processPage(Page page) {
		// 给这个页面添加标志，pipeline处理的时候好进行区分
		page.putField(PageType.PAGE_TYPE_FIELD_KEY, PageType.PRODUCT_PRICE);
		String json = page.getRawText();
		json = json.substring(1, json.length() - 2);	//去掉[]
		JSONObject jsonObject = JSON.parseObject(json);
		String skuid = jsonObject.getString("id").substring(2); // 得到id属性值
		String price = jsonObject.getString("p"); // 得到P属性值
		page.putField("skuid", skuid);
		page.putField("price", price);
		return MatchOther.NO;
	}

}
