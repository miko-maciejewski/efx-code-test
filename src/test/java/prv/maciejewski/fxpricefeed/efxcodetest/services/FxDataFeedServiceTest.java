package prv.maciejewski.fxpricefeed.efxcodetest.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import prv.maciejewski.fxpricefeed.efxcodetest.model.FxPrice;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@DisplayName("Testing CSV parsing module.") 
class FxDataFeedServiceTest {
	
    @Autowired
    private FxDataFeedService fxDataFeedService;
    
    @Autowired
    private FxPriceStoreService fxDaFxPriceStoreService;

	@Test
	void testParseCSVLineWithEmptyString() {
		List<String> csvRecordColumns = fxDataFeedService.parseCSVLine("");
		
		List<String> expectedColumns = Arrays.asList("");
		
		assertThat(expectedColumns.toArray()).isEqualTo(csvRecordColumns.toArray());
	}
	
	@Test
	void testParseCSVLineWith1Column() {
		List<String> csvRecordColumns = fxDataFeedService.parseCSVLine("column_name");
		
		List<String> expectedColumns = Arrays.asList("column_name");
		
		assertThat(expectedColumns.toArray()).isEqualTo(csvRecordColumns.toArray());
	}
	
	@Test
	void testParseCSVLineWith2Columns() {
		List<String> csvRecordColumns = fxDataFeedService.parseCSVLine("column1,column2");
		
		List<String> expectedColumns = Arrays.asList("column1", "column2");
		
		assertThat(expectedColumns.toArray()).isEqualTo(csvRecordColumns.toArray());
	}
	
	@Test
	void testParseCSVLineWithColumnsWithSpaces() {
		List<String> csvRecordColumns = fxDataFeedService.parseCSVLine("column1 , column2 ");
		
		List<String> expectedColumns = Arrays.asList("column1 ", " column2 ");
		
		assertThat(expectedColumns.toArray()).isEqualTo(csvRecordColumns.toArray());
	}
	
	@Test
	void testParseCSVLineWithQuotedColumns() {
		List<String> csvRecordColumns = fxDataFeedService.parseCSVLine("\"column1 \",\" column2 \"");
		
		List<String> expectedColumns = Arrays.asList("column1 ", " column2 ");
		
		assertThat(expectedColumns.toArray()).isEqualTo(csvRecordColumns.toArray());
	}
	
	@Test
	void testParseCSVLineWithQuotedColumnsAndQuotesInside() {
		List<String> csvRecordColumns = fxDataFeedService.parseCSVLine("\"column1 \"\"\",\"\"\"column2\"");
		
		List<String> expectedColumns = Arrays.asList("column1 \"", "\"column2");
		
		assertThat(expectedColumns.toArray()).isEqualTo(csvRecordColumns.toArray());
	}

	@Test
	void testSplitMessage2Lines() {
		List<String> splittedLines = fxDataFeedService.splitMessage2Lines("test line 1\ntest line2");
		List<String> controlTest = Arrays.asList("test line 1", "test line2");
		assertThat(splittedLines.toArray()).isEqualTo(controlTest.toArray());

	}
	
	@Test
	void testSplitMessageLinesWithMixedEOLs() {
		List<String> splittedLines = fxDataFeedService.splitMessage2Lines("test line 1\ntest line2\rLINE3\r\nLIne4\n\rLine 5");
		List<String> controlTest = Arrays.asList("test line 1", "test line2", "LINE3", "LIne4", "Line 5");
		assertThat(splittedLines.toArray()).isEqualTo(controlTest.toArray());

	}

	@Test
	void testParseSingleLineFxPrice() {
		List<String> fxPriceCols = fxDataFeedService.parseCSVLine("106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001");
		FxPrice testFxPrice = FxPrice.transformStrings2FxPrice.apply(fxPriceCols);
		assertNotNull(testFxPrice);
		assertEquals(testFxPrice.getPairSymbol(),"EUR/USD");
		assertEquals(testFxPrice.getAskPrice(),new BigDecimal("1.2000"));
	}
	
	@Test
	void testCSVSingleMessage() {
		fxDataFeedService.onMessage("107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002");
		
		Optional<FxPrice> testFxPrice = fxDaFxPriceStoreService.getFxPriceByPairSymbol("EUR/JPY");
		assertNotNull(testFxPrice);
		assertTrue(testFxPrice.isPresent());
		assertEquals(testFxPrice.get().getPairSymbol(),"EUR/JPY");
		assertThat(testFxPrice.get().getAskPrice().compareTo(new BigDecimal("120.0199"))).isEqualTo( 0 );
	}
	
	@Test
	void testMessageDateCheck() {
		fxDataFeedService.onMessage("107, EUR/JPY, 119.60,119.86,01-06-2020 12:01:02:002\n109, EUR/JPY, 119.50,119.80,01-06-2020 11:59:11:001");
		
		Optional<FxPrice> testFxPrice = fxDaFxPriceStoreService.getFxPriceByPairSymbol("EUR/JPY");
		assertNotNull(testFxPrice);
		assertTrue(testFxPrice.isPresent());
		assertEquals(testFxPrice.get().getPairSymbol(),"EUR/JPY");
		assertThat(testFxPrice.get().getAskPrice().compareTo(new BigDecimal("119.97986"))).isEqualTo( 0 );
	}
	
	@Test
	void testOnMessage() {
		fxDataFeedService.onMessage("106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001");

	}

}
