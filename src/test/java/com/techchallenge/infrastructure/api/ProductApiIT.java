package com.techchallenge.infrastructure.api;

import com.techchallenge.MongoTestConfig;
import com.techchallenge.application.gateway.ProductGateway;
import com.techchallenge.application.usecases.ProductUseCase;
import com.techchallenge.application.usecases.interactor.ProductUseCaseInteractor;
import com.techchallenge.config.MongoConfig;
import com.techchallenge.domain.entity.Product;
import com.techchallenge.infrastructure.api.request.InsertProductRequest;
import com.techchallenge.infrastructure.api.request.UpdateProductRequest;
import com.techchallenge.infrastructure.gateways.ProductRepositoryGateway;
import com.techchallenge.infrastructure.persistence.document.ProductDocument;
import com.techchallenge.infrastructure.persistence.mapper.ProductEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.utils.ProductHelpér;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ProductApiIT {

    public static final String API_V_1_PRODUCTS = "/tech-challenge-product/products/api/v1";
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.2"))
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(20)));

    @DynamicPropertySource
    static void overrrideMongoDBContainerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
    }

    @BeforeAll
    static void setUp(){
        mongoDBContainer.withReuse(true);
        mongoDBContainer.start();
    }

    @AfterAll
    static void setDown(){
        mongoDBContainer.stop();
    }


    @Autowired
    ProductRepository repository;

    @LocalServerPort
    private int port;
    @BeforeEach
    public void init(){

        repository.deleteAll(ProductHelpér.getProductDocuments());
        repository.saveAll(ProductHelpér.getProductDocuments());

        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }




    @Nested
    class TestInsertProduct {
        @Test
        void testInsertProductFieldsInvalidFields() throws Exception {
            InsertProductRequest request = new InsertProductRequest(null,"", "", "",
                    new BigDecimal("0"), "");
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post( API_V_1_PRODUCTS)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("$", hasKey("code"))
                    .body("$", hasKey("errors"))
                    .body("code", equalTo(400))
                    .body("errors[0]", equalTo("Category is required!"))
                    .body("errors[1]", equalTo("Description is required!"))
                    .body("errors[2]", equalTo("Price can't be zero"))
                    .body("errors[3]", equalTo("Title is required!"));
        }

        @Test
        void testInsertProductFieldsInvalidCategory() throws Exception {
            InsertProductRequest request = new InsertProductRequest(null,"X Salada", "SANDUICHE",
                    "Carne com Alface e pao", new BigDecimal("10.0"), "");

            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post( API_V_1_PRODUCTS)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("$", hasKey("code"))
                    .body("$", hasKey("errors"))
                    .body("code", equalTo(400))
                    .body("errors[0]", equalTo("Invalid Category!"));

        }

        @Test
        void testInsertProductFieldsValid() throws Exception {
            InsertProductRequest request = new InsertProductRequest(null,"X Salada", "LANCHE",
                    "Carne com Alface e pao", new BigDecimal("10.0"), "");

            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post( API_V_1_PRODUCTS)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("./data/product-schema.json"))
                    .body(matchesJsonSchemaInClasspath("./data/product-insert.json"));

        }
    }

    @Nested
    class TestUpdateProduct {
        @Test
        void testUpdateProductFieldsValid() throws Exception {
            UpdateProductRequest request = new UpdateProductRequest("sku456987001", "X Salada Bacon", "LANCHE", "Carne com Alface e pao", new BigDecimal("10.0"), "");
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .put( API_V_1_PRODUCTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("./data/product-schema.json"))
                    .body(matchesJsonSchemaInClasspath("./data/product-update.json"));

        }


        @Test
        void testUpdateProductJustTitleField() throws Exception {

            UpdateProductRequest request = new UpdateProductRequest("sku456987004", "Bolo De Chocolate", "SOBREMESA",
                    "chocolate com creme", new BigDecimal("20.0"), "");

            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .put( API_V_1_PRODUCTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("./data/product-schema.json"))
                    .body(matchesJsonSchemaInClasspath("./data/product-update.json"));

        }
    }

    @Nested
    class TestDeleteProduct {
        @Test
        void testDeleteProductFieldsValid() throws Exception {
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete( API_V_1_PRODUCTS+"/delete/{sku}", "sku456987001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasKey("code"))
                    .body("$", hasKey("body"))
                    .body("code", equalTo(200))
                    .body("body", equalTo("Delete with success!"));

        }
    }

    @Nested
    class TestFindProduct {
        @Test
        void testFindProductByCategoryLANCHE() throws Exception {
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get( API_V_1_PRODUCTS+"/category/{category}?page=0&size=10", "LANCHE")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("./data/product-schema-list.json"))
                    .body(matchesJsonSchemaInClasspath("./data/product-update.json"));
        }

        @Test
        void testFindProductByCategoryBEBIDA() throws Exception {
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get( API_V_1_PRODUCTS+"/category/{category}?page=0&size=10", "BEBIDA")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("./data/product-schema-list.json"))
                    .body(matchesJsonSchemaInClasspath("./data/product-category-bebida.json"));


        }

        @Test
        void testFindProductByCategoryInvalidCategory() throws Exception {
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get( API_V_1_PRODUCTS+"/category/{category}?page=0&size=10", "TEST")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("$", hasKey("code"))
                    .body("$", hasKey("errors"))
                    .body("code", equalTo(404))
                    .body("errors[0]", equalTo("Product not found for category: TEST"));

        }

        @Test
        void testFindProductById() throws Exception {
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get( API_V_1_PRODUCTS+"/find/{sku}", "sku456987001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("./data/product-schema.json"))
                    .body(matchesJsonSchemaInClasspath("./data/product-findbyId.json"));
        }

        @Test
        void testFindProductWhenNotExists() throws Exception {

            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(API_V_1_PRODUCTS + "/find/{sku}", "test")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("$", hasKey("code"))
                    .body("$", hasKey("errors"))
                    .body("code", equalTo(404))
                    .body("errors[0]", equalTo("Product not found!"));
        }

        @Test
        void testFindProductByIds() throws Exception {
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(API_V_1_PRODUCTS + "/find?skus=sku456987003&skus=sku456987004")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("./data/product-schema-list.json"))
                    .body(matchesJsonSchemaInClasspath("./data/product-findbyids.json"));

        }
    }

}