package cn.edu.bistu.cs.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

public class ProductInfoPipeline extends AbsPipeline {

	public ProductInfoPipeline(String pageType) {
		super(pageType);
	}

	public void process(ResultItems resultItems, Task task) {
		if (!check(resultItems))
			return;
		String name = resultItems.get("name");// 产品名称

		String parameter = resultItems.get("parameter");// 商品参数

		String productUrl = resultItems.get("producturl");// 商品链接

		String sql = "INSERT INTO cellphoneInfo VALUES('" + skuid + "','" + name + "','" + parameter + "','"
				+ productUrl + "') " + "ON DUPLICATE KEY UPDATE name = '" + name + "' , parameter = '" + parameter
				+ "' , url = '" + productUrl + "'";

		jdbc.insert(sql);
	}

}
