package cn.edu.bistu.cs.processor;

import org.apache.log4j.Logger;

import cn.edu.bistu.cs.util.PageType;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;

public class ProductInfoPageProcessor implements SubPageProcessor {
	// 日志文件
	private static final Logger log = Logger.getLogger(ProductInfoPageProcessor.class);

	public boolean match(Request request) {
		// 如果request里面的链接包括【http://item.jd.com/】的话就是手机商品页
		return request.getUrl().startsWith("http://item.jd.com/");
	}

	public MatchOther processPage(Page page) {
		// 给这个页面添加标志，pipeline处理的时候好进行区分
		page.putField(PageType.PAGE_TYPE_FIELD_KEY, PageType.PRODUCT_INFO);
		// 获取当前商品的url
		String productUrl = page.getUrl().toString();
		page.putField("producturl", productUrl);
		// 获取商品名称
		String name = page.getHtml().xpath("//div[@class='sku-name']/text()").toString();
		page.putField("name", name);
		// 获取商品id
		String skuid = page.getHtml().xpath("//input[@type='checkbox']/@data-sku").toString();
		page.putField("skuid", skuid);
		// 添加产品价格页JSON地址
		page.addTargetRequest("http://p.3.cn/prices/get?skuid=J_" + skuid);
		// 添加商品评论的概要数据页JSON地址
		page.addTargetRequest("http://club.jd.com/clubservice.aspx?method=GetCommentsCount&referenceIds=" + skuid);

		// 参数是由两个部分组成的
		String parameter1 = page.getHtml().xpath("//ul[@class='parameter1 p-parameter-list']/li/div/p/text()").all()
				.toString();
		String parameter2 = page.getHtml().xpath("//ul[@class='parameter2 p-parameter-list']/li/text()").all()
				.toString();
		// 把两部分参数去除开头和结尾的“[]”并相加
		String parameter = parameter1.substring(1, parameter1.length() - 1) + "\n"
				+ parameter2.substring(1, parameter2.length() - 1);
		page.putField("parameter", parameter);
		// String url = page.getUrl().toString();
		// String sql = "INSERT INTO cellphoneInfo VALUES('" + id + "','" + name
		// + "','" + parameter + "','" + url + "')";
		log.info("成功获取" + skuid + "数据");
		return MatchOther.NO;
	}

}
