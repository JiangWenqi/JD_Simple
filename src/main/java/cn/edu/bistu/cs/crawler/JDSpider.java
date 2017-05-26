package cn.edu.bistu.cs.crawler;

import javax.management.JMException;

import cn.edu.bistu.cs.pipeline.CommentSummaryPipeline;
import cn.edu.bistu.cs.pipeline.PricePipeline;
import cn.edu.bistu.cs.pipeline.ProductInfoPipeline;
import cn.edu.bistu.cs.processor.CommentSummaryPageProcessor;
import cn.edu.bistu.cs.processor.PricePageProcessor;
import cn.edu.bistu.cs.processor.ProductInfoPageProcessor;
import cn.edu.bistu.cs.processor.ProductListPageProcessor;
import cn.edu.bistu.cs.util.ConfigReader;
import cn.edu.bistu.cs.util.PageType;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.handler.CompositePageProcessor;
import us.codecraft.webmagic.monitor.SpiderMonitor;

public class JDSpider {

	public static final String PAGE_FIELD_SKUID = "skuid";

	public static void main(String[] args) throws JMException {
		Site site = Site.me().setCycleRetryTimes(20).setSleepTime(500).setTimeOut(20000)
				.setUserAgent("Mozilla/5.0 (X11; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0").setUseGzip(true);
		CompositePageProcessor jdProcessor = new CompositePageProcessor(site);
		/**
		 * 添加各子页面的PageProcessor, 下面的添加顺序对程序性能有一定的影响，
		 * 因为CompositePageProcessor是按照添加的顺序去逐个调用对应SubPageProcessor的match方法的，
		 * 但因为爬虫抓取的过程中，网络延时以及休眠等待时间占据了大多数的时间，因此，这个顺序对程序性能影响不大
		 */

		jdProcessor.addSubPageProcessor(new ProductListPageProcessor()); // 商品列表的处理办法
		jdProcessor.addSubPageProcessor(new ProductInfoPageProcessor()); // 具体商品的处理办法
		jdProcessor.addSubPageProcessor(new PricePageProcessor()); // 价格json处理办法
		jdProcessor.addSubPageProcessor(new CommentSummaryPageProcessor()); // 商品评论概要页处理办法

		Spider spider = Spider.create(jdProcessor);
		spider.addUrl(ConfigReader.getConfig("START_PAGE"));// 添加起始页面
		spider.thread(5);// 增加多线程
		/**
		 * 为各子页面添加Pipeline处理方法
		 */
		spider.addPipeline(new ProductInfoPipeline(PageType.PRODUCT_INFO)); // 添加具体商品PipeLine处理方法
		spider.addPipeline(new PricePipeline(PageType.PRODUCT_PRICE)); // 添加具体的价格Pipeline处理方法
		spider.addPipeline(new CommentSummaryPipeline(PageType.PRODUCT_COMMENT_SUMMARY));// 商品评论概要Pipeline处理办法

		SpiderMonitor.instance().register(spider);
		spider.run();
	}

}
