package com.enigma.tokonyadia.specification;

import com.enigma.tokonyadia.entity.Product;
import com.enigma.tokonyadia.entity.ProductPrice;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> getSpecification(String name, Long maxPrice) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, ProductPrice> productPrices = root.join("productPrices");
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(productPrices.get("price"), maxPrice));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
