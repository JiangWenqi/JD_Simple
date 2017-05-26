package cn.edu.bistu.cs.pipeline;

import cn.edu.bistu.cs.crawler.JDSpider;
import cn.edu.bistu.cs.util.JDBC;
import cn.edu.bistu.cs.util.PageType;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.pipeline.Pipeline;

/** 这是pipeline公共父类，用来加载一些公共方法 */
public abstract class AbsPipeline implements Pipeline {

	// 初始化数据库连接
	protected static JDBC jdbc = new JDBC();
	// 日志文件
	private static final Logger log = Logger.getLogger(AbsPipeline.class);
	// 商品编号
	protected String skuid = null;
	// 页面类型
	private String pageType = null;

	public AbsPipeline(String pageType) {
		this.pageType = pageType;
	}

	/**
	 * 检查当前Pipeline是否是用来处理当前页面类型的
	 * 
	 * @param results
	 * @return
	 */

	protected boolean check(ResultItems results) {
		/**
		 * 首先检查PageType是否存在
		 */
		String pageType = results.get(PageType.PAGE_TYPE_FIELD_KEY);
		if (pageType == null) {
			// 任何一个页面抓取的数据中，都应该包含PageType.PAGE_TYPE_FIELD_KEY这样一个字段的值，用来标识这个页面抓取数据的类型
			log.error("页面:" + results.getRequest().getUrl() + "的抓取结果中没有包含" + PageType.PAGE_TYPE_FIELD_KEY + "数据项");
			return false;
		}
		/**
		 * 然后检查SKUID是否存在
		 */
		skuid = results.get(JDSpider.PAGE_FIELD_SKUID);

		if (skuid == null || "".equals(skuid)) {
			log.error("页面:" + results.getRequest().getUrl() + "的抓取结果中SKUID为空");
			return false;
		}
		/**
		 * 接下来检查pageType是否匹配
		 */
		if (pageType.equalsIgnoreCase(this.pageType)) {
			return true;
		} else {
			return false;
		}
	}
}
