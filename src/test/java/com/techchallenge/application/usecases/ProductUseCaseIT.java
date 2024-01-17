package com.techchallenge.application.usecases;

import com.techchallenge.MongoTestConfig;
import com.techchallenge.application.gateway.ProductGateway;
import com.techchallenge.application.usecases.interactor.ProductUseCaseInteractor;
import com.techchallenge.core.exceptions.NotFoundException;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Product;
import com.techchallenge.domain.enums.Category;
import com.techchallenge.infrastructure.api.request.UpdateProductRequest;
import com.techchallenge.infrastructure.gateways.ProductRepositoryGateway;
import com.techchallenge.infrastructure.persistence.document.ProductDocument;
import com.techchallenge.infrastructure.persistence.mapper.ProductEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.utils.MockUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {MongoTestConfig.class})
@TestPropertySource(locations = "classpath:/application-test.yaml")
@Testcontainers
class ProductUseCaseIT {

    ProductUseCase productUseCase;
    ProductGateway productGateway;

    @Autowired
    ProductRepository repository;
    @Autowired
    ProductEntityMapper mapper;


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

    @BeforeEach
    public void init(){
        productGateway = new ProductRepositoryGateway(repository, mapper);
        productUseCase = new ProductUseCaseInteractor(productGateway);

        repository.deleteAll(MockUtils.getProductDocuments());
        repository.saveAll(MockUtils.getProductDocuments());

    }

    @Test
    void testInsertProduct(){

        Product product =  productUseCase.insert( MockUtils.getProduct(null));

        assertNotNull(product.getSku(), "Sku Must Be not null");
        assertEquals("X Salada Bacon",product.getTitle(), "Must Be Equals");
        assertEquals("Carne com Alface e pao e bacon",product.getDescription(), "Must Be Equals");
        assertEquals("",product.getImage(), "Must Be Equals");
        assertEquals(new BigDecimal("10.0"),product.getPrice(), "Must Be Equals");
        assertEquals(Category.LANCHE,product.getCategory(), "Must Be Equals");
        productUseCase.delete(product.getSku());
    }

    @Nested
    class TestUpdate {
        @Test
        void testUpdateTitleProduct() {
            String sku = "sku456987001";
            UpdateProductRequest request = new UpdateProductRequest(sku,"X Tudo Salada",
                    null, null,
                     null, "" );
            Product product = productUseCase.update(request);
            assertEquals("sku456987001",product.getSku(), "Must Be Equals");
            assertEquals("X Tudo Salada",product.getTitle(), "Must Be Equals");
            assertEquals("Carne com Alface e pao",product.getDescription(), "Must Be Equals");
            assertEquals("",product.getImage(), "Must Be Equals");
            assertEquals(new BigDecimal("10.0"),product.getPrice(), "Must Be Equals");
            assertEquals(Category.LANCHE, product.getCategory(), "Must Be Equals");
        }

        @Test
        void testUpdateDescriptionProduct() {
            String sku = "sku456987001";
            UpdateProductRequest request = new UpdateProductRequest(sku,null,
                    null, "Nova descrition",
                    null, "" );
            Product product = productUseCase.update(request);
            assertEquals("sku456987001",product.getSku(), "Must Be Equals");
            assertEquals("X Salada",product.getTitle(), "Must Be Equals");
            assertEquals("Nova descrition",product.getDescription(), "Must Be Equals");
            assertEquals("",product.getImage(), "Must Be Equals");
            assertEquals(new BigDecimal("10.0"),product.getPrice(), "Must Be Equals");
            assertEquals(Category.LANCHE, product.getCategory(), "Must Be Equals");
        }

        @Test
        void testUpdateImageProduct() {
            String sku = "sku456987001";
            UpdateProductRequest request = new UpdateProductRequest(sku,null,
                    null, null,
                    null, "Nova Imagem" );
            Product product = productUseCase.update(request);
            assertEquals("sku456987001",product.getSku(), "Must Be Equals");
            assertEquals("X Salada",product.getTitle(), "Must Be Equals");
            assertEquals("Carne com Alface e pao",product.getDescription(), "Must Be Equals");
            assertEquals("Nova Imagem",product.getImage(), "Must Be Equals");
            assertEquals(new BigDecimal("10.0"),product.getPrice(), "Must Be Equals");
            assertEquals(Category.LANCHE, product.getCategory(), "Must Be Equals");
        }

        @Test
        void testUpdatePriceProduct() {
            String sku = "sku456987001";
            UpdateProductRequest request = new UpdateProductRequest(sku,null,
                    null, null,
                    new BigDecimal("16.0"), null );
            Product product = productUseCase.update(request);
            assertEquals("sku456987001",product.getSku(), "Must Be Equals");
            assertEquals("X Salada",product.getTitle(), "Must Be Equals");
            assertEquals("Carne com Alface e pao",product.getDescription(), "Must Be Equals");
            assertEquals("",product.getImage(), "Must Be Equals");
            assertEquals(new BigDecimal("16.0"),product.getPrice(), "Must Be Equals");
            assertEquals(Category.LANCHE, product.getCategory(), "Must Be Equals");
        }

