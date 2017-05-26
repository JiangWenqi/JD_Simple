# JD_Simple
京东爬虫架构重构、webMagic、pipeline、mySQL、lucene、luceneSearch
使用Lucene创建索引和执行检索
一、	WebMagic爬虫与Lucene的衔接
1. 实验目的
	掌握在已有WebMagic爬虫基础上实现面向Lucene索引的Pipeline的方法；
	掌握Lucene构建索引时不同类型的Field的选择和配置。
2. 实验环境
	操作系统：Windows / Mac OS X / Linux
	JDK版本：JDK 1.7及以上
	Eclipse：Eclipse
3．实验要求
1)	根据试验一选择的实验题目，编写LucenePipeline类，实现WebMagic的Pipeline接口：
public class LucenePipeline implements Pipeline {
	public void process(ResultItems arg0, Task arg1) {
	//实现对抓取出来的结果集的提取和索引工作
	}
}

A.	代码简单说明：
因为考虑到京东商品的一个页面无法涵盖：价格，参数，评论等诸多信息，导致在实现商品唯一标识ID的时候会覆盖之前已创建好的索引。（例如：一个包含价格的Page在用lucenePipeline添加索引的时候，因为商品ID一样，会覆盖之前cellphone information page已经创建好的索引。）所以我直接从数据库导入商品数据创建索引。
B.	JDBC代码展示（用于连接数据库的接口类）

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {

private final String driver = ConfigReader.getConfig("MYSQL_DEIVER");
private final String url = ConfigReader.getConfig("MYSQL_URL");
private final String user = ConfigReader.getConfig("MYSQL_USER");
private final String password = ConfigReader.getConfig("MYSQL_PASSWORD");
private static Connection conn;
private static Statement stmt;

	public JDBC() {
		try {
			// 加载驱动
			Class.forName(driver);
			// 连接数据库
			conn = DriverManager.getConnection(url, user, password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			System.out.println("The database connection is successful!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 插入语句
	public void insert(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 查询语句
	public ResultSet select(String sql) {
		try {
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		try {

			conn.close();
			System.out.println("Close the database connection successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JDBC jdbc = new JDBC();
		jdbc.insert("INSERT INTO cellphoneInfo(id,name) VALUES('123', '小米5'),('1234','小米6')");
		jdbc.close();
	}
}
C.	查询语句
// 多表连接查询语句
String sql = 
"select ci.id,ci.name, ci.parameter, ci.url,cp.price, cc.commentcount, cc.goodrate, cc.createtime from cellphoneinfo ci join cellphoneprice cp on ci.id = cp.j_id join cellphonecomment cc on ci.id=cc.j_id";
// 执行查询
ResultSet cellphone = jdbc.select(sql);

2)	在LucenePipeline中设计适当的Field格式，构建Lucene索引，要求：
A.	使用支持中文的Analyzer，如HanLPAnalyzer，（相关jar包在网盘中已经上传）

a)	代码展示：
// HanLP分词 Analyzer
Analyzer analyzer = new HanLPAnalyzer();
IndexWriterConfig config = new IndexWriterConfig(analyzer);

B.	索引应该至少包含如下字段：
京东商城爬虫需要包含商品名称、商品价格、商品总评价数、商品好评度、商品介绍、商品URL等字段。


a)	代码展示：
doc.add(new StringField("SKUID", skuid, Store.YES));
doc.add(new TextField("NAME", name, Store.YES));
doc.add(new TextField("PARAMETER", parameter, Store.YES));
doc.add(new StoredField("URL", url));
doc.add(new DoublePoint("PRICE", price));
doc.add(new IntPoint("COMMENTCOUNT", commentCount));
doc.add(new DoublePoint("GOODRATE", goodRate));
doc.add(new LongPoint("CREATETIME", createTime));


C.	使用updateDocument(term, doc)方法添加索引，避免重复对同一个页面建立索引。
提示：实现这一功能的关键在于，需要在索引中增加一个可以用作页面唯一标识的ID字段（Field）。在添加索引时，根据ID字段对索引执行更新操作，请自行查看Lucene的API实现上述功能:
http://lucene.apache.org/core/6_5_1/core/org/apache/lucene/index/IndexWriter.html#updateDocument-org.apache.lucene.index.Term-java.lang.Iterable-

a)	代码展示：
writer.updateDocument(new Term("SKUID", skuid), doc);
writer.commit();


D.	对日期、时间类型数据，可以通过编写程序，转换为一个长整形数据，并使用Lucene提供的LongPoint字段类型加以索引，从而在后续的检索部分实现针对日期的范围检索。
如果对Java日期类型处理不熟悉，也可以直接将日期转换为对应的整数形式，如“2015-01-13 23:00:21”，可以转换为20150113230021。相应地，可以将日期字段建模为IntPoint类型。参考：
http://www.cnblogs.com/loveyakamoz/archive/2011/08/18/2145047.html
http://blog.csdn.net/gubaohua/article/details/575488/

a)	代码展示：
// 对时间进行格式化
String time = cellphone.getString("createtime");
SimpleDateFormat format 
= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Date date = format.parse(time);
long createTime = date.getTime() / 1000;
// 将日期字段建模为	LongPoint
doc.add(new LongPoint("CREATETIME", createTime));

 
二、	基于Lucene的简单检索
1．实验目的
	掌握使用Lucene API实现简单检索的方法；
2．实验要求	
1). 编写程序，查询并输出索引中包含的文档数
A.	代码展示：
	/*
	 * 查询文档数目
	 */
	public int getDocCount() {
		try {
			return reader.getDocCount("SKUID");
		} catch (IOException e) {
			log.error("查询文档数失败");
			e.printStackTrace();
		}
		return 0;
	}
B.	结果展示：
 
2). 根据实验一的题目选择，编写程序，分别检索“商品名称”字段中包含单词“苹果”的页面并输出其商品名称和商品URL，输出应该类似下图：
 


A.	代码展示：
/*
	 * 根据手机名查询
	 */
	public void searchByName(String name) throws IOException, ParseException {
		TermQuery query = new TermQuery(new Term("NAME", name));
		TopDocs results = searcher.search(query, hitsCount);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		System.out.println("一共查询到【" + numTotalHits + "】部相关手机：");
		// 检索到的文档总数和需要展示的文档数做比较，取小值
		int number = Math.min(hits.length, hitsCount);
		for (int i = 0; i < number; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String cellphoneName = doc.get("NAME");
			String url = doc.get("URL");
			System.out.println(url + "/" + cellphoneName);
			// System.out.println("doc=" + hits[i].doc + " score=" +
			// hits[i].score);
		}
	}
B.	结果展示：
 

3). 在实验2.1的基础上，检索并输出商品价格在1000~3500元之间的商品列表，输出其商品名称和商品URL，输出应该类似下图：
 
