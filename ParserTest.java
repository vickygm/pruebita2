package coolc.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;
import coolc.compiler.autogen.lexer.LexerException;
import coolc.compiler.autogen.parser.ParserException;

public class ParserTest {
	public final String outputPath = "target/outputParser/";
	public final String inputPath = "src/test/resources/parser/input/";
	public final String refPath = "src/test/resources/parser/reference/";
	public final String cases = "src/test/resources/parser/";
	
	@BeforeTest
	public void createOutput() {
		File output = new File(outputPath);
		output.mkdirs();
	}
	
	public Iterator<Object[]> readCases(String file) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(cases + file), ';');
		ArrayList<Object[]> list = new ArrayList<Object[]>();

		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			list.add( new Object[] { nextLine[0], nextLine[2] } );
		}
		
		reader.close();
		return list.iterator();		
	}

	@DataProvider(name = "filesProviderOk")
	public Iterator<Object[]> readCasesOk() throws IOException {
		return readCases("cases.ok");
	}

	@DataProvider(name = "filesProviderBad")
	public Iterator<Object[]> readCasesBad() throws IOException {
		return readCases("cases.bad");
	}

	
	@Test(dataProvider = "filesProviderOk")
	public void testOk(String file, String testName) throws LexerException, IOException, ParserException {
		PrintStream out = new PrintStream(new FileOutputStream(outputPath + file + ".out"));
		new Main().parseCheck(inputPath + file, out);
		
		Iterator<String> refLines = FileUtils.readLines(new File(refPath + file + ".out")).iterator();
		Iterator<String> outLines = FileUtils.readLines(new File(outputPath + file + ".out")).iterator();
		
		
		while(true) {				
			if (!refLines.hasNext()) break;
			String r = refLines.next();
			String o = outLines.next();
			assert r.compareTo(o) == 0 : String.format("%s -> %s, reference=[%s], output=[%s]", file, testName, r, o);
		}
		
	}

	/*
	 *  Esperamos una parse exception en estos casos
	 */
	@Test(dataProvider = "filesProviderBad", expectedExceptions = {ParserException.class})
	public void testBad(String file, String testName) throws LexerException, IOException, ParserException {
		PrintStream out = new PrintStream(new FileOutputStream(outputPath + file + ".out"));
		new Main().parseCheck(inputPath + file, out);
	}

}
