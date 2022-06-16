package com.SCB.enrich.Repository;

import com.SCB.enrich.Entity.Trade;
import com.SCB.enrich.Entity.TradeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Integer> {
}