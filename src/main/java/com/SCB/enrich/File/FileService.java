package com.SCB.enrich.File;

import com.SCB.enrich.Entity.TradeDto;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@Service
public interface FileService {
    public File downloadEnrichCSV() throws FileNotFoundException;
    public List<TradeDto> getFileData();
}
