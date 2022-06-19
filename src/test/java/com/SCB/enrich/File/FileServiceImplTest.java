package com.SCB.enrich.File;

import com.SCB.enrich.Entity.Product;
import com.SCB.enrich.Entity.TradeDto;
import com.SCB.enrich.Repository.ProductRepository;
import com.SCB.enrich.Repository.TradeRepository;
import com.SCB.enrich.Service.TradingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class FileServiceImplTest {

    Logger logger = (Logger) LoggerFactory.getLogger(TradingService.class);

    @Autowired
    private TradingService tradingService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private FileServiceImpl underTest;

    MockMultipartFile productCSV;
    MockMultipartFile tradeCSV;

    @BeforeEach
    void setUp() throws Exception {
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
    void downloadEnrichCSV() throws Exception {
        // given
        tradingService.saveProducts(productCSV);
        tradingService.saveTrades(tradeCSV);

        // when
        File expected = underTest.downloadEnrichCSV();
        FileReader reader = new FileReader(expected);
        int lines = 0;
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                lines++;
            }
            br.close();
            reader.close();
        } catch (IOException ex) {
            logger.error("Failed to parse CSV file " + expected.getName());
            throw new Exception("Failed to parse CSV file {}", ex);
        }

        // then
        assertThat(expected.getName()).isEqualTo("enrich.csv");
        assertThat(lines).isEqualTo(4);
    }

    @Test
    void getFileData() throws Exception {
        // given
        tradingService.saveProducts(productCSV);
        tradingService.saveTrades(tradeCSV);

        // when
        List<TradeDto> expected = underTest.getFileData();

        // then
        // 20160101,1,EUR,10.0
        assertThat(expected.size()).isEqualTo(3);
        assertThat(expected.get(0).getDate()).isEqualTo("20160101");
        assertThat(expected.get(0).getProduct_name()).isEqualTo("Treasury Bills Domestic");
        assertThat(expected.get(0).getCurrency()).isEqualTo("EUR");
        assertThat(expected.get(0).getPrice()).isEqualTo(10.0);
    }
}