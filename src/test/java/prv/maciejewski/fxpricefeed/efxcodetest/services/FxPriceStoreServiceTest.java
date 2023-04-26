package prv.maciejewski.fxpricefeed.efxcodetest.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import prv.maciejewski.fxpricefeed.efxcodetest.model.FxPrice;

@SpringBootTest
@DisplayName("Testing price storage service.")
public class FxPriceStoreServiceTest {
	
	@Autowired
	FxPriceStoreService fxPriceStoreService;
	
	@BeforeEach
	void clearStorage() {
		fxPriceStoreService.removeAll();
	}
	
	@Test
	void testPriceStorage() {
		FxPrice sourcePrice = new FxPrice("1002", "CUR/CRR", new BigDecimal("1000.000"), new BigDecimal("2000.000"),
				LocalDateTime.of(2023, 4, 25, 13, 15, 56, 750000000));

		fxPriceStoreService.updateFxPrice(sourcePrice);
		
		Optional<FxPrice> testPrice = fxPriceStoreService.getFxPriceByPairSymbol("CUR/CRR");
		
		assertNotNull(testPrice);
		assertTrue(testPrice.isPresent());
		assertEquals(testPrice.get().getPriceId(), "1002", "Price ID is diffrent than expected.");
		assertEquals(testPrice.get().getPairSymbol(), "CUR/CRR", "Pair symbol is not right.");
		assertTrue(testPrice.get().getAskPrice().equals(new BigDecimal("2000.000")), "Ask price is wrong.");
		assertTrue(testPrice.get().getBidPrice().equals(new BigDecimal("1000.000")), "Bid  price is wrong.");

		FxPrice sourcePrice2 = new FxPrice("1005", "CUR/CRR", new BigDecimal("1024.000"), new BigDecimal("2048.000"),
				LocalDateTime.of(2023, 4, 25, 13, 15, 56, 750000000));
		
		fxPriceStoreService.updateFxPrice(sourcePrice2);
		
		Optional<FxPrice> testPrice2 = fxPriceStoreService.getFxPriceByPairSymbol("CUR/CRR");
		
		assertNotNull(testPrice2);
		assertTrue(testPrice2.isPresent());
		assertEquals(testPrice2.get().getPriceId(), "1005", "Price ID is diffrent than expected.");
		assertEquals(testPrice2.get().getPairSymbol(), "CUR/CRR", "Pair symbol is not right.");
		assertTrue(testPrice2.get().getAskPrice().equals(new BigDecimal("2048.000")), "Ask price is wrong in second test value.");
		assertTrue(testPrice2.get().getBidPrice().equals(new BigDecimal("1024.000")), "Bid  price is wrong.");
	}
}
