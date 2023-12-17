package com.techchallenge.utils;

import com.techchallenge.domain.entity.Product;
import com.techchallenge.infrastructure.api.mapper.ProductMapper;
import com.techchallenge.infrastructure.api.request.InsertProductRequest;
import com.techchallenge.infrastructure.api.request.UpdateProductRequest;
import com.techchallenge.infrastructure.persistence.document.ProductDocument;
import com.techchallenge.infrastructure.persistence.mapper.ProductEntityMapper;
import java.math.BigDecimal;
import java.util.List;

public class MockUtils {

    public static List<UpdateProductRequest> getUpdateProductRequest() {
        UpdateProductRequest request1 = new UpdateProductRequest("sku456987001", "X Salada", "LANCHE", "Carne com Alface e pao", new BigDecimal("10.0"), "");
        UpdateProductRequest request2 = new UpdateProductRequest("sku456987002", "Coca Cola", "BEBIDA", "Gelada", new BigDecimal("15.0"), "");
        UpdateProductRequest request3 = new UpdateProductRequest("sku456987003", "Batata", "ACOMPANHAMENTO", "com bacon", new BigDecimal("8.0"), "");
        UpdateProductRequest request4 = new UpdateProductRequest("sku456987004", "Bolo", "SOBREMESA", "chocolate com creme", new BigDecimal("20.0"), "");

        UpdateProductRequest request5 = new UpdateProductRequest("sku456987005", "X Frango", "LANCHE", "lanche com frango", new BigDecimal("20.0"), "");

        UpdateProductRequest request6 = new UpdateProductRequest("sku456987006", "X Bacon", "LANCHE", "lanche com bacon", new BigDecimal("20.0"), "");

        return List.of(request1, request2, request3, request4, request5, request6);
    }

    public static InsertProductRequest getInsertProductRequest() {
        return new InsertProductRequest("", "", "", new BigDecimal("0"), "");
    }

    public static List<ProductDocument> getProductDocuments(){
        ProductEntityMapper mapper = new ProductEntityMapper();
        return mapper.toProductDocumentList(getProducts());
    }

    public static List<Product> getProducts(){
        ProductMapper mapper = new ProductMapper();
        return getUpdateProductRequest().stream().map(update -> mapper.toProduct(update)).toList();
    }

    public static Product getProduct(String sku){
        return new Product(sku,"X Salada Bacon", "LANCHE",
                "Carne com Alface e pao e bacon", new BigDecimal("10.0"), "");
    }


}
