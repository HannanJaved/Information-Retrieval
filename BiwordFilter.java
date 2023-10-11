
import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class BiwordFilter extends TokenFilter {

	private CharTermAttribute charTermAttr;
	private static char[] buffer1;
	private static int length1;
	private static int loop=0;
	
	  protected BiwordFilter(TokenStream ts) {
	    super(ts);
	    this.charTermAttr = addAttribute(CharTermAttribute.class);
	  }

	  @Override
	  public boolean incrementToken() throws IOException {
		 
	    if (!input.incrementToken()) {
	    	buffer1=null;
	    	length1=0;
	    	loop=0;
	      return false;
	    }
	    if(loop==0) {
	    length1 = charTermAttr.length();
		buffer1 = charTermAttr.buffer().clone();
	    }
	    if(loop==0) {
	    	input.incrementToken();
	    }
		
	    int length2 = charTermAttr.length();
	    char[] buffer2 = charTermAttr.buffer().clone();
	    
	    int length=length1+length2+1;
	    char[] newBuffer = new char[length];
	    
	    for (int i = 0; i < length1+1; i++) {
	    	if(i<length1) {
	    		newBuffer[i] = buffer1[i];
	    	}else if(i==length1) {
	    		newBuffer[i] = ' ';
	    	}
	    }
	    
	    for(int i=0;i<length2;i++) {
	    	newBuffer[i+length1+1] = buffer2[i];
		}
	    charTermAttr.setEmpty();
	    charTermAttr.copyBuffer(newBuffer, 0, length);
	    loop++;
	    buffer1=buffer2.clone();
	    length1=length2;
	    return true;
	  }

}/*
@Override
public boolean incrementToken() throws IOException {
  if (!input.incrementToken()) {
    return false;
  }

  int length = charTermAttr.length();
  char[] buffer = charTermAttr.buffer();
  char[] newBuffer = new char[length];
  System.out.print("newbuffer: ");
  for (int i = 0; i < length; i++) {
    newBuffer[i] = buffer[length - 1 - i];
    System.out.print(newBuffer[i]);
    if(i==length-1) {
  	  System.out.println();
    }
  }
  charTermAttr.setEmpty();
  charTermAttr.copyBuffer(newBuffer, 0, newBuffer.length);
  
  return true;
}

	  @Override
	  public boolean incrementToken() throws IOException {
	    if (!input.incrementToken()) {
	      return false;
	    }
	    //int j=0;
	    int length1 = charTermAttr.length();
	    char[] buffer1 = charTermAttr.buffer();
	    /*for (int i = 0; i < length1;i++) {
	    	System.out.println(i + " luuk: " + buffer1[i]);
	    }*//*
	    char[] newBuffer = new char[100];
	    for (int i = 0; i < length1+1; i++) {
	    	if(i<length1) {
	    		newBuffer[i] = buffer1[i];
	    	}else if(i==length1) {
	    		newBuffer[i] = ' ';
	    	}
	    }
	    //if(loop==0) {
	    	input.incrementToken();
	    //}
	    int length2 = charTermAttr.length();
	    char[] buffer2 = charTermAttr.buffer();
	    
	    int length=length1+length2+1;

	    
	    /*for (int i = 0; i < length2;i++) {
	    	System.out.println(i + " loook: " + buffer2[i]);
	    }*/
	    
	    /*
	    for(int i=0;i<length2;i++) {
	    	newBuffer[i+length1+1] = buffer2[i];
	    	//j++;
		}
	    charTermAttr.setEmpty();
	    charTermAttr.copyBuffer(newBuffer, 0, length);
	    //j=0;
	    loop++;
	    return true;
	  }*/