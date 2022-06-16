package com.SCB.enrich.Entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TradeDto implements Serializable {
    private final String date;
    private final String product_name;
    private final String currency;
    private final double price;
}
