package com.enigma.tokonyadia.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "m_product_price")
public class ProductPrice {

    @GenericGenerator(strategy = "uuid2", name = "system-uuid")
    @GeneratedValue(generator = "system-uuid")
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(columnDefinition = "bigint check (price > 0)")
    private Long price;

    @Column(columnDefinition = "int check (stock > 0)")
    private Integer stock;

    private Boolean isActive;
}