        @Test
        void testUpdateCategoryProduct() {
            String sku = "sku456987001";
            UpdateProductRequest request = new UpdateProductRequest(sku,null,
                    "ACOMPANHAMENTO", null,
                     null, null );
            Product product = productUseCase.update(request);
            assertEquals("sku456987001",product.getSku(), "Must Be Equals");
            assertEquals("X Salada",product.getTitle(), "Must Be Equals");
            assertEquals("Carne com Alface e pao",product.getDescription(), "Must Be Equals");
            assertEquals("",product.getImage(), "Must Be Equals");
            assertEquals(new BigDecimal("10.0"),product.getPrice(), "Must Be Equals");
            assertEquals(Category.ACOMPANHAMENTO, product.getCategory(), "Must Be Equals");
        }

        @Test
        void testTryUpdateProductWhenNotExists() {
            ProductRepository spyRepository = spy(repository);
            String sku = "sku0000";
            UpdateProductRequest request = new UpdateProductRequest(sku
                    , "X Salada",
                    "LANCHE", "Carne com Alface e pao", new BigDecimal("10.0"), "");
            NotFoundException notFoundException = assertThrows(NotFoundException.class,
                    () -> productUseCase.update(request));
            assertEquals("Product not found for update!", notFoundException.getMessage(), "Must Be Equals");
        }

    }

    @Nested
    class FindProductTest {

        @Test
        void testFindByCategoryLancheWithPagination() {
            Result<List<Product>> byCategory = productUseCase.findByCategory(Category.LANCHE.toString(), 0, 2);
            assertNotNull(byCategory, "Must Be Not Null");
            assertEquals(2L,byCategory.getBody().size(), "Must Be Equals");
            assertTrue(byCategory.getHasNext(), "Must Be True");
            assertEquals(3L,byCategory.getTotal(), "Must Be Equals");
        }

        @Test
        void testFindByCategoryBebidaWithPagination() {
            Result<List<Product>> byCategory = productUseCase.findByCategory(Category.BEBIDA.toString(), 0, 2);
            assertNotNull(byCategory, "Must Be Not Null");
            assertEquals(1L,byCategory.getBody().size(), "Must Be Equals");
            assertFalse(byCategory.getHasNext(), "Must Be False");
            assertEquals(1L,byCategory.getTotal(), "Must Be Equals");
        }

        @Test
        void testFindByCategoryWithInvalidCategory() {
            NotFoundException notFoundException = assertThrows(
                    NotFoundException.class,
                    () -> productUseCase.findByCategory("SANDUICHE", 0, 2));

            assertEquals("Product not found for category: SANDUICHE",
                    notFoundException.getMessage(), "Must Be Equals");
        }

        @Test
        void testFindBySkuSuccess() {
            Product product = productUseCase.findById("sku456987004");
            assertEquals("sku456987004",product.getSku(), "Must Be Equals");
            assertEquals("Bolo",product.getTitle(), "Must Be Equals");
            assertEquals("chocolate com creme",product.getDescription(), "Must Be Equals");
            assertEquals("",product.getImage(), "Must Be Equals");
            assertEquals(new BigDecimal("20.0"),product.getPrice(), "Must Be Equals");
            assertEquals(Category.SOBREMESA, product.getCategory(), "Must Be Equals");
        }
        @Test
        void testFindBySkuNotFound() {
            NotFoundException notFoundException =  assertThrows(NotFoundException.class,
                    () -> productUseCase.findById("sku0000"));
            assertEquals("Product not found!",
                    notFoundException.getMessage(), "Must Be Equals");
        }

        @Test
        void testFindBySkusSuccess() {
            List<String> skusParameters = List.of("sku456987002","sku456987003");
            List<Product> products = productUseCase.findByIds(skusParameters);
            List<String> skus = products.stream().map(product -> product.getSku()).toList();

            assertEquals(2,products.size(), "Must Be Equals");
            assertTrue(skus.containsAll(skusParameters), "Must Be True");

        }

        @Test
        void testFindBySkusNotFound() {
            List<String> skusParameters = List.of("sku000","sku111");
            NotFoundException notFoundException =  assertThrows(NotFoundException.class,
                    () -> productUseCase.findByIds(skusParameters));
            assertEquals("Products not found for skus: [sku000, sku111]",
                    notFoundException.getMessage(), "Must Be Equals");

        }
    }

    @Test
    void testDeleteProduct(){
        productUseCase.delete("sku456987002");
        NotFoundException notFoundException =  assertThrows(NotFoundException.class,
                () -> productUseCase.findById("sku456987002"));
        assertEquals("Product not found!",
                notFoundException.getMessage(), "Must Be Equals");
    }

    @Test
    void testProductDocumentEqualsAndHashCode_productEquals(){

        Product product = productUseCase.findById("sku456987002");
        ProductDocument productDocument = mapper.toProductDocument(product);
        ProductDocument other = ProductDocument.builder()
                .image("")
                .title("Coca Cola")
                .id("sku456987002")
                .price(new BigDecimal("15.0")).category("BEBIDA").description("Gelada")
                .build();
        assertTrue(productDocument.equals(other));
        assertNotNull(product.hashCode());
    }
    @Test
    void testProductDocumentEqualsAndHashCode_productNotEquals(){

        Product product = productUseCase.findById("sku456987002");
        ProductDocument productDocument = mapper.toProductDocument(product);
        ProductDocument other = ProductDocument.builder()
                .image("test")
                .title("Coca Cola")
                .id("sku456987002")
                .price(new BigDecimal("15.0")).category("BEBIDA").description("Gelada")
                .build();
        assertFalse(productDocument.equals(other));
        assertNotNull(product.hashCode());
    }



}