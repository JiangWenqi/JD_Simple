package cn.edu.bistu.cs.crawler.beans;

public class CommentsCount {

	private int commentCount = 0;
	private double goodRate = 0;
	private String createTime = "";

	public double getGoodRate() {
		return goodRate;
	}

	public void setGoodRate(double goodRate) {
		this.goodRate = goodRate;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "CommentsCount [commentCount=" + commentCount + ", goodRate=" + goodRate + ", createTime=" + createTime
				+ "]";
	}
	

}
/**
        {
            "SkuId": 1378538,
            "ProductId": 1378538,
            "Score1Count": 239,
            "Score2Count": 58,
            "Score3Count": 177,
            "Score4Count": 708,
            "Score5Count": 18469,
            "ShowCount": 3061,
            "CommentCount": 19651,
            "AverageScore": 5,
            "GoodCount": 19177,
            "GoodRate": 0.977,
            "GoodRateShow": 98,
            "GoodRateStyle": 146,
            "GeneralCount": 235,
            "GeneralRate": 0.011,
            "GeneralRateShow": 1,
            "GeneralRateStyle": 2,
            "PoorCount": 239,
            "PoorRate": 0.012,
            "PoorRateShow": 1,
            "PoorRateStyle": 2
        }

 */
