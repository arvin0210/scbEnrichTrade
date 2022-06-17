package com.SCB.enrich.Service;

import com.SCB.enrich.Entity.Product;
import com.SCB.enrich.Entity.Trade;
import com.SCB.enrich.Entity.TradeDto;
import com.SCB.enrich.Repository.ProductRepository;
import com.SCB.enrich.Repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradingService {
    Logger logger = (Logger) LoggerFactory.getLogger(TradingService.class);

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TradeRepository tradeRepository;

    public void saveProducts(MultipartFile file) throws Exception {
        productRepository.deleteAll();
        List<Product> products = new ArrayList<>();
        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (!data[0].contains("product_id")) {
                        Product p = new Product();
                        p.setProduct_id(Integer.parseInt(data[0]));
                        p.setProduct_name(data[1]);
                        products.add(p);
                    }
                }
                productRepository.saveAll(products);
            }
        } catch (IOException ex) {
            logger.error("Failed to parse CSV file " + ex);
            throw new Exception("Failed to parse CSV file {}", ex);
        }
    }

    public void saveTrades(MultipartFile file) throws Exception {
        tradeRepository.deleteAll();
        List<Product> products = new ArrayList<>();
        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
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
                            logger.error("Invalid Product ID : " + data[1]);
                            throw new Exception("Invalid Product ID : " + data[1]);
                        }
                        p.addTrade(t);
                        products.add(p);
                    }
                }
                productRepository.saveAll(products);
            }
        } catch (IOException ex) {
            logger.error("Failed to parse CSV file " + ex);
            throw new Exception("Failed to parse CSV file {}", ex);
        }
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public List<Trade> findAllTrades() {
        return tradeRepository.findAll();
    }

    public List<TradeDto> findAllTrades_DTO() {
        List<TradeDto> dtoList = new ArrayList<>();
        List<Trade> all = tradeRepository.findAll();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        for (Trade trade : all) {
            TradeDto dto = new TradeDto(formatter.format(trade.getDate()), trade.getProduct().getProduct_name(), trade.getCurrency(), trade.getPrice());
            dtoList.add(dto);
        }
        return dtoList;
    }

    public File enrichCsvFile() throws FileNotFoundException {
        File csvFile = new File("enrich.csv");
        PrintWriter printWriter = new PrintWriter(csvFile);
        printWriter.write("date,product_name,currency,price\n");
        for (TradeDto dto : findAllTrades_DTO()) {
            printWriter.write(dto.getDate() + "," + dto.getProduct_name() + "," + dto.getCurrency() + "," + dto.getPrice() + "\n");
        }
        printWriter.close();
        return csvFile;
    }
}
