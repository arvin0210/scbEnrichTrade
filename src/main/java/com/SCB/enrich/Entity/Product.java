package com.SCB.enrich.Entity;

import lombok.*;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Product")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private int product_id;

    @Column(nullable = false)
    @NonNull
    private String product_name;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Trade> trades;

    public void addTrade(Trade trade) {
        if (trades == null) {
            trades = new HashSet<>();
        }
        trade.setProduct(this);
        trades.add(trade);
    }
}
