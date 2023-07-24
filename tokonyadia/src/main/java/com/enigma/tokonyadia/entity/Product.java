package com.enigma.tokonyadia.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "m_product")
public class Product {
    @GenericGenerator(strategy = "uuid2", name = "system-uuid")
    @GeneratedValue(generator = "system-uuid")
    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ProductPrice> productPrices;

    public List<ProductPrice> getProductPrices() {
        return Collections.unmodifiableList(productPrices);
    }

    public void addProductPrice(ProductPrice productPrice) {
        productPrices.add(productPrice);
    }
}
