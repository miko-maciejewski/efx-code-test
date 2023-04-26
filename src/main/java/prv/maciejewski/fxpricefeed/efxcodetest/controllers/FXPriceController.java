package prv.maciejewski.fxpricefeed.efxcodetest.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import prv.maciejewski.fxpricefeed.efxcodetest.dto.FxPriceDto;
import prv.maciejewski.fxpricefeed.efxcodetest.model.FxPrice;
import prv.maciejewski.fxpricefeed.efxcodetest.services.FxDataFeedService;
import prv.maciejewski.fxpricefeed.efxcodetest.services.FxPriceStoreService;

@RestController
@RequestMapping(path = "fx")
public class FXPriceController {
	
	@Autowired
	FxPriceStoreService fxPriceStoreService;
	
	@Autowired
	FxDataFeedService fxDataFeedService;
	

    /**
     * Returns fx price for given currancy pair symbols
     * 
     * @param pairSymbol - currncy symbol of pair to get fx price
     * @return fx price for currency
     */
    @GetMapping("/price/{pairSymbol}")
    public FxPriceDto getFXPrice(@PathVariable String pairSymbol) throws Exception {
    	String decodedPairSymbol =  URLDecoder.decode(pairSymbol, StandardCharsets.UTF_8.toString());
    	decodedPairSymbol = decodedPairSymbol.replaceAll("$2F", "/");
    	Optional<FxPrice> fxPrice = fxPriceStoreService.getFxPriceByPairSymbol(decodedPairSymbol);
    	
    	 FxPriceDto result = null;
    	
    	// if fx price found then translate entity to DTO
    	if (fxPrice.isPresent()) {
    		FxPrice value2send = fxPrice.get();
    		 result = FxPriceDto.builder()
						.pairSymbol(value2send.getPairSymbol())
						.askPrice(value2send.getAskPrice())
						.bidPrice(value2send.getBidPrice())
						.dateTime(value2send.getDateTime())
						.build();		
    	} else {
    		result = new FxPriceDto(); // return empty object if not found
    	}
    	return result;
    }
    
    @GetMapping("/price/{pairSymbol1}/{pairSymbol2}")
    public FxPriceDto getFXPrice(@PathVariable String pairSymbol1, @PathVariable String pairSymbol2) throws Exception {
    	String pairSymbol = pairSymbol1.concat("/").concat(pairSymbol2);
    	return this.getFXPrice(pairSymbol);
    }
    
    /**
     * Returns all fx prices
     * 
     * @return fx prices for all currently stored currency pairs 
     */
    @GetMapping("/prices")
    public List<FxPriceDto> getFXPrices()
    {
    	List<FxPrice> fxPrice = fxPriceStoreService.getAllFxPrices();
    	
    	List<FxPriceDto> result = fxPrice.stream()
    				.map(value2send -> {return FxPriceDto.builder()
    						.pairSymbol(value2send.getPairSymbol())
    						.askPrice(value2send.getAskPrice())
    						.bidPrice(value2send.getBidPrice())
    						.dateTime(value2send.getDateTime())
    						.build(); })
    						.collect(Collectors.toList());
    	return result;
    }
    
    
    /**
     * Simple put endpoing for csv data feed to system.
     * 
     */
    @PutMapping("/price")
    public void putFxPrice(@RequestBody String csvPriceFeed)
    {
    	fxDataFeedService.onMessage(csvPriceFeed);
    }
}
