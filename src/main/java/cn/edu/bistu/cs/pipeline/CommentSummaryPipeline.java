package cn.edu.bistu.cs.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 商品评论概要Pipeline处理办法
 */
public class CommentSummaryPipeline extends AbsPipeline {

	public CommentSummaryPipeline(String pageType) {
		super(pageType);
	}

	public void process(ResultItems resultItems, Task task) {
		if(!check(resultItems))
			return;
		String commentCount = resultItems.get("commentcount");
		String goodRate = resultItems.get("goodrate");
		String sql = "INSERT INTO cellphonecomment (j_id,commentcount,goodrate) VALUES('" + skuid + "','" + commentCount
				+ "','" + goodRate + "')";
		jdbc.insert(sql);

	}

}
