package cn.edu.bistu.cs.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.handler.SubPageProcessor;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.*;

import cn.edu.bistu.cs.util.PageType;

public class CommentSummaryPageProcessor implements SubPageProcessor {
	Logger log = Logger.getLogger(CommentSummaryPageProcessor.class);

	public boolean match(Request request) {
		return request.getUrl().startsWith("http://club.jd.com/clubservice.aspx?method=GetCommentsCount");
	}

	public MatchOther processPage(Page page) {
		// 给这个页面添加标志，pipeline处理的时候好进行区分
		page.putField(PageType.PAGE_TYPE_FIELD_KEY, PageType.PRODUCT_COMMENT_SUMMARY);
		JSONObject jsonObj = JSON.parseObject(page.getRawText());
		// 取json中“CommentsCount”对象数组里面的对象
		JSONArray jsonArray = jsonObj.getJSONArray("CommentsCount");
		
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject commentJsonObject = jsonArray.getJSONObject(i);
			String skuid = commentJsonObject.getString("ProductId"); // 商品ID
			String commentCount = commentJsonObject.getString("CommentCount"); // 商品评论总数
			String goodRate = commentJsonObject.getString("GoodRate"); // 得到好评率
			page.putField("skuid", skuid);
			page.putField("commentcount", commentCount);
			page.putField("goodrate", goodRate);
		}
		return MatchOther.NO;
	}
}
