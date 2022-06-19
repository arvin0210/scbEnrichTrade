package com.SCB.enrich.Service;

import com.SCB.enrich.Entity.Product;
import com.SCB.enrich.Entity.Trade;
import com.SCB.enrich.Repository.ProductRepository;
import com.SCB.enrich.Repository.TradeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class TradingServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradingService underTest;

    MockMultipartFile productCSV;
    MockMultipartFile tradeCSV;

    @BeforeEach
    void setup() throws Exception {
        FileInputStream productFile = new FileInputStream("src/main/resources/product.csv");
        productCSV = new MockMultipartFile("file", "product.csv", "multipart/form-data", productFile);
        FileInputStream tradeFile = new FileInputStream("src/main/resources/trade.csv");
        tradeCSV = new MockMultipartFile("file", "trade.csv", "multipart/form-data", tradeFile);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        tradeRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    void saveProducts() throws Exception {
        // given
        underTest.saveProducts(productCSV);

        // when
        List<Product> expected = productRepository.findAll();

        // then
        assertThat(expected.size()).isEqualTo(10);
        assertThat(expected.get(0).getProduct_id()).isEqualTo(1);
        assertThat(expected.get(0).getProduct_name()).isEqualTo("Treasury Bills Domestic");
    }

    @Test
    void saveTrades() throws Exception {
        // given
        underTest.saveProducts(productCSV);
        underTest.saveTrades(tradeCSV);
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        // when
        List<Trade> expected = tradeRepository.findAll();

        // then
        assertThat(expected.size()).isEqualTo(3);
        assertThat(formatter.format(expected.get(0).getDate())).isEqualTo("20160101");
        assertThat(expected.get(0).getProduct().getProduct_name()).isEqualTo("Treasury Bills Domestic");
        assertThat(expected.get(0).getCurrency()).isEqualTo("EUR");
        assertThat(expected.get(0).getPrice()).isEqualTo(10.0);
    }

    @Test
    @Transactional
    void deleteAllTrades() throws Exception {
        // given
        underTest.saveProducts(productCSV);
        underTest.saveTrades(tradeCSV);
        long recorded = tradeRepository.findAll().stream().count();

        // when
        underTest.deleteAllTrades();
        long balanceCount = tradeRepository.findAll().stream().count();

        // then
        assertThat(recorded).isEqualTo(3);
        assertThat(balanceCount).isEqualTo(0);
    }

}