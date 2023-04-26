package prv.maciejewski.fxpricefeed.efxcodetest.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Slf4j
public class FxPrice {

	final public static String UTC_TZ = "UTC";
	final public static ZoneId UTC_ZONE_ID = ZoneId.of(UTC_TZ);
	final public static DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("d-MM-uuuu HH:mm:ss:SSS").withZone(UTC_ZONE_ID);


	// id of
	String priceId;

	// currency pair symbols to deliver
	String pairSymbol;

	// bid price for the currency pair
	BigDecimal bidPrice;

	// ask price for the currency pair
	BigDecimal askPrice;

	// local date time in GMT
	LocalDateTime dateTime;

	/**
	 * Transforms and verify list of strings to FxPrice. 
	 * It is critical function of flow.
	 * Meaning of columns by list index:
	 * 
	 * columns[0] - id 
	 * columns[1] - pairSymbol 
	 * columns[2] - bidPrice 
	 * columns[3] - askPrice 
	 * columns[4] - dateTime
	 * 
	 * @param dataColumns - list of strings with values to transform into PxPrice
	 * @return - price after transformation. 
	 * 		Null return if there problem found while processing - it not pass validation or unexpected exception occured
	 */
	public static final Function<List<String>, FxPrice> transformStrings2FxPrice = columns -> {
		try {
			FxPrice result = null;
			if (columns == null) {
				log.warn("Value provided to parse is null");
				return null;
			}
	
			if (columns.size() != 5) {
				log.warn("Wrong column number: {} expected is 5.", columns.size());
				return null;
			}
	
			var idStr = columns.get(0).trim();
	
			var pairSymbol = columns.get(1).trim(); // pairSymbol won't be validated for pattern - only length
			if (pairSymbol.isBlank()) {
				log.warn("pairSymbol is empty string.");
				return null;
			}
	
			var bidPriceStr = columns.get(2).trim();
			BigDecimal bidPriceVal;
			try {
				bidPriceVal = new BigDecimal(bidPriceStr);
			} catch (NumberFormatException nfee) {
				log.warn("Bid price has wrong format: [{}]", bidPriceStr);
				return null;
			}
	
			var askPriceStr = columns.get(3).trim();
			BigDecimal askPriceVal;
			try {
				askPriceVal = new BigDecimal(askPriceStr);
			} catch (NumberFormatException nfe) {
				log.warn("Ask price has wrong format: [{}]", askPriceStr);
				return null;
			}
			
			LocalDateTime dateTimeVal;
			var dateTimeStr = columns.get(4).trim();
			try {
				dateTimeVal = LocalDateTime.parse(dateTimeStr, dtFormatter);
			} catch (DateTimeParseException dpe) {
				log.warn("Wrong date time format: [{}]", dateTimeStr);
				return null;
			}
			return FxPrice.builder()
					.priceId(idStr)
					.pairSymbol(pairSymbol)
					.bidPrice(bidPriceVal)
					.askPrice(askPriceVal)
					.dateTime(dateTimeVal)
					.build();
		} catch (Exception exc) {
			// here is catch any exception to not to brake processing chain
			// handling is simplified for demo purposes - just log warning
			log.warn("Unknown exception {} while processing: [{}]", exc.toString() ,columns.toString());
			return null;		
		}
	};
}
