import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
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
import org.apache.lucene.util.BytesRef;

public class SecondTaskIR {
	//a)
	static int docnum=0;
	static FieldType ft=new FieldType();
	
	
	
	
	// //adding docements to the directory (index) using IndexWriter
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
	
	
	public static void main(String[] args) throws IOException, ParseException {
		//analyzer only for lowercase and none letters characters
		Analyzer analyzer = CustomAnalyzer.builder()
				   .withTokenizer("standard")
				   .addTokenFilter("lowercase")
				   .build();
		
		Directory index = new RAMDirectory(); //makes a new directory in the ram for storing the index
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(index, config); // making IndexWriter to add document to the index
		
		//adding docements to the directory (index) using IndexWriter by calling the method addDoc()
		addDoc(writer, "Today is sunny.");
		addDoc(writer, " She is a sunny girl.");
		addDoc(writer, "To be or not to be.");
		addDoc(writer, "She is in Berlin today.");
		addDoc(writer, "Sunny Berlin sunny!");
		addDoc(writer, "Berlin is always exciting!");
		writer.close();
		
		
		// query for part a) c)
		String querystr1 = "sunny AND excited";
		Query q = new QueryParser("Main", analyzer).parse(querystr1);
		
		
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);	//reader to read the index
		IndexSearcher searcher = new IndexSearcher(reader); 
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector); //search for the query and save all hits
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		System.out.println("a)");

		System.out.println("Query string: " + querystr1 );
		System.out.println("Found " + hits.length + " hits.");
		//print all the docs that have this query
		for (int i = 0; i < hits.length; ++i) {
		int docId = hits[i].doc;
		Document d = searcher.doc(docId);
		System.out.println((i + 1) + ". " + d.get("Main"));
		}
		System.out.println("--------------------------------------------------------------------------");
		System.out.println("b)");


		
		//b)
		String querystr2 = "sunny";
		String querystr3 = "to";
		System.out.println("format: [tokenname:total frequency:doc frequency]->[docid:frequency:[positions]]->[docid:frequency:[positions]] \n");
		stats(querystr2, reader);
		stats(querystr3, reader);
				
	}
	
	
	
	
	// a method that shows the (total frequency, doc frequency, docid, frequency in a doc and positions)
		public static void stats(String querystr, IndexReader reader) throws IOException {
			Term termInstance = new Term("Main", querystr);     //term                         
			long termFreq = reader.totalTermFreq(termInstance);//total frequency, doc frequency
			long docCount = reader.docFreq(termInstance);
			System.out.print("["+querystr+":"+termFreq+":"+docCount+"]->["); 

			//iterate over all docs and create in each loop an itarator to iterate over all terms in a doc
	        for(int i=0;i<docnum;i++) {
	        	Terms termVector1 = reader.getTermVector(i, "Main");
		        TermsEnum itr1 = termVector1.iterator();
		        BytesRef term1 = null;
		        PostingsEnum p = null;
		        //iterate over all terms in a doc and get the frequency of the chosen term in a doc and its positions
		        while ((term1 = itr1.next()) != null) { 
		        	p = itr1.postings(p, PostingsEnum.ALL);
		        	if(term1.equals(termInstance.bytes())) {
		        		p.nextDoc();
		        		long termFreq1 = itr1.totalTermFreq();  
		        		for(int k=0;k<termFreq1;k++) {
		        			final int pos = p.nextPosition();
		        			System.out.print(i+":"+termFreq1+":["+pos+"]]");
		        			if(k>0 && k==termFreq1-1) {}else {
			        			System.out.print("->[");
		        			}
		        		}
		        		}
	        }
		        
		        }
	        System.out.println();
		}
}
		
