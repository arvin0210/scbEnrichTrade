package com.SCB.enrich.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Trade")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"product"})
public class Trade {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int trade_id;

    @Column(nullable = false)
    @NonNull
    private String currency;

    @Column(nullable = false)
    @NonNull
    private double price;

    @Column(nullable = false)
    @NonNull
    @Basic
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "product_id") // product_product_id
    @ToString.Exclude
    private Product product;
}
