package prv.maciejewski.fxpricefeed.efxcodetest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
/**
 * The class is DTO class used to feed with data consumers via FXPriceController
 * 
 * @author Mikolaj Maciejewski
 *
 */
public class FxPriceDto {

	// currency pair symbols to deliver
	String pairSymbol;

	// bid price for the currency pair
	BigDecimal bidPrice;

	// ask price for the currency pair
	BigDecimal askPrice;

	// local date time in GMT
	LocalDateTime dateTime;

}
