package com.SCB.enrich.Controller;

import com.SCB.enrich.File.FileServiceImpl;
import com.SCB.enrich.Service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/v1")
public class TradeController {
    @Autowired
    private TradingService tradingService;

    @Autowired
    private FileServiceImpl fileService;

    @PostMapping(value = "/product_update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity updateProducts(@RequestParam("product.csv")MultipartFile file) throws Exception {
        tradingService.saveProducts(file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/enrich", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "text/csv")
    public ResponseEntity<Resource> enrichTrades(@RequestParam("trade.csv")MultipartFile file) throws Exception {
        tradingService.saveTrades(file);
        File enrichCSV = fileService.downloadEnrichCSV();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(enrichCSV));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(enrichCSV.length())
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
}
