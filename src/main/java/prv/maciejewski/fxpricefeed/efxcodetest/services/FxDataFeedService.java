package prv.maciejewski.fxpricefeed.efxcodetest.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import prv.maciejewski.fxpricefeed.efxcodetest.model.FxPrice;

/**
 * Service used to parse comma-separated values (CSV) strings and update value
 * in FxPriceStoreService
 * 
 * Assumptions for CSV format:
 * 
 * There is no header line in incoming message with column names. Lines are
 * separated with CR/LF/CRLF characters. Empty lines are ignored without
 * warning. Every line is set of record separated by column separator - default
 * is "," Every column in record may be surrounded by double quote character.
 * Double quote character are escaped by second double quote character put next
 * to it
 * 
 * Record with erratic data are ignored with warning in logs.
 * 
 * It is assume that problem in one record / CSV Line have no influence on
 * processing of other lines
 *
 * Details of CSV file in https://datatracker.ietf.org/doc/html/rfc4180
 */
@Service
public class FxDataFeedService {

	final static char DEFAULT_COLUMN_SEPARATOR = ',';
	final static char QUOTE_CHARACTER = '"';
	final static String CRLF_EOL = "\r\n";
	final static String LFCR_EOL = "\n\r";
	final static String CR_EOL = "\r";
	final static String LF_EOL = "\n";

	static char columnSeparator = DEFAULT_COLUMN_SEPARATOR;

	@Autowired
	FxPriceStoreService fxPriceStoreService;
	
	
	@Autowired
	FxPriceMarginCalculatorService fxPriceMarginCalculatorService;

//	@parsing csv, "" - for field, trim, verify date, parse string to BigDecimal,
//	timezone assumed is GMT, no negative values for bid, ask, no overwrite with data before current value

	/**
	 * Parses single line with CSV record using rules defined in RFC 4180
	 * 
	 * @param csvLine - string with line containing data record in CSV form. if null
	 *                then function returns null.
	 * 
	 * @return List of Strings with record columns
	 */
	List<String> parseCSVLine(String csvLine) {
		List<String> result = new ArrayList<>();

		if (csvLine == null)
			return null;

		boolean inQuotes = false; // flag to determine if we parse string in quote
		StringBuilder columnBuilder = new StringBuilder(); // column value

		for (int i = 0; i < csvLine.length(); i++) {
			char c = csvLine.charAt(i);

			if (c == QUOTE_CHARACTER) {
				// quote character found
				if (i < csvLine.length() - 1 && csvLine.charAt(i + 1) == QUOTE_CHARACTER) {
					// Found an escaped quote - skip the first quote and append the second
					columnBuilder.append(QUOTE_CHARACTER);
					i++; // move to next char
				} else {
					// Found a non-escaped quote - toggle the inQuotes flag
					inQuotes = !inQuotes;
				}
			} else if (c == columnSeparator && !inQuotes) {
				// unquoted column separator found
				result.add(columnBuilder.toString());
				columnBuilder.setLength(0);
			} else {
				columnBuilder.append(c);
			}
		}
		result.add(columnBuilder.toString()); // Add last column

		return result;
	}

	/**
	 * Splits provided string to list of Strings. Lines are separated by End of Line
	 * (EOL) separator. As EOL is platform dependent, EOL in string are normalized
	 * to LF_EOL (\n) character and then divided to lines. If null is provided as
	 * input then function return null value.
	 * 
	 * @param csvMessageFeed - data feed with lines separated by EOL character
	 * @return - List of Strings - each is separate line in feed.
	 */
	List<String> splitMessage2Lines(String csvMessageFeed) {

		if (csvMessageFeed == null)
			return null;

		String normalizedLines = csvMessageFeed.replace(CRLF_EOL, LF_EOL).replace(LFCR_EOL, LF_EOL).replace(CR_EOL,
				LF_EOL);

		return Arrays.asList(normalizedLines.split(LF_EOL));
	}

	/**
	 * Main procedure to feed new lines in CSV format and update price storage
	 * 
	 * 
	 * @param csvInput - 0 or more lines with CSV records to be parsed and sent to
	 *                 fx pricing store service
	 */
	public void onMessage(String csvMessageFeed) {

		// split message to line list
		List<String> lineList = splitMessage2Lines(csvMessageFeed);
		
		// process splitted lines (splitting message to lines is separated intentionally)
		lineList.stream()
			.filter(line -> (!line.isBlank())) // remove blank lines after split
			.map(line -> parseCSVLine(line))
			.filter(rec -> (rec != null)) // remove null in case of problem while parsing CSV Line
			.map(rec -> FxPrice.transformStrings2FxPrice.apply(rec))
			.filter(price -> (price != null)) // remove null generated in case of problem while 
			.map(price -> fxPriceMarginCalculatorService.calculateNewPrice(price))  // add margins to price
			.forEach(price -> fxPriceStoreService.updateFxPrice(price)); // update price store service
	}

}
