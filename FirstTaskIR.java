
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


public class FirstTaskIR {
	//part a)
	/**
	 * Tokenizes a text with StandardTokenizer
	 * @param str a text that should be tokenized
	 * @return Tokens using StandardTokenizer
	 */
	public static List<String> standardTokenizer(String str) {
		List<String> result = new ArrayList<String>();
		
		try (StandardTokenizer stream= new StandardTokenizer()) {
		 	stream.setReader(new StringReader(str));
		 	stream.reset();
		    while(stream.incrementToken()) {
		    	result.add(stream.getAttribute(CharTermAttribute.class).toString());
		    }stream.close();
		  } catch (IOException e) {
		      new RuntimeException(e); 
		  }
		return result;
	}
	
	/**
	 * Tokenizes a text with WhitespaceTokenizer
	 * @param str a text that should be tokenized
	 * @return Tokens using WhitespaceTokenizer
	 */
	public static List<String> whiteSpaceTokenizer(String str) {
		List<String> result = new ArrayList<String>();
		
		try (WhitespaceTokenizer stream= new WhitespaceTokenizer()) {
		 	stream.setReader(new StringReader(str));
		 	stream.reset();
		    while(stream.incrementToken()) {
		    	result.add(stream.getAttribute(CharTermAttribute.class).toString());
		    }stream.close();
		  } catch (IOException e) {
		      new RuntimeException(e);  
		  }
		return result;
	}
	//b
	/**
	 * Tokenizes a text with StandardTokenizer and filters the tokens from the stopwords (was, is, in, to, be)
	 * @param str a text that should be tokenized and filterd
	 * @param caseSensitve if the filter should be case sensetive or not
	 * @return tokens without stopwords
	 */
	public static List<String> stopWordFilterStandard(String str, boolean caseSensitve) {
		List<String> result = new ArrayList<String>();
		CharArraySet stopWords=new CharArraySet(5, caseSensitve);
		stopWords.add("was");
		stopWords.add("is");
		stopWords.add("in");
		stopWords.add("to");
		stopWords.add("be");
		
		try (StandardTokenizer stream= new StandardTokenizer()) {
		 	stream.setReader(new StringReader(str));
		 	StopFilter stream2= new StopFilter(stream, stopWords);
		 	stream2.reset();
		    while(stream2.incrementToken()) {
		    	result.add(stream2.getAttribute(CharTermAttribute.class).toString());
		    }stream2.close();
		  } catch (IOException e) {
		      new RuntimeException(e);  
		  }
		return result;
	}
	 
	 
	 //c
	/**
	 * analyzes a text with a chosen analyzer
	 * @param str the text to be analyzed
	 * @param analyzer the chosen analyzer to analyze with
	 * @return the analyzed tokens
	 * @throws IOException not used
	 */
	public static List<String> analyze(String str, Analyzer analyzer) throws IOException{//instead of try catch in the other methods (alternative) with it need to close the stream
		 List<String> result = new ArrayList<String>();
		 TokenStream stream  = analyzer.tokenStream(null, new StringReader(str));
	     stream.reset();
	     while (stream.incrementToken()) {
	       result.add(stream.getAttribute(CharTermAttribute.class).toString());
	     }
		return result;
	}
	
	/**
	 * creates an analyzer that uses standardtokenizer, lowercaseFilter, stopwordFilter and PorterstemmerFilter
	 * @return an analyzer that uses standardtokenizer, lowercaseFilter, stopwordFilter and PorterstemmerFilter
	 * @throws IOException not reached
	 */
	public static Analyzer analyzer() throws IOException{
		Analyzer analyzer = CustomAnalyzer.builder()
				   .withTokenizer("standard")
				   .addTokenFilter("lowercase")
				   .addTokenFilter("stop", "ignoreCase", "false", "words", "stopwords.txt", "format", "wordset")
				   .addTokenFilter("porterstem")
				   .build();
		return analyzer;
	}
	
	public static void main(String[] args) throws IOException {
		if(args.length==0) {//Default sentence
			String sentence="Today is sunny. She is a sunny girl. To be or not to be. She is in Berlin today. Sunny Berlin! Berlin is always exciting!";

			System.out.println("The sentence to test on is: " + sentence + "\n" + "--------------------------------------------------------------------------------------" + "\n");
			
			//output part a
			List<String> standard = standardTokenizer(sentence);
			
			System.out.println("Part a): \nTokens with StandardTokenizer: \n" + standard + "\n" + "\n");
			
			List<String> whitespace = whiteSpaceTokenizer(sentence);
			 
			System.out.println("Tokens with WhitespaceTokenizer:\n" +whitespace+ "\n\nThe differnce between the two Tokenizers is that the White space Tokenizer will tokenize each Token as the whole things between the spaces for example keeping the . , ? ! with the actuall word as 1 token. like in our sentence Berlin! is a token using the whitespacetokenizer and using the standardtokenizer we would only get Berlin as the Token." + "\n" + "--------------------------------------------------------------------------------------" + "\n");
			
			
			//output part b
			List<String> result3 = stopWordFilterStandard(sentence,false);
			List<String> result4 = stopWordFilterStandard(sentence,true);
			
			System.out.println("Part b):\nStandardTokeniser and StopwordFilter with (“was”, “is”, “in”, “to”, “be”) as stop words:\nnot case sensetive:\n" +result3);
			System.out.println("\ncase sensetive:\n" +result4+ "\n" + "--------------------------------------------------------------------------------------" + "\n");
			
			//output part c
			List<String> result5 = analyze(sentence, analyzer());
			
			System.out.println("Part c): \nanalyzed with standardtokenizer, lowercaseFilter, stopwordFilter and PorterstemmerFilter:\n" +result5);
		}else {//explicit sentence from input
		
		String sentence="";
		for(int i=0;i<args.length;i++) {
			sentence+=args[i];
			if(i!= args.length-1) {
				sentence+=" ";
			}
		}

		System.out.println("The sentence to test on is: " + sentence + "\n" + "--------------------------------------------------------------------------------------" + "\n");
		
		//output part a
		List<String> standard = standardTokenizer(sentence);
		
		System.out.println("Part a): \nTokens with StandardTokenizer: \n" + standard + "\n" + "\n");
		
		List<String> whitespace = whiteSpaceTokenizer(sentence);
		 
		System.out.println("Tokens with WhitespaceTokenizer:\n" +whitespace+ "\n\nThe differnce between the two Tokenizers is that the White space Tokenizer will tokenize each Token as the whole things between the spaces for example keeping the . , ? ! with the actuall word as 1 token. like in our sentence Berlin! is a token using the whitespacetokenizer and using the standardtokenizer we would only get Berlin as the Token." + "\n" + "--------------------------------------------------------------------------------------" + "\n");
		
		
		//output part b
		List<String> result3 = stopWordFilterStandard(sentence,false);
		List<String> result4 = stopWordFilterStandard(sentence,true);
		
		System.out.println("Part b):\nStandardTokeniser and StopwordFilter with (“was”, “is”, “in”, “to”, “be”) as stop words:\nnot case sensetive:\n" +result3);
		System.out.println("\ncase sensetive:\n" +result4+ "\n" + "--------------------------------------------------------------------------------------" + "\n");
		
		//output part c
		List<String> result5 = analyze(sentence, analyzer());
		
		System.out.println("Part c): \nanalyzed with standardtokenizer, lowercaseFilter, stopwordFilter and PorterstemmerFilter:\n" +result5);
	
		}
	}
}
