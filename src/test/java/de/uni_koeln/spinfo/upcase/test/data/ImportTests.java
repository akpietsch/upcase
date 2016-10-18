package de.uni_koeln.spinfo.upcase.test.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import de.uni_koeln.spinfo.upcase.model.RegistrationForm;
import de.uni_koeln.spinfo.upcase.mongodb.data.document.future.Box;
import de.uni_koeln.spinfo.upcase.mongodb.data.document.future.Collection;
import de.uni_koeln.spinfo.upcase.mongodb.data.document.future.Page;
import de.uni_koeln.spinfo.upcase.mongodb.data.document.future.UpcaseUser;
import de.uni_koeln.spinfo.upcase.mongodb.data.document.future.Word;

public class ImportTests {

	@BeforeClass
	public static void init() throws IOException {

		Pattern bboxPattern = Pattern.compile("\\d{1,4}\\s\\d{1,4}\\s\\d{1,4}\\s\\d{1,4}");

		Collection collection = new Collection("My Test Collection",
				new UpcaseUser(new RegistrationForm("Mihail", "Atanassov", "matanass@uni-koeln.de",
						"Department of Computational Linguistics", "secret123", "secret123")));

		List<Page> pages = new ArrayList<>();

		File[] data = new File("OCR").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".html");
			}
		});

		int pageCount = 0;
		for (File file : data) {

			String imgUrl = file.getName().replace(".html", ".png");
			Page page = new Page(imgUrl, pageCount, 0);
			List<Word> words = new ArrayList<>();

			Document hOCR = Jsoup.parse(file, "UTF-8"); // hOCR document
			Elements ocrPage = hOCR.select(".ocr_page"); // Page
			Elements ocrPars = ocrPage.select(".ocr_par"); // Paragraphs in Page

			for (Element parElement : ocrPars) {

				// Elements ocrLine = ocrPars.select(".ocr_line"); // Lines in
				// Paragraph
				Elements ocrxWords = parElement.select(".ocrx_word"); // Words
																		// in
																		// Line

				for (Element wordElement : ocrxWords) {

					String title = wordElement.attr("title");
					Matcher matcher = bboxPattern.matcher(title);
					matcher.find();

					String[] bBox = matcher.group(0).trim().split(" ");

					String token = wordElement.text();
					if (token.isEmpty())
						token = wordElement.select("span").text();

					if (!token.isEmpty()) {

						// System.out.printf("%s x1(%s) x2(%s) y1(%s) y2(%s)
						// \n", token.toUpperCase(), bBox[0], bBox[2], bBox[1],
						// bBox[3]);

						int x1 = Integer.parseInt(bBox[0]);
						int x2 = Integer.parseInt(bBox[2]);
						int y1 = Integer.parseInt(bBox[1]);
						int y2 = Integer.parseInt(bBox[3]);

						words.add(new Word(token, new Box(x1, x2, y1, y2)));
					}
				}
				page.setWords(words);
			}
			pages.add(page);
			pageCount++;
		}
		collection.setPages(pages);
		collection.setContributable(true);
		System.out.println(collection);
		for (Page page : collection.getPages()) {
			System.out.println(page);
		}
	}

	@Test
	public void test() {

	}

}
