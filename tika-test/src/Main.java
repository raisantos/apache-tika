import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
//import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

@SuppressWarnings("deprecation")
public class Main {

	protected static final int MAXIMUM_TEXT_CHUNK_SIZE = 5;

	public static void main(String[] args) throws IOException, SAXException, TikaException {
		//String string = new Main().parseToStringExample();
		//System.out.println(string);
		
		//String parseAuto = new Main().parseExample();
		//System.out.println(parseAuto);
		
		String plainText = new Main().parseToPlainText();
		System.out.println(plainText);
		
		//String html = new Main().parseToHTML();
		//System.out.println(html);
	
		//String htmlBody = new Main().parseBodyToHTML();
		//System.out.println(htmlBody);
		
		/*AutoDetectParser parser = new AutoDetectParser();
        ContentHandler textHandler = new BodyContentHandler();
        Metadata xmetadata = new Metadata();
        try  (InputStream stream = TikaInputStream.get(new URL("https://www.vivaolinux.com.br/topico/Java/Executar-arquivos-JAR"))){
            parser.parse(stream, new BoilerpipeContentHandler(textHandler), xmetadata);
            System.out.println("text:\n" + textHandler.toString());
        }*/
		
		//String web = new Main().parseToHTMLFromWeb();
		//System.out.println(web);
		
		//String part = new Main().parseOnePartToHTML();
		//System.out.println(part);
		
		/*List<String> chunks = new Main().parseToPlainTextChunks();
		System.out.println(chunks.size());
		for (String string : chunks) {
			System.out.println("*" + string + "!");
		}*/
		
		String language = new Main().identifyLanguage(plainText);
		System.out.println(language);
	}
	
	public String parseToStringExample() throws IOException, SAXException, TikaException {
	    Tika tika = new Tika();
	    try (InputStream stream = Main.class.getResourceAsStream("/resources/test.doc")) {
	        return tika.parseToString(stream);
	    }
	}
	
	public String parseExample() throws IOException, SAXException, TikaException {
	    AutoDetectParser parser = new AutoDetectParser();
	    BodyContentHandler handler = new BodyContentHandler();
	    Metadata metadata = new Metadata();
	    try (InputStream stream = Main.class.getResourceAsStream("/resources/test.doc")) {
	        parser.parse(stream, handler, metadata);
	        return handler.toString();
	    }
	}
	
	public String parseToPlainText() throws IOException, SAXException, TikaException {
	    BodyContentHandler handler = new BodyContentHandler();
	 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    try (InputStream stream = Main.class.getResourceAsStream("/resources/test.pdf")) {
	        parser.parse(stream, handler, metadata);
	        return handler.toString();
	    }
	}
	
	public String parseToHTML() throws IOException, SAXException, TikaException {
	    ContentHandler handler = new ToXMLContentHandler();
	 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    try (InputStream stream = Main.class.getResourceAsStream("/resources/test.pdf")) {
	    	parser.parse(stream, handler, metadata);
	        return handler.toString();
	    }
	}
	
	//não funcionando
	/*public String parseBodyToHTML() throws IOException, SAXException, TikaException {
	    ContentHandler handler = new BodyContentHandler(new ToXMLContentHandler());
	 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    try (InputStream stream = Main.class.getResourceAsStream("/resources/test.pdf")) {
	        parser.parse(stream, handler, metadata);
	        return handler.toString();
	    }
	}*/

	public String parseToHTMLFromWeb() throws IOException, SAXException, TikaException {
	    ContentHandler handler = new ToXMLContentHandler();
	 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    try(InputStream stream = TikaInputStream.get(new URL("https://www.tecmundo.com.br/produto/130488-razer-blade-notebook-gamer.htm"))) {
	    	parser.parse(stream, handler, metadata);
	        return handler.toString();
	    }
	}
	
	public String parseOnePartToHTML() throws IOException, SAXException, TikaException {
	    // Only get things under html -> body -> div (class=header)
	    XPathParser xhtmlParser = new XPathParser("xhtml", XHTMLContentHandler.XHTML);
	    Matcher divContentMatcher = xhtmlParser.parse("/xhtml:html/xhtml:body/xhtml:div/descendant::node()");
	    ContentHandler handler = new MatchingContentHandler(new ToXMLContentHandler(), divContentMatcher);
	 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    try (InputStream stream = Main.class.getResourceAsStream("/resources/test2.doc")) {
	        parser.parse(stream, handler, metadata);
	        return handler.toString();
	    }
	}
	
	public List<String> parseToPlainTextChunks() throws IOException, SAXException, TikaException {
	    final List<String> chunks = new ArrayList<>();
	    chunks.add("");
	    ContentHandlerDecorator handler = new ContentHandlerDecorator() {
	        @Override
	        public void characters(char[] ch, int start, int length) {
	            String lastChunk = chunks.get(chunks.size() - 1);
	            String thisStr = new String(ch, start, length);
	 
	            if (lastChunk.length() + length > MAXIMUM_TEXT_CHUNK_SIZE) {
	                chunks.add(thisStr);
	            } else {
	                chunks.set(chunks.size() - 1, lastChunk + thisStr);
	            }
	        }
	    };
	 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    try (InputStream stream = Main.class.getResourceAsStream("/resources/test.pdf")) {
	        parser.parse(stream, handler, metadata);
	        return chunks;
	    }
	}
	
	public String identifyLanguage(String text) {
		LanguageIdentifier identifier = new LanguageIdentifier(text);
	    return identifier.getLanguage();
	}
}
