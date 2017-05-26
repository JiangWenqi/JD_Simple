package cn.edu.bistu.cs.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

public class PricePipeline extends AbsPipeline {

	public PricePipeline(String pageType) {
		super(pageType);
	}

	public void process(ResultItems resultItems, Task task) {
		if (!check(resultItems))
			return;
		String price = resultItems.get("price");
		String sql = "INSERT INTO cellphoneprice (j_id,price) VALUES('" + skuid + "','" + price + "')";
		jdbc.insert(sql);
	}

}
