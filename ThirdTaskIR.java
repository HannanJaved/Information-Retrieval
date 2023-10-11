
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.store.RAMDirectory;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class ThirdTaskIR {
	static int docnum=0;
	static FieldType ft=new FieldType();
	private static void addDoc(IndexWriter w, String Main) throws IOException {
		Document doc = new Document();
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); //enable storing the reuired statistics
		ft.setStored(true);
		ft.setStoreTermVectors(true);
		ft.setStoreTermVectorPositions(true);
		ft.setStoreTermVectorPayloads(true);
		ft.setStoreTermVectorOffsets(true);
		doc.add(new Field("Main", Main, ft));
		w.addDocument(doc);
		docnum++;
		}
	
	public static Analyzer analyzer() throws IOException{
		Analyzer analyser = CustomAnalyzer.builder()
                .withTokenizer(StandardTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(BiwordFilterFactory.class)
                .build();
		return analyser;
	}
	
	
	public static List<String> analyze(String str, Analyzer analyzer) throws IOException{//instead of try catch in the other methods (alternative) with it need to close the stream
		 List<String> result = new ArrayList<String>();
		 TokenStream stream  = analyzer.tokenStream(null, new StringReader(str));
	     stream.reset();
	     while (stream.incrementToken()) {
	       result.add(stream.getAttribute(CharTermAttribute.class).toString());
	     }
	     stream.close();
	     stream.reset();
	     stream.end();
		return result;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		String sentence="Today is sunny. She is a sunny girl. To be or not to be. She is in Berlin today. Sunny Berlin! Berlin is always exciting!";
		List<String> result1 = analyze(sentence, analyzer());
		System.out.println("part a)\nToknize the following sentence using biword tokenizer:\n" + sentence + "\n" +"analyzed with standardtokenizer, lowercaseFilter, and biword tokenizer:\n" +result1+ "\n--------------------------------------------------------------------------------------" + "\n");
		System.out.println("part b)\nAssume a biword index. Give an example of a document which will be returned for a query of (New York University) but is actually a false positive which should not be returned:");
		System.out.println("Every Student in New York dreams of getting accepted in York University in Toronto.");
		List<String> result2 = analyze("Every Student in New York dreams of getting accepted in York University in Toronto.", analyzer());
		System.out.println("analyzed with standardtokenizer, lowercaseFilter, and biword tokenizer:\n" +result2+ "\n--------------------------------------------------------------------------------------" + "\n");
		
		Directory index = new RAMDirectory(); //makes a new directory in the ram for storing the index
		IndexWriterConfig config = new IndexWriterConfig(analyzer());
		IndexWriter writer = new IndexWriter(index, config); // making IndexWriter to add document to the index
		
		//adding docements to the directory (index) using IndexWriter by calling the method addDoc()
		addDoc(writer, "Every Student in New York dreams of getting accepted in York University in Toronto.");
		writer.close();
		
		String querystr1 = "New York University";
		Query q = new QueryParser("Main", analyzer()).parse(querystr1);
				
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);	//reader to read the index
		IndexSearcher searcher = new IndexSearcher(reader); 
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector); //search for the query and save all hits
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		System.out.println("query: " + querystr1+ "---> Analyzed: "+ analyze(querystr1, analyzer()));
		System.out.println();

		System.out.println("Query string: " + querystr1 );
		System.out.println("Found " + hits.length + " hits.");
		//print all the docs that have this query
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("Main"));
		}
		System.out.println("false positive");
	}	
}
