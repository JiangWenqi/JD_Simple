package cn.edu.bistu.cs.lucene;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.hankcs.lucene.HanLPAnalyzer;

import cn.edu.bistu.cs.util.ConfigReader;
import cn.edu.bistu.cs.util.JDBC;

public class Lucene {
	// 日志文件
	private static final Logger log = Logger.getLogger(Lucene.class);
	// 初始化数据库连接
	private static JDBC jdbc = new JDBC();
	// IndexWriter：创建新索引、打开已有索引、从索引中添加、删除或更新⽂档
	private static IndexWriter writer = null;
	// 索引地址
	private static final String indexDir = ConfigReader.getConfig("INDEX_DIR");
	// 初始化wirter
	public Lucene() throws IOException, SQLException {
		File dir = new File(indexDir);
		Directory index = FSDirectory.open(dir.toPath());
		// HanLP分词 Analyzer
		Analyzer analyzer = new HanLPAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
	
		writer = new IndexWriter(index, config);
	}


	public void buildIndex() throws IOException, SQLException, ParseException {
		int count = 0;	//计数器
		// 多表连接查询
		String sql = "select ci.id, ci.name,ci.parameter,ci.url,cp.price,cc.commentcount,cc.goodrate,cc.createtime from cellphoneinfo ci join cellphoneprice cp on ci.id = cp.j_id join cellphonecomment cc on ci.id=cc.j_id";
		ResultSet cellphone = jdbc.select(sql);
		
		while (cellphone.next()) {
			String skuid = cellphone.getString("id");
			String name = cellphone.getString("name");
			String parameter = cellphone.getString("parameter");
			String url = cellphone.getString("url");
			double price = cellphone.getDouble("price");
			int commentCount = cellphone.getInt("commentcount");
			double goodRate = cellphone.getDouble("goodrate");
			// 对时间进行格式化
			String time = cellphone.getString("createtime");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = format.parse(time);
			long createTime = date.getTime() / 1000;
			
			
			Document doc = new Document();
			// Field的子类用法：查官方文档
			doc.add(new StringField("SKUID", skuid, Store.YES));
			doc.add(new TextField("NAME", name, Store.YES));
			doc.add(new TextField("PARAMETER", parameter, Store.YES));
			doc.add(new StoredField("URL", url));
			doc.add(new DoublePoint("PRICE", price));
			doc.add(new IntPoint("COMMENTCOUNT", commentCount));
			doc.add(new DoublePoint("GOODRATE", goodRate));
			doc.add(new LongPoint("CREATETIME", createTime));
				
			writer.updateDocument(new Term("SKUID", skuid), doc);
			writer.commit();
			count++;
			log.info("第"+count+"部手机【"+skuid+"】创建索引成功");
		}
		writer.close();
		jdbc.close();
	}

	public static void main(String[] args) throws IOException, SQLException, ParseException {
		Lucene lucene = new Lucene();
		lucene.buildIndex();
	}
}
