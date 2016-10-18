package de.uni_koeln.spinfo.upcase.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import de.uni_koeln.spinfo.upcase.mongodb.data.document.future.Box;
import de.uni_koeln.spinfo.upcase.mongodb.data.document.future.Page;
import de.uni_koeln.spinfo.upcase.mongodb.data.document.future.Word;
import de.uni_koeln.spinfo.upcase.mongodb.repository.future.PageRepository;

@Component
public class PdfExport {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired 
	private HOCRParser hocrParser;
	
	@Autowired
	private PageRepository pageRepository;
	
	private Page page;
	private String token;
	int x1;
	int x2;
	int y1;
	int y2;
	float boxWidth;
	float boxHeight;
	private String src = "OCR/PPN345572629_0004-0007.png";
	float pointsPerInch = 72.0f;
	float lineWidth=0;
	
	
	public void exportToPDF() throws DocumentException, IOException {

		Image pageImage = Image.getInstance(src);
	//	Image pageImage = Image.g;

		
		float dotsPerPointX = pageImage.getDpiX() / pointsPerInch;
		float dotsPerPointY = pageImage.getDpiY() / pointsPerInch;
		float pageImagePixelHeight = pageImage.getHeight();

		Rectangle whitePage = new Rectangle(pageImage.getWidth() / dotsPerPointX,
				pageImage.getHeight() / dotsPerPointY);

		com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document(whitePage);

		PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream("sample.pdf"));

		pdfDocument.open();

		PdfContentByte cb = pdfWriter.getDirectContent();

		pageImage.scaleToFit(pageImage.getWidth() / dotsPerPointX, pageImage.getHeight() / dotsPerPointY);

		pageImage.setAbsolutePosition(0, 0);
		pdfWriter.getDirectContent().addImage(pageImage);
		whitePage.setBackgroundColor(BaseColor.WHITE);
		cb.rectangle(whitePage);
		BaseFont font = BaseFont.createFont("src/main/webapp/bootstrap/fonts/OldNewspaperTypes.ttf", "UTF-8", BaseFont.EMBEDDED);


		
		List<Word> words = page.getWords();
		for (Word word : words) {
			token = word.getToken();
			Box box = word.getBox();
			x1 = box.getX1();
			y1 = box.getY1();
			x2 = box.getX2();
			y2 = box.getY2();

			boxWidth = (x2 - x1) / dotsPerPointX;
			boxHeight = (y2 - y1) / dotsPerPointY;
			

			System.out.println(token + "-->" + box);

			cb.beginText();		
			
			boolean textScaled = false;					
			do {
				  lineWidth = font.getWidthPoint(token,boxHeight);
				 if(lineWidth < boxWidth){
					 textScaled = true;
				 } else {
					 boxHeight-=0.1f;
				 }
			} while (textScaled==false);
			
			cb.setFontAndSize(font, boxHeight);
			
			cb.moveText((float) ((x1) / dotsPerPointX), (float) ((pageImagePixelHeight
					
					- y2) / dotsPerPointY));
			cb.showText(token);
			System.out.println(cb);
			cb.endText();
		}
		pdfDocument.close();
	}
	
	
}
