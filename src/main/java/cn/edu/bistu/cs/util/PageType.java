/**
 * 
 */
package cn.edu.bistu.cs.util;

/**
 * 京东爬虫在抓取的过程中需要处理多种页面，
 * 为了在后期处理（PipeLine）的过程中区分这些页面，
 * 需要对页面进行分类
 * @author hadoop
 *
 */
public class PageType {
	public static final String PAGE_TYPE_FIELD_KEY="_PT_";
	public static final String PRODUCT_LIST="PL";
	public static final String PRODUCT_INFO="PI";
	public static final String PRODUCT_PRICE="PP";
	public static final String PRODUCT_COMMENT_SUMMARY="CS";
}
