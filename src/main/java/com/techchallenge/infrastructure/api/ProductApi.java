package com.techchallenge.infrastructure.api;

import com.techchallenge.application.usecases.ProductUseCase;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Product;
import com.techchallenge.infrastructure.api.mapper.ProductMapper;
import com.techchallenge.infrastructure.api.request.InsertProductRequest;
import com.techchallenge.infrastructure.api.request.ProductResponse;
import com.techchallenge.infrastructure.api.request.UpdateProductRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("products/api/v1")
@Tag(name = "Product API")
public class ProductApi {

	private final ProductUseCase productUseCase;
	private final ProductMapper mapper;

	public ProductApi(ProductUseCase productUseCase, ProductMapper mapper) {
		super();
		this.productUseCase = productUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<Result<ProductResponse>> insert(@RequestBody @Valid InsertProductRequest request,
														  UriComponentsBuilder uri) {
		Product product = productUseCase.insert(mapper.toProduct(request));
		UriComponents uriComponents = uri.path("/products/api/v1/find/{sku}").buildAndExpand(product.getSku());
		return ResponseEntity.created(uriComponents.toUri()).body(Result.create(mapper.toProductResponse(product)));
	}

	@PutMapping
	public ResponseEntity<Result<ProductResponse>> update(@RequestBody @Valid UpdateProductRequest request) {
		Product product = productUseCase.update(request);
		return ResponseEntity.ok(Result.ok(mapper.toProductResponse(product)));
	}

	@DeleteMapping("delete/{sku}")
	public ResponseEntity<Result<String>> delete(@PathVariable String sku) {
		productUseCase.delete(sku);
		return ResponseEntity.ok(Result.ok("Delete with success!"));
	}

	@GetMapping(value = "/category/{category}")
	public ResponseEntity<Result<List<ProductResponse>>> findByCategory(
			@PathVariable String category, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Result<List<Product>> product = productUseCase.findByCategory(category, page, size);
		return ResponseEntity.status(HttpStatus.OK).headers(product.getHeadersNosniff()).
				body(Result.ok(mapper.toProductResponseList(product.getBody()),
				product.getHasNext(), product.getTotal()));
	}

	@GetMapping("/find/{sku}")
	public ResponseEntity<Result<ProductResponse>> findBysku(@PathVariable String sku) {
		Product product = productUseCase.findById(sku);
		return ResponseEntity.ok(Result.ok(mapper.toProductResponse(product)));
	}

	@GetMapping("/find")
	public ResponseEntity<Result<List<ProductResponse>>> findByskus(@RequestParam List<String> skus) {
		List<Product> products = productUseCase.findByIds(skus);
		return ResponseEntity.ok(Result.ok(mapper.toProductResponseList(products)));
	}

}
