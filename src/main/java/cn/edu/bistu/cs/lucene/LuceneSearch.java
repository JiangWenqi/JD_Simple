package cn.edu.bistu.cs.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.hankcs.lucene.HanLPAnalyzer;

import cn.edu.bistu.cs.util.ConfigReader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LuceneSearch {
	// 日志文件
	private static final Logger log = Logger.getLogger(LuceneSearch.class);
	// 索引目录
	private static final String index = ConfigReader.getConfig("INDEX_DIR");
	// 采用HanLPAnalyzer进行分词
	private static Analyzer analyzer = new HanLPAnalyzer();
	// 需要查询的数目
	private final static int hitsCount = 20;

	private static IndexReader reader;
	private static IndexSearcher searcher;

	// 初始化IndexReader searcher等参数
	public LuceneSearch() throws IOException {
		File file = new File(index);
		Directory dir = FSDirectory.open(file.toPath());
		reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
	}

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
	
	public static void main(String[] arg) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		LuceneSearch lucene = new LuceneSearch();
		// 实验一：获取总文档数
		System.out.println("总文档数:" + lucene.getDocCount() + "\n");
		// 实验二：查询手机名（链接）
		System.out.println("---------------------------根据手机名查询---------------------");
		System.out.print("请输入手机名: ");
		String name = in.readLine();
		name = name.trim();
		lucene.searchByName(name);
		// 实验三：根据价格区间查询
		System.out.println("----------------------------根据价格区间查询：-----------------------");
		System.out.print("请输入最低价格：");
		double lowPrice = Double.valueOf(in.readLine());
		System.out.print("请输入最高价格：");
		double highPrice = Double.valueOf(in.readLine());
		lucene.searchByPriceRange(lowPrice, highPrice);
		// 实验四：综合查询
		System.out.println("--------------------------------综合查询--------------------------------");
		System.out.print("请输入手机名：");
		String cellphoneName = in.readLine();
		System.out.print("请输入参数相关信息：");
		String parameter = in.readLine();
		lucene.booleanQuery(cellphoneName, parameter);
		in.close();
		reader.close();
	}

}