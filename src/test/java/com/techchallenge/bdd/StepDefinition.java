package com.techchallenge.bdd;


import com.techchallenge.infrastructure.api.request.InsertProductRequest;
import com.techchallenge.infrastructure.api.request.UpdateProductRequest;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.utils.ProductHelpér;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsMapContaining.hasKey;


public class StepDefinition {

    private Response response;
    private String sku;

    List<String> skus;
    private String category;
    InsertProductRequest request;

    ProductRepository productRepository;

    private String ENDPOINT_PRODUCTS = "http://localhost:8085/tech-challenge-product/products/api/v1";


    @Dado("que quero cadastrar um novo produto")
    public void que_quero_cadastrar_um_novo_produto() {
        request = new InsertProductRequest(null,"X Salada", "LANCHE",
                "Carne com Alface e pao", new BigDecimal("10.0"), "");
    }
    @Quando("quando informar todos os campos obrigatórios")
    public void quando_informar_todos_os_campos_obrigatórios() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(ENDPOINT_PRODUCTS);
    }
    @Entao("devo conseguir cadastrar o produto")
    public void devo_conseguir_cadastrar_o_produto() {
        ValidatableResponse body = response.then().statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("./data/product-schema.json"))
                .body(matchesJsonSchemaInClasspath("./data/product-insert.json"));
    }

    @Dado("que tenho um produto cadastrado")
    public void que_tenho_um_produto_cadastrado() {
       insertProducts();
    }
    @Dado("quero atualizar alguns dados")
    public void quero_atualizar_alguns_dados() {
        UpdateProductRequest update = new UpdateProductRequest("sku456987001",
                "X Salada Bacon", "LANCHE", "Carne Bacon", new BigDecimal("10.0"), "");
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(update)
                .when()
                .put(ENDPOINT_PRODUCTS);
    }
    @Entao("devo conseguir atualizar os dados")
    public void devo_conseguir_atualizar_os_dados() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/product-schema.json"));
    }

    @Dado("que preciso buscar um produto específico")
    public void que_preciso_buscar_um_produto_específico() {
        sku = "sku456987005";
    }
    @Quando("fizer a requisição informando o sku")
    public void fizer_a_requisição_informando_o_sku() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(ENDPOINT_PRODUCTS+"/find/{sku}", sku);
    }
    @Entao("devo obter o produto do sku informado")
    public void devo_obter_o_produto_do_sku_informado() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/product-schema.json"));
    }

    @Dado("que preciso buscar vários produtos")
    public void que_preciso_buscar_vários_produtos() {
        skus = new ArrayList<>(List.of("sku456987006", "sku456987001"));
    }
    @Quando("informo vários skus")
    public void informo_vários_skus() {
       response =  given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(ENDPOINT_PRODUCTS + "/find?skus=sku456987003&skus=sku456987004");

    }
    @Entao("devo conseguir obter todos dos skus informados")
    public void devo_conseguir_obter_todos_dos_skus_informados() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/product-schema-list.json"))
                .body(matchesJsonSchemaInClasspath("./data/product-findbyids.json"));
    }

    @Dado("que preciso buscar todos os produtos de uma categoria")
    public void que_preciso_buscar_todos_os_produtos_de_uma_categoria() {
        category = "LANCHE";
    }
    @Quando("informar uma categoria")
    public void informar_uma_categoria() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get( ENDPOINT_PRODUCTS+"/category/{category}?page=0&size=10", category);

    }
    @Entao("devo conseguir obter todos os produtos dessa categoria")
    public void devo_conseguir_obter_todos_os_produtos_dessa_categoria() {
       response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/product-schema-list.json"))
                .body(matchesJsonSchemaInClasspath("./data/product-update.json"));
    }

    @Dado("que preciso deletar um produto")
    public void que_preciso_deletar_um_produto() {
        sku = "sku456987001";
    }
    @Quando("informar o sku do produto")
    public void informar_o_sku_do_produto() {
        response= given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete( ENDPOINT_PRODUCTS+"/delete/{sku}", sku);
    }
    @Entao("o produto deve deixar de existir na consulta")
    public void o_produto_deve_deixar_de_existir_na_consulta() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasKey("code"))
                .body("$", hasKey("body"))
                .body("code", equalTo(200))
                .body("body", equalTo("Delete with success!"));
    }


    public void insertProducts(){
        ProductHelpér.getInsertProducsRequest().forEach( product -> {
            given().contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(product)
                    .when().post(ENDPOINT_PRODUCTS).then().statusCode(HttpStatus.CREATED.value());
        });
    }

}
