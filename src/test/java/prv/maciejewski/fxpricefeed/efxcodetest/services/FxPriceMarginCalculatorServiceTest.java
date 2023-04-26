package prv.maciejewski.fxpricefeed.efxcodetest.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import prv.maciejewski.fxpricefeed.efxcodetest.model.FxPrice;

@SpringBootTest
@DisplayName("Testing price recalculation service.")
public class FxPriceMarginCalculatorServiceTest {

	@Autowired
	FxPriceMarginCalculatorService fxPriceMarginCalculatorService;

	@Test
	void testPriceWithMarginCalculationLambda() {
		FxPrice sourcePrice = new FxPrice("1002", "CUR/CRR", new BigDecimal("1000.000"), new BigDecimal("2000.000"),
				LocalDateTime.of(2023, 4, 25, 13, 15, 56, 750000000));

		FxPrice marginPrice = FxPriceMarginCalculatorService.calculatePriceWithMargin.apply(sourcePrice);

		assertNotNull(marginPrice, "Result date is null");
		assertEquals(marginPrice.getPriceId(), "R1002", "Price ID is diffrent than expected.");
		assertEquals(marginPrice.getPairSymbol(), "CUR/CRR", "Pair symbol is not right.");
	
		
		assertThat(marginPrice.getAskPrice().compareTo(new BigDecimal("2002.000000"))).isEqualTo( 0 ).withFailMessage("Ask price misscalculated.");

		
		assertThat(marginPrice.getBidPrice().compareTo(new BigDecimal("999.000000"))).isEqualTo( 0 ).withFailMessage("Bid price misscalculated.");
		
		
		assertNotNull(marginPrice.getDateTime(), "Date is not assigned");
	}

	@Test
	void testPriceWithMarginCalculationService() {
		FxPrice sourcePrice = new FxPrice("1003", "XUR/CUR", new BigDecimal("450.00"), new BigDecimal("750.00"),
				LocalDateTime.of(2023, 4, 25, 13, 15, 56, 750000000));

		FxPrice marginPrice = fxPriceMarginCalculatorService.calculateNewPrice(sourcePrice);

		assertNotNull(marginPrice, "Result date is null");
		assertEquals(marginPrice.getPriceId(), "R1003", "Price ID is diffrent than expected.");
		assertEquals(marginPrice.getPairSymbol(), "XUR/CUR", "Pair symbol is not right.");
		assertTrue(marginPrice.getAskPrice().equals(new BigDecimal("750.75000")), "Ask price misscalculated.");
		assertTrue(marginPrice.getBidPrice().equals(new BigDecimal("449.55000")), "Bid price misscalculated.");
		assertNotNull(marginPrice.getDateTime(), "Date is not assigned");
	}

}
