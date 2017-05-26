package cn.edu.bistu.cs.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;

import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.bistu.cs.util.PageType;


/**
 * 商品（手机）目录页抓取商品链接和其他目录页
 */
public class ProductListPageProcessor implements SubPageProcessor {

	// 日志文件
	private static final Logger log = Logger.getLogger(ProductListPageProcessor.class);

	public boolean match(Request request) {
		return request.getUrl().startsWith("http://list.jd.com/list.html?");
	}

	public MatchOther processPage(Page page) {
		// 给这个页面添加标志，pipeline处理的时候好进行区分
		page.putField(PageType.PAGE_TYPE_FIELD_KEY, PageType.PRODUCT_LIST);
		log.info("抓取目录页面：" + page.getUrl()); // 日志
		// xpath 获取当前目录页下所有的商品链接
		List<String> productUrls = page.getHtml().xpath("//div[@class='p-img']/a[@target='_blank']/@href").all();
		for (String productUrl : productUrls) {
			log.info("商品（手机）链接：" + productUrl); // 日志
			page.addTargetRequest(productUrl);
		}

		// xpath 获取当前目录页下 其他目录页的链接
		List<String> productListUrls = page.getHtml().xpath("//span[@class='p-num']/a/@href").all();
		for (String productListUrl : productListUrls) {
			log.info("目录页的链接：" + productListUrl); // 日志
			page.addTargetRequest(productListUrl);
		}
		/**
		 * 产品清单页，只是抓取出产品清单以及其他清单页的地址，不需要做后续的处理，因此可以设置skip
		 */
		page.setSkip(true);
		// 当前PageProcessor处理完毕后，没有必要再尝试其他PageProcessor了
		return MatchOther.NO;
	}

}