A.	代码展示：
	/* 根据价格区间查找 */
	public void searchByPriceRange(double low, double high) throws IOException {
		Query query = DoublePoint.newRangeQuery("PRICE", low, high);
		TopDocs results = searcher.search(query, hitsCount);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		System.out.println("一共查询到【" + numTotalHits + "】部手机在这个价格区间：");
		int number = Math.min(hits.length, hitsCount);
		for (int i = 0; i < number; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String name = doc.get("NAME");
			String url = doc.get("URL");
			System.out.println(url + "/" + name);
			// System.out.println("doc=" + hits[i].doc + " score=" +
			// hits[i].score);
		}
	}
B.	结果展示：
 

3．实验结果
 
 
三、	基于Lucene的复合检索
1．实验目的
	掌握使用Lucene API实现复合检索的方法；
2．实验要求
1)【京东爬虫】编写函数，使用BooleanQuery，实现针对索引中的“商品名称”和“商品介绍”字段进行联合检索，并将检索结果输出，输出内容中至少应该包含商品ID/商品URL，“商品名称”两个字段内容。检索词作为函数的参数输入。

如，使用“中文”作为检索词进行搜索时，检索结果应该类似下图所示：
 
2)编写程序，使用QueryParser实现与上题相同的功能
代码展示：
/*
	 * 组合条件查询
	 */

	public void booleanQuery(String name,String parameter) throws ParseException, IOException{
		BooleanQuery.Builder booleanBuilder = new BooleanQuery.Builder();
		// 手机名进行查询
		TermQuery nameQuery = new TermQuery(new Term("NAME", name));
		// 对参数进行查询
		QueryParser parameterParser = new QueryParser("PARAMETER", analyzer);	//初始化分词器
		Query parameterQuery = parameterParser.parse(parameter);
		// 合并
		booleanBuilder.add(nameQuery,Occur.MUST);
		booleanBuilder.add(parameterQuery,Occur.MUST);
		BooleanQuery bq = booleanBuilder.build();
		// 进行查询
		TopDocs results = searcher.search(bq,hitsCount);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		System.out.println("一共查询到【" + numTotalHits + "】部手机符合要求：");
		int number = Math.min(hits.length, hitsCount);
		for (int i = 0; i < number; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String cellphoneName = doc.get("NAME");
			String url = doc.get("URL");
			System.out.println(url + "/" + cellphoneName);
			// System.out.println("doc=" + hits[i].doc + " score=" +
			// hits[i].score);
		}
	}
3．实验结果
 
 
四、	实验总结
此次实验收获颇丰。
首先是在陈老师的帮助下，重新把爬虫的架构搭了一遍，再次熟悉了WebMagic的使用，其中subPageProcessor的使用印象深刻。
接着就是实际动手操作了利用pipeline向数据库写数据。
这其中有几个比较好的思路，一个是
	利用fastjson 一键生成一个bean类，免去了一步一步添加数据
//商品评论概要内容
		JSONObject obj = JSON.parseObject(page.getRawText());
		JSONArray arr = obj.getJSONArray("CommentsCount");
CommentsCount comment = arr.getObject(0, CommentsCount.class);

	然后就是利用PageType防止网页错位。
/**
		 * 为各子页面添加Pipeline处理方法
		 */
		spider.addPipeline(new ProductInfoPipeline(PageType.PRODUCT_INFO)); 
// 添加具体商品PipeLine处理方法
		spider.addPipeline(new PricePipeline(PageType.PRODUCT_PRICE));
// 添加具体的价格Pipeline处理方法
		spider.addPipeline(new CommentSummaryPipeline(PageType.PRODUCT_COMMENT_SUMMARY));
// 商品评论概要Pipeline处理办法

-----------------------------------------------------------------------------------------------------------------------------------------------------------
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

	用一个抽象类做父类，然后pipeline继承，就不用多次进行配置了
	HanLP分词结合lucene很好用。

这次试验学到了不少东西，多亏了陈老师。谢谢啦。
后期还要进行相当多的内容拓展，正真把它变成一个搜索引擎。
