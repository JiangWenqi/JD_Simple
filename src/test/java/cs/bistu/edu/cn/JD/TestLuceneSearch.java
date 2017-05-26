package cs.bistu.edu.cn.JD;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

public class TestLuceneSearch {
	private static IndexSearcher searcher = null;

	public TestLuceneSearch() throws IOException {
		if (searcher == null) {
			File file = new File("./index");
			Directory dir = FSDirectory.open(file.toPath());
			IndexReader reader = DirectoryReader.open(dir);
			System.out.println();
			System.out.println(reader.getDocCount("PAGEID"));
			System.out.println(reader.getDocCount("TITLE"));
			int docCount = reader.getDocCount("PAGEID");
			Document doc = null;
			Set<String> fields = new LinkedHashSet<>();
			fields.add("PAGEID");
			fields.add("TITLE");
			for (int i = 0; i < docCount; i++) {
				doc = reader.document(i, fields);
				Iterator<String> ite = fields.iterator();
				System.out.print(i + ":");
				while (ite.hasNext()) {
					System.out.print(doc.get(ite.next()) + " ");
				}
				System.out.println();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new TestLuceneSearch();
	}
}