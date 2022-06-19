package com.SCB.enrich.Service;

import com.SCB.enrich.Entity.Product;
import com.SCB.enrich.Entity.Trade;
import com.SCB.enrich.Entity.TradeDto;
import com.SCB.enrich.Repository.ProductRepository;
import com.SCB.enrich.Repository.TradeRepository;
import com.SCB.enrich.Utility.UtilityServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TradingService {
    Logger logger = (Logger) LoggerFactory.getLogger(TradingService.class);

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TradeRepository tradeRepository;

    @Autowired
    UtilityServiceImpl utilService;

    public void saveProducts(MultipartFile file) throws Exception {
        productRepository.deleteAll();
        List<Product> products = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(file.getInputStream());
        try {
            try (BufferedReader br = new BufferedReader(reader)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (utilService.isInteger(data[0])) {
                        Product p = new Product();
                        p.setProduct_id(Integer.parseInt(data[0]));
                        p.setProduct_name(data[1]);
                        products.add(p);
                    }
                }
                br.close();
                reader.close();
                productRepository.saveAll(products);
            }
        } catch (IOException ex) {
            logger.error("Failed to parse CSV file " + ex);
            throw new Exception("Failed to parse CSV file {}", ex);
        }
    }

    public void saveTrades(MultipartFile file) throws Exception {
        deleteAllTrades();

        List<Product> products = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(file.getInputStream());
        try {
            try (BufferedReader br = new BufferedReader(reader)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (utilService.isInteger(data[0]) && utilService.isInteger(data[1]) && utilService.isDouble(data[3])) {
                        Trade t = new Trade();
                        t.setDate(new SimpleDateFormat("yyyyMMdd").parse(data[0]));
                        t.setCurrency(data[2]);
                        t.setPrice(Double.parseDouble(data[3]));
                        Product p = productRepository.findById(Integer.parseInt(data[1])).stream().findFirst().orElse(null);
                        if (p == null) {
                            logger.error("Invalid Product ID : " + data[1]);
                            throw new Exception("Invalid Product ID : " + data[1]);
                        }
                        p.addTrade(t);
                        products.add(p);
                    }
                }
                br.close();
                reader.close();
                productRepository.saveAll(products);
            }
        } catch (IOException ex) {
            logger.error("Failed to parse CSV file " + ex);
            throw new Exception("Failed to parse CSV file {}", ex);
        }
    }

    public void deleteAllTrades() {
        // clear previous trade records
        tradeRepository.deleteAllInBatch();
    }
}
