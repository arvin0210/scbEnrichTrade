package com.SCB.enrich.File;

import com.SCB.enrich.Entity.Trade;
import com.SCB.enrich.Entity.TradeDto;
import com.SCB.enrich.Repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileServiceImpl implements FileService {

    @Autowired
    TradeRepository tradeRepository;

    @Override
    public File downloadEnrichCSV() throws FileNotFoundException {
        File csvFile = new File("enrich.csv");
        PrintWriter printWriter = new PrintWriter(csvFile);
        printWriter.write("date,product_name,currency,price\n");
        for (TradeDto dto : this.getFileData()) {
            printWriter.write(dto.getDate() + "," + dto.getProduct_name() + "," + dto.getCurrency() + "," + dto.getPrice() + "\n");
        }
        printWriter.close();
        return csvFile;
    }

    @Override
    public List<TradeDto> getFileData() {
        List<TradeDto> dtoList = new ArrayList<>();
        List<Trade> all = tradeRepository.findAll();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        for (Trade trade : all) {
            TradeDto dto = new TradeDto(formatter.format(trade.getDate()), trade.getProduct().getProduct_name(), trade.getCurrency(), trade.getPrice());
            dtoList.add(dto);
        }
        return dtoList;
    }
}
