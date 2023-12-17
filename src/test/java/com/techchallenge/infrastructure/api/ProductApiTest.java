package com.techchallenge.infrastructure.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.application.gateway.ProductGateway;
import com.techchallenge.application.usecases.ProductUseCase;
import com.techchallenge.application.usecases.interactor.ProductUseCaseInteractor;
import com.techchallenge.core.exceptions.NotFoundException;
import com.techchallenge.core.exceptions.handler.ExceptionHandlerConfig;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Product;
import com.techchallenge.infrastructure.api.mapper.ProductMapper;
import com.techchallenge.infrastructure.api.request.InsertProductRequest;
import com.techchallenge.infrastructure.api.request.ProductResponse;
import com.techchallenge.infrastructure.api.request.UpdateProductRequest;
import com.techchallenge.infrastructure.gateways.ProductRepositoryGateway;
import com.techchallenge.infrastructure.persistence.document.ProductDocument;
import com.techchallenge.infrastructure.persistence.mapper.ProductEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.utils.MockUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class ProductApiTest {

    MockMvc mockMvc;
    ProductApi productApi;
    ProductUseCase productUseCase;
    ProductGateway productGateway;

    ProductMapper mapper;
    JsonUtils jsonUtils;

    @MockBean
    ProductRepository repository;
    ProductEntityMapper entityMapper;

    @BeforeEach
    void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonUtils = new JsonUtils(objectMapper);
        mapper = new ProductMapper();
        entityMapper = new ProductEntityMapper();

        productGateway = new ProductRepositoryGateway(repository, entityMapper);
        productUseCase = new ProductUseCaseInteractor(productGateway);

        productApi = new ProductApi(productUseCase, mapper);
        mockMvc = MockMvcBuilders.standaloneSetup(productApi)
                .setControllerAdvice(new ExceptionHandlerConfig()).build();

    }

    @Nested
    class TestInsertProduct {
        @Test
        void testInsertProductFieldsInvalidFields() throws Exception {
            InsertProductRequest request = new InsertProductRequest("", "", "", new BigDecimal("0"), "");
            String jsonRequest = jsonUtils.toJson(request).orElse("");

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isBadRequest()).andReturn();

            Optional<Result<ProductResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<ProductResponse>>() {
                    });
            int code = response.get().getCode();

            assertEquals(400, code, "Must Be Equals");
            assertEquals(List.of("Category is required!", "Description is required!", "Price can't be zero", "Title is required!"),
                    response.get().getErrors(), "Must Be Equals");


        }

        @Test
        void testInsertProductFieldsInvalidCategory() throws Exception {
            InsertProductRequest request = new InsertProductRequest("X Salada", "SANDUICHE", "Carne com Alface e pao", new BigDecimal("10.0"), "");
            String jsonRequest = jsonUtils.toJson(request).orElse("");

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isBadRequest()).andReturn();

            Optional<Result<ProductResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<ProductResponse>>() {
                    });
            int code = response.get().getCode();

            assertEquals(400, code, "Must Be Equals");
            assertEquals(List.of("Invalid Category!"),
                    response.get().getErrors(), "Must Be Equals");


        }

        @Test
        void testInsertProductFieldsValid() throws Exception {
            InsertProductRequest request = new InsertProductRequest("X Salada", "LANCHE",
                    "Carne com Alface e pao", new BigDecimal("10.0"), "");
            String jsonRequest = jsonUtils.toJson(request).orElse("");
            Product product = mapper.toProduct(request);
            ProductDocument productDocument = entityMapper.toProductDocument(product);

            when(repository.save(any())).thenReturn(productDocument);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isCreated()).andReturn();

            Optional<Result<ProductResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<ProductResponse>>() {
                    });
            int code = response.get().getCode();
            String category = response.get().getBody().category();
            String description = response.get().getBody().description();
            String title = response.get().getBody().title();
            BigDecimal price = response.get().getBody().price();

            assertEquals(201, code, "Must Be Equals");

            assertEquals("LANCHE", category, "Must Be Equals");
            assertEquals("Carne com Alface e pao", description, "Must Be Equals");
            assertEquals("X Salada", title, "Must Be Equals");
            assertEquals(new BigDecimal("10.0"), price, "Must Be Equals");


        }
    }

    @Nested
    class TestUpdateProduct {
        @Test
        void testUpdateProductFieldsValid() throws Exception {
            String id = "635981f6e40f61599e839ddb";
            UpdateProductRequest request = new UpdateProductRequest(id, "X Salada", "LANCHE", "Carne com Alface e pao", new BigDecimal("10.0"), "");
            String jsonRequest = jsonUtils.toJson(request).orElse("");
            Product product = mapper.toProduct(request);
            ProductDocument productDocument = entityMapper.toProductDocument(product);

            when(repository.save(productDocument)).thenReturn(productDocument);
            when(repository.findById(id)).thenReturn(Optional.of(productDocument));

            MvcResult mvcResult = mockMvc.perform(put("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<ProductResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<ProductResponse>>() {
                    });

            int code = response.get().getCode();
            String category = response.get().getBody().category();
            String description = response.get().getBody().description();
            String title = response.get().getBody().title();
            BigDecimal price = response.get().getBody().price();

            assertEquals(200, code, "Must Be Equals");

            assertEquals("LANCHE", category, "Must Be Equals");
            assertEquals("Carne com Alface e pao", description, "Must Be Equals");
            assertEquals("X Salada", title, "Must Be Equals");
            assertEquals(new BigDecimal("10.0"), price, "Must Be Equals");
        }


        @Test
        void testUpdateProductJustTitleField() throws Exception {
            String id = "635981f6e40f61599e839ddb";
            UpdateProductRequest request = new UpdateProductRequest(id, "X Salada Bacon", null, null,null, null);
            ProductDocument productFind = new ProductDocument(id, "X Salada", "LANCHE", "Carne com Alface e pao", new BigDecimal("10.0"), "");
            ProductDocument productUpdate = new ProductDocument(id, "X Salada Bacon", "LANCHE", "Carne com Alface e pao", new BigDecimal("10.0"), "");
            String jsonRequest = jsonUtils.toJson(request).orElse("");

            when(repository.save(any(ProductDocument.class))).thenReturn(productUpdate);
            when(repository.findById(id)).thenReturn(Optional.of(productFind));

            MvcResult mvcResult = mockMvc.perform(put("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<ProductResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<ProductResponse>>() {
                    });

            int code = response.get().getCode();
            String category = response.get().getBody().category();
            String description = response.get().getBody().description();
            String title = response.get().getBody().title();
            BigDecimal price = response.get().getBody().price();

            assertEquals(200, code, "Must Be Equals");

            assertEquals("LANCHE", category, "Must Be Equals");
            assertEquals("Carne com Alface e pao", description, "Must Be Equals");
            assertEquals("X Salada Bacon", title, "Must Be Equals");
            assertEquals(new BigDecimal("10.0"), price, "Must Be Equals");
        }
    }

    @Nested
    class TestFindProduct {
        @Test
        void testFindProductByCategoryLANCHE() throws Exception {

            List<Product> toProducts = MockUtils.getProducts();
            List<ProductDocument> documents = entityMapper.toProductDocumentList(toProducts);
            ProductDocument productDocument = documents.stream().filter(ent -> ent.getCategory().equals("LANCHE")).findFirst().orElse(null);

            when(repository.findByCategory("LANCHE",  PageRequest.of(0, 1))).thenReturn(new PageImpl(List.of(productDocument)));

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/category/LANCHE?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<List<ProductResponse>>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<List<ProductResponse>>>() {
                    });

            int code = response.get().getCode();
            String category = response.get().getBody().get(0).category();
            String description = response.get().getBody().get(0).description();
            String title = response.get().getBody().get(0).title();
            BigDecimal price = response.get().getBody().get(0).price();

            assertEquals(200, code, "Must Be Equals");

            assertEquals("LANCHE", category, "Must Be Equals");
            assertEquals("Carne com Alface e pao", description, "Must Be Equals");
            assertEquals("X Salada", title, "Must Be Equals");
            assertEquals(new BigDecimal("10.0"), price, "Must Be Equals");
        }

        @Test
        void testFindProductByCategoryBEBIDA() throws Exception {
            List<Product> toProducts = MockUtils.getProducts();
            List<ProductDocument> documents = entityMapper.toProductDocumentList(toProducts);
            ProductDocument productDocument = documents.stream().filter(ent -> ent.getCategory().equals("BEBIDA")).findFirst().orElse(null);

            when(repository.findByCategory("BEBIDA",  PageRequest.of(0, 1))).thenReturn(new PageImpl(List.of(productDocument)));

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/category/BEBIDA?page=0&size=1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<List<ProductResponse>>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<List<ProductResponse>>>() {
                    });

            int code = response.get().getCode();
            String category = response.get().getBody().get(0).category();
            String description = response.get().getBody().get(0).description();
            String title = response.get().getBody().get(0).title();
            BigDecimal price = response.get().getBody().get(0).price();

            assertEquals(200, code, "Must Be Equals");

            assertEquals("BEBIDA", category, "Must Be Equals");
            assertEquals("Gelada", description, "Must Be Equals");
            assertEquals("Coca Cola", title, "Must Be Equals");
            assertEquals(new BigDecimal("15.0"), price, "Must Be Equals");
        }

        @Test
        void testFindProductByCategoryInvalidCategory() throws Exception {

            List<Product> toProducts = MockUtils.getProducts();
            List<ProductDocument> documents = entityMapper.toProductDocumentList(toProducts);
            ProductDocument productDocument = documents.stream().filter(ent -> ent.getCategory().equals("BEBIDA")).findFirst().orElse(null);

            when(repository.findByCategory("TEST", PageRequest.of(0, 1)))
                    .thenReturn(new PageImpl(Collections.emptyList()));

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/category/TEST?page=0&size=1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()).andReturn();

            Optional<Result<List<ProductResponse>>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<List<ProductResponse>>>() {
                    });

            int code = response.get().getCode();

            assertEquals(404, code, "Must Be Equals");
            assertEquals(List.of("Product not found for category: TEST"),
                    response.get().getErrors(), "Must Be Equals");


        }

        @Test
        void testFindProductById() throws Exception {
            String id = "635981f6e40f61599e839ddb";
            UpdateProductRequest request3 = new UpdateProductRequest(id, "Batata", "ACOMPANHAMENTO", "com bacon", new BigDecimal("8.0"), "");
            Product product = mapper.toProduct(request3);
            ProductDocument entity = entityMapper.toProductDocument(product);

            when(repository.findById(id)).thenReturn(Optional.ofNullable(entity));

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/find/635981f6e40f61599e839ddb").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<ProductResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<ProductResponse>>() {
                    });

            int code = response.get().getCode();
            String category = response.get().getBody().category();
            String description = response.get().getBody().description();
            String title = response.get().getBody().title();
            BigDecimal price = response.get().getBody().price();

            assertEquals(200, code, "Must Be Equals");

            assertEquals("ACOMPANHAMENTO", category, "Must Be Equals");
            assertEquals("com bacon", description, "Must Be Equals");
            assertEquals("Batata", title, "Must Be Equals");
            assertEquals(new BigDecimal("8.0"), price, "Must Be Equals");
        }

        @Test
        void testFindProductWhenNotExists() throws Exception {

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/find/635981f6e40f61599e839ddb")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()).andReturn();

            Optional<Result<ProductResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<ProductResponse>>() {
                    });

            int code = response.get().getCode();

            assertEquals(404, code, "Must Be Equals");
            assertEquals(List.of("Product not found!"),
                    response.get().getErrors(), "Must Be Equals");


        }

        @Test
        void testFindProductByIds() throws Exception {

            List<ProductDocument> productDocuments = MockUtils.getProductDocuments().stream()
                    .filter( product ->product.getId().equals("sku456987003") || product.getId().equals("sku456987004")).toList();

            when(repository.findByIdIn(List.of("sku456987003","sku456987004")))
                    .thenReturn(productDocuments);

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/products/find?skus=sku456987003,sku456987004")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<List<ProductResponse>>> response =
                    jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<List<ProductResponse>>>() {
                    });

            int code = response.get().getCode();
            assertEquals(200, code, "Must Be Equals");
            assertEquals(2, response.get().getBody().size(), "Must Be Equals");
        }
    }

}