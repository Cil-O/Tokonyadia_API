package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.Product;
import com.enigma.tokonyadia.entity.ProductPrice;
import com.enigma.tokonyadia.entity.Store;
import com.enigma.tokonyadia.model.request.ProductRequest;
import com.enigma.tokonyadia.model.response.ProductResponse;
import com.enigma.tokonyadia.model.response.StoreResponse;
import com.enigma.tokonyadia.repository.ProductRepository;
import com.enigma.tokonyadia.service.ProductPriceService;
import com.enigma.tokonyadia.service.ProductService;
import com.enigma.tokonyadia.service.StoreService;
import com.enigma.tokonyadia.specification.ProductSpecification;
import com.enigma.tokonyadia.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final ProductPriceService productPriceService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProductResponse create(ProductRequest request) {
        validationUtil.validate(request);
        Store store = storeService.getById(request.getStoreId());

        Product product = Product.builder()
                .name(request.getProductName())
                .description(request.getDescription())
                .build();
        productRepository.saveAndFlush(product);

        ProductPrice productPrice = ProductPrice.builder()
                .price(request.getPrice())
                .stock(request.getStock())
                .store(store)
                .product(product)
                .isActive(true)
                .build();
        productPriceService.create(productPrice);

        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .description(product.getDescription())
                .price(productPrice.getPrice())
                .stock(productPrice.getStock())
                .store(StoreResponse.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .address(store.getAddress())
                        .build())
                .build();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public List<ProductResponse> createBulk(List<ProductRequest> products) {
        return products.stream().map(this::create).collect(Collectors.toList());
    }

    @Override
    public ProductResponse getById(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
        Optional<ProductPrice> productPrice = product.getProductPrices().stream().filter(ProductPrice::getIsActive).findFirst();

        if (productPrice.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found");
        Store store = productPrice.get().getStore();

        return toProductResponse(product, productPrice.get(), store);
    }

    @Override
    public Page<ProductResponse> getAllByNameOrPrice(String name, Long maxPrice, Integer page, Integer size) {
        Specification<Product> specification = ProductSpecification.getSpecification(name, maxPrice);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(specification, pageable);
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products.getContent()) {
            Optional<ProductPrice> productPrice = product.getProductPrices()
                    .stream()
                    .filter(ProductPrice::getIsActive).findFirst();

            if (productPrice.isEmpty()) continue;
            Store store = productPrice.get().getStore();

            productResponses.add(toProductResponse(product, productPrice.get(), store));
        }

        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public ProductResponse update(ProductRequest request) {
        Product currentProduct = findByIdOrThrowNotFound(request.getProductId());
        currentProduct.setName(request.getProductName());
        currentProduct.setDescription(request.getDescription());

        ProductPrice productPriceActive = productPriceService.findProductPriceActive(request.getProductId(), true);

        if (!productPriceActive.getStore().getId().equals(request.getStoreId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "store tidak boleh diubah");

        // TODO: If price different create new product price
        if (!request.getPrice().equals(productPriceActive.getPrice())) {
            productPriceActive.setIsActive(false);
            ProductPrice productPrice = productPriceService.create(ProductPrice.builder()
                    .price(request.getPrice())
                    .stock(request.getStock())
                    .product(currentProduct)
                    .store(productPriceActive.getStore())
                    .isActive(true)
                    .build());
            currentProduct.addProductPrice(productPrice);
            return toProductResponse(currentProduct, productPrice, productPrice.getStore());
        }

        productPriceActive.setStock(request.getStock());

        return toProductResponse(currentProduct, productPriceActive, productPriceActive.getStore());
    }

    @Override
    public void deleteById(String id) {
        Product product = findByIdOrThrowNotFound(id);
        productRepository.delete(product);
    }

    private Product findByIdOrThrowNotFound(String id) {
        return productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
    }

    private static ProductResponse toProductResponse(Product product, ProductPrice productPrice, Store store) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .description(product.getDescription())
                .price(productPrice.getPrice())
                .stock(productPrice.getStock())
                .store(StoreResponse.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .address(store.getAddress())
                        .build())
                .build();
    }
}
