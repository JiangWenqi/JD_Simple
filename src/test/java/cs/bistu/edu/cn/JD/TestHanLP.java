package cs.bistu.edu.cn.JD;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import com.hankcs.lucene.HanLPAnalyzer;


public class TestHanLP {

	public static void main(String[] args) throws IOException {
		String indexDir = "./index";
		Analyzer analyzer = new HanLPAnalyzer();	
		File dir = new File(indexDir);
		Directory index = FSDirectory.open(dir.toPath());
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(index, config);
		Document doc = new Document();
		String pageID = "12345";
		String title = "标题也很帅";
		String text = "我觉得姜文奇特别帅.";//待索引的内容"
		String tag = "编程";
		doc.add(new TextField("TITLE", title,Store.YES));
		doc.add(new StringField("PAGETAG",tag,Store.YES));
		doc.add(new TextField("TEXT",text, Store.YES));
		iwriter.updateDocument(new Term("pageId",pageID), doc);
		iwriter.commit();
		System.out.println("123");
		iwriter.close();
	}

}
