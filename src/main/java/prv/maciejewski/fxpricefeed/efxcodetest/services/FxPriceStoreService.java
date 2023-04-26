package prv.maciejewski.fxpricefeed.efxcodetest.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import prv.maciejewski.fxpricefeed.efxcodetest.model.FxPrice;


/**
 * Simple service for storing data feed from market
 * Assumed is no use of database in the case for demo purposes.
 * Instead of DB data are stored in simple map object
 * 
 * @author Mikolaj Maciejewski
 *
 */
@Service
@Slf4j
public class FxPriceStoreService {
	
	// work as price storage
	private static final Map<String, FxPrice> fxPriceStorage = new HashMap<>();
	
	/**
	 * Returns Optional object with FxPrice from storage with provide pairSymbol. 
	 * 
	 * @param pairSymbol - symbols to get fx price for
	 * 
	 * @return Optional with fx price for pairSymbol
	 */
	public Optional<FxPrice> getFxPriceByPairSymbol(String pairSymbol) {
		return Optional.ofNullable(fxPriceStorage.get(pairSymbol));
	}
	
	/**
	 * Return list of all stored fx prices stored in storage
	 * 
	 * @return - list of fx prices
	 */
	public List<FxPrice> getAllFxPrices() {
		return fxPriceStorage.entrySet().stream().map(entry -> entry.getValue()).toList();
	}
	
	/**
	 * Adds / updates storage with new price.
	 * If price with pair symbol exist then dates are check - if it is newer then it is updated,
	 * else warnng is sent to logs and it is not updated
	 * if prices does not exists in storage then it is just added
	 * 
	 * @param newFxPrice - new price to add / update
	 */
	public void updateFxPrice(FxPrice newFxPrice) {
		// just raport and ignore null value - something went wrong
		if (newFxPrice == null) {
			log.warn("Update with null value has been ignored.");
			return;
		}
		FxPrice currentFxPrice = fxPriceStorage.get(newFxPrice.getPairSymbol());

		if ((currentFxPrice != null) && (currentFxPrice.getDateTime().compareTo(newFxPrice.getDateTime()) > 0)) {
			log.warn("Update with older dated price has been ignored.");
			return;
		}
		fxPriceStorage.put(newFxPrice.getPairSymbol(), newFxPrice);
				
	}

	/**
	 * Clears storage - used for test purposes
	 */
	public void removeAll() {
		fxPriceStorage.clear();
		
	}
}
