package com.SCB.enrich.Service;

import com.SCB.enrich.Entity.Product;
import com.SCB.enrich.Entity.Trade;
import com.SCB.enrich.Entity.TradeDto;
import com.SCB.enrich.Repository.ProductRepository;
import com.SCB.enrich.Repository.TradeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class TradingServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradingService tradingService;

    MockMultipartFile productCSV;
    MockMultipartFile tradeCSV;

    @BeforeEach
    void setup() throws Exception {
        FileInputStream productFile = new FileInputStream("src/main/resources/product.csv");
        productCSV = new MockMultipartFile("file", "product.csv", "multipart/form-data", productFile);
        FileInputStream tradeFile = new FileInputStream("src/main/resources/trade.csv");
        tradeCSV = new MockMultipartFile("file", "product.csv", "multipart/form-data", tradeFile);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void saveProductsManually() throws Exception {
        // given
        try (BufferedReader br = new BufferedReader(new InputStreamReader(productCSV.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (!data[0].contains("product_id")) {
                    Product p = new Product();
                    p.setProduct_id(Integer.parseInt(data[0]));
                    p.setProduct_name(data[1]);
                    productRepository.save(p);
                }
            }
        }

        // when
        List<Product> expected = productRepository.findAll();

        // then
        assertThat(expected.size()).isEqualTo(10);
    }

    @Test
    void saveProducts() throws Exception {
        // given
        tradingService.saveProducts(productCSV);

        // when
        List<Product> expected = productRepository.findAll();

        // then
        assertThat(expected.size()).isEqualTo(10);
    }

    @Test
    void saveTradesManually() throws Exception {
        // given
        tradingService.saveProducts(productCSV);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(tradeCSV.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (!data[0].contains("date")) {
                    Trade t = new Trade();
                    t.setDate(new SimpleDateFormat("yyyyMMdd").parse(data[0]));
                    t.setCurrency(data[2]);
                    t.setPrice(Double.parseDouble(data[3]));
                    Product p = productRepository.findById(Integer.parseInt(data[1])).stream().findFirst().orElse(null);
                    if (p == null) {
                        throw new Exception("Invalid Product ID : " + data[1]);
                    }
                    p.addTrade(t);
                    productRepository.save(p);
                }
            }
        }

        // when
        List<Trade> expected = tradeRepository.findAll();

        // then
        assertThat(expected.size()).isEqualTo(3);
    }

    @Test
    void saveTrades() throws Exception {
        // given
        tradingService.saveProducts(productCSV);
        tradingService.saveTrades(tradeCSV);

        // when
        List<Trade> expected = tradeRepository.findAll();

        // then
        assertThat(expected.size()).isEqualTo(3);
    }

    @Test
    void findAllTradesManually() throws Exception {
        // given
        tradingService.saveProducts(productCSV);
        tradingService.saveTrades(tradeCSV);

        List<TradeDto> expected = new ArrayList<>();
        List<Trade> all = tradeRepository.findAll();

        // when
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        for (Trade trade : all) {
            TradeDto dto = new TradeDto(formatter.format(trade.getDate()), trade.getProduct().getProduct_name(), trade.getCurrency(), trade.getPrice());
            expected.add(dto);
        }

        // then
        assertThat(expected.size()).isEqualTo(3);
        assertThat(expected.get(0).getDate()).isEqualTo("20160101");

    }

    @Test
    void findAllProducts() throws Exception {
        // given
        tradingService.saveProducts(productCSV);

        // when
        List<Product> expected = tradingService.findAllProducts();

        // then
        assertThat(expected.size()).isEqualTo(10);
    }

    @Test
    void findAllTrades() throws Exception {
        // given
        tradingService.saveProducts(productCSV);
        tradingService.saveTrades(tradeCSV);

        // when
        List<Trade> expected = tradingService.findAllTrades();

        // then
        assertThat(expected.size()).isEqualTo(3);
    }

    @Test
    void findAllTrades_DTO() throws Exception {
        // given
        tradingService.saveProducts(productCSV);
        tradingService.saveTrades(tradeCSV);

        // when
        List<TradeDto> expected = tradingService.findAllTrades_DTO();

        // then
        assertThat(expected.size()).isEqualTo(3);
        assertThat(expected.get(0).getDate()).isEqualTo("20160101");
    }

    @Test
    void enrichCsvFile() throws Exception {
        // given
        tradingService.saveProducts(productCSV);
        tradingService.saveTrades(tradeCSV);

        File csvFile = tradingService.enrichCsvFile();
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        String line;
        List<TradeDto> expected = new ArrayList<>();

        // when
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            if (!data[0].contains("date")) {
                TradeDto t = new TradeDto(data[0], data[1], data[2], Double.parseDouble(data[3]));
                expected.add(t);
            }
        }

        // then
        assertThat(expected.size()).isEqualTo(3);
        assertThat(expected.get(0).getDate()).isEqualTo("20160101");
    }
}