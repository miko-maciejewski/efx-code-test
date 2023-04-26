package prv.maciejewski.fxpricefeed.efxcodetest.services;

import java.math.BigDecimal;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import prv.maciejewski.fxpricefeed.efxcodetest.model.FxPrice;

/**
 * Simple service to calculate new FxPrice on base of given commission Values of
 * commission for bid and ask are hardcoded as constants for case of the test
 * 
 * @author Mikolaj Maciejewski
 *
 */
@Service
public class FxPriceMarginCalculatorService {
	// commission constants for BigDecimal
	// -0.1% on bid
	// +0.1% on ask
	// new price have new ID with "R" prefix addeded to incoming - date is unchanged
	// multiply is without math context for simplicity

	final static String BID_MARGIN = "-0.001"; // -0.1%
	final static String ASK_MARGIN = "0.001"; // 0.1%
	final static String NEW_PRICE_ID_PREFIX = "R";

	// declare bid and ask multipliers for new value calculation
	final static BigDecimal BID_MULTIPLIER = (new BigDecimal("1.000")).add(new BigDecimal(BID_MARGIN));
	final static BigDecimal ASK_MULTIPLIER = (new BigDecimal("1.000")).add(new BigDecimal(ASK_MARGIN));

	/**
	 * Define lambda function <T,R> to calculate price with margins
	 * 
	 * T - FxPrice - base price for calculation R - FxPrice - result price with
	 * calculated new margin and ID
	 */
	public static final Function<FxPrice, FxPrice> calculatePriceWithMargin = price -> {
		return FxPrice.builder().priceId(NEW_PRICE_ID_PREFIX.concat(price.getPriceId()))
				.pairSymbol(price.getPairSymbol()).bidPrice(price.getBidPrice().multiply(BID_MULTIPLIER))
				.askPrice(price.getAskPrice().multiply(ASK_MULTIPLIER)).dateTime(price.getDateTime()).build();
	};

	/**
	 * Service function for calculate new price with margin. It reuse defined lambda
	 * function
	 * 
	 *
	 * @param fxPrice - base value for price
	 * 
	 * @return result price with calculated margin
	 */
	public FxPrice calculateNewPrice(FxPrice fxPrice) {
		return calculatePriceWithMargin.apply(fxPrice);
	}

}
