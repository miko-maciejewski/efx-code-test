package prv.maciejewski.fxpricefeed.efxcodetest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import prv.maciejewski.fxpricefeed.efxcodetest.EfxCodeTestApplication;
import prv.maciejewski.fxpricefeed.efxcodetest.dto.FxPriceDto;
import prv.maciejewski.fxpricefeed.efxcodetest.services.FxDataFeedService;
import prv.maciejewski.fxpricefeed.efxcodetest.services.FxPriceStoreService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
@DisplayName("Testing FX prices controller module")
@AutoConfigureMockMvc
@SpringBootTest(classes = EfxCodeTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FxPriceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FxDataFeedService fxDataFeedService;

	@Autowired
	private FxPriceStoreService fxDaFxPriceStoreService;

	@Test
	void testGetController() throws Exception {
		
		fxDaFxPriceStoreService.removeAll();
		fxDataFeedService.onMessage("109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100");

		mockMvc.perform(get("/fx/price/GBP/USD"))
				// Validate the response code and content type
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(jsonPath("$.priceId").doesNotExist())
				.andExpect(jsonPath("$.pairSymbol",  is("GBP/USD")))
				.andExpect(jsonPath("$.bidPrice",  is((new BigDecimal("1.2486501")).doubleValue())))
				.andExpect(jsonPath("$.askPrice",  is((new BigDecimal("1.2573561")).doubleValue())))
				.andExpect(jsonPath("$.dateTime",  is("2020-06-01T12:01:02.1")));
	}
	
	@Test
	void testPutController() throws Exception {
		fxDaFxPriceStoreService.removeAll();
	
		String testCSV = "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001\n"+
						"107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002\r"+
						"108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002\n"+
						"109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100\n"+
						"110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110";
		
		mockMvc.perform(put("/fx/price")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .content(testCSV))
                .andExpect(status().isOk())           
                .andReturn();
		
		mockMvc.perform(get("/fx/price/EUR/JPY"))
				// Validate the response code and content type
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(jsonPath("$.priceId").doesNotExist())
				.andExpect(jsonPath("$.pairSymbol",  is("EUR/JPY")))
				.andExpect(jsonPath("$.bidPrice",  is((new BigDecimal("119.49039")).doubleValue())))
				.andExpect(jsonPath("$.askPrice",  is((new BigDecimal("120.02991")).doubleValue())))
				.andExpect(jsonPath("$.dateTime",  is("2020-06-01T12:01:02.11")));
	}

}
