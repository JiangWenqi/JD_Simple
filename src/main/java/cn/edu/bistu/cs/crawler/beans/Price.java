package cn.edu.bistu.cs.crawler.beans;

public class Price {
	private double price = 0;
	private String createTime = "";

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "Price [price=" + price + ", createTime=" + createTime + "]";
	}
}
