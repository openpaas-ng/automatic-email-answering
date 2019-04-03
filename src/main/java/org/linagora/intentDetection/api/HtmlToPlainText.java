package org.linagora.intentDetection.api;


import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;


/**
 * HTML to plain-text. This example program demonstrates the use of jsoup to convert HTML input to lightly-formatted
 * plain-text. That is divergent from the general goal of jsoup's .text() methods, which is to get clean data from a
 * scrape.
 * <p>
 * Note that this is a fairly simplistic formatter -- for real world use you'll want to embrace and extend.
 * </p>
 * <p>
 * To invoke from the command line, assuming you've downloaded the jsoup jar to your current directory:</p>
 * <p><code>java -cp jsoup.jar org.jsoup.examples.HtmlToPlainText url [selector]</code></p>
 * where <i>url</i> is the URL to fetch, and <i>selector</i> is an optional CSS selector.
 * 
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class HtmlToPlainText {
    private static final String userAgent = "Mozilla/5.0 (jsoup)";
    private static final int timeout = 5 * 1000;

       
    private String formatTextResult(String text) {
    	String [] lines = text.replaceAll("[\\u00a0\\u202f ]+"," ").split("\n");
        StringBuffer newText = new StringBuffer(lines[0]);
        for(int i = 1; i<lines.length; i++) {
     	   String line = lines[i];
     	   boolean isBlank = line.matches(" +");
     	   boolean isNewLine = line.length() == 0;
     	   boolean startWithBlank = line.matches("^\\s.+");
     	   boolean endWithBlank = line.matches(".+\\s$");
//     	  System.out.println(line+ ">>> isBlank="+isBlank+", isNewLine="+isNewLine+", startWithBlank="+startWithBlank+", endWithBlank="+endWithBlank);
//     	  System.out.println(line.length()+">>>"+line);
     	   if(isNewLine) {
     		   newText.append("\n");
     	   }else if(isBlank) {
     		   newText.append(" ");
     	   }else if(startWithBlank) {
     		   newText.append(line);
     	   }else if (endWithBlank) {
     		   newText.append(line);
     	   }else {
     		   newText.append("\n"+line);
     	   }
        }
        StringBuffer cleanedText = new StringBuffer();
       for(String line: newText.toString().replaceAll(" +"," ").split("\n")) {
     	  cleanedText.append(line.trim()+"\n");
       }
       return cleanedText.toString();
    }
    
    /**
     * Format an Element to plain-text
     * @param element the root element to format
     * @return formatted text
     */
    public String getPlainText(Element element) {
        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor.traverse(formatter, element); // walk the DOM, and call .head() and .tail() for each node

        return formatTextResult(formatter.toString());
    }

    // the formatting rules, implemented in a breadth-first DOM traverse
    private class FormattingVisitor implements NodeVisitor {
       
        private StringBuilder accum = new StringBuilder(); // holds the accumulated text

        // hit when the node is first seen
        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode) {
            	            	
            	String nodeText = (((TextNode) node).getWholeText());
                     	
            	
            	append(nodeText); // TextNodes carry all user-readable text in the DOM.
            }
                
            else if (name.equals("li"))
                append("\n * ");
            else if (name.equals("dt"))
                append("  ");
            else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr", "div"))
                append("\n");
        }

        // hit when all of the node's children (if any) have been visited
        public void tail(Node node, int depth) {
            String name = node.nodeName();
            if (StringUtil.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5", "div"))
                append("\n");
            else if (name.equals("a"))
                append(String.format(" <%s>", node.absUrl("href")));
        }

        // appends text to the string builder with a simple word wrap method
        private void append(String text) {
        	
        	accum.append(text);
        }

        @Override
        public String toString() {
        	
            return accum.toString();
        }
    }
    
   
}