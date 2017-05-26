package cn.edu.bistu.cs.crawler.beans;

import java.util.List;

public class Cellphone {
	private String skuid;
	private String name;
	private String parameter;
	private String url;
	private List<CommentsCount> commentCounts;
	private List<Price> prices;
	public String getSkuid() {
		return skuid;
	}
	public void setSkuid(String skuid) {
		this.skuid = skuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<CommentsCount> getCommentCounts() {
		return commentCounts;
	}
	public void setCommentCounts(List<CommentsCount> commentCounts) {
		this.commentCounts = commentCounts;
	}
	public List<Price> getPrices() {
		return prices;
	}
	public void setPrices(List<Price> prices) {
		this.prices = prices;
	}
	
}
