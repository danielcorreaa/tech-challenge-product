
## Documentação da API

### Cadastro, atualização e buscas de produtos

#### Cadastrar produto

```http
  POST api/v1/products
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `sku` | `string` | **Obrigatório**. Sku do produto |
| `title` | `string` | **Obrigatório**. Título do produto |
| `category` | `enum` | **Obrigatório**  Categoria do produto( LANCHE, ACOMPANHAMENTO, BEBIDA, SOBREMESA) |
| `description` | `string` | **Obrigatório**. Descrição do produto |
| `price` | `BigDecimal` | **Obrigatório**. Preço do produto |
| `image` | `string` |  Imagem do produto |

#### Atualizar produto

```http
  PUT api/v1/products
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `sku` | `string` | **Obrigatório**  Sku do produto |
| `title` | `string` |  Título do produto |
| `category` | `enum` |  Categoria do produto( LANCHE, ACOMPANHAMENTO, BEBIDA, SOBREMESA) |
| `description` | `string` |  Descrição do produto |
| `price` | `BigDecimal` |  Preço do produto |
| `image` | `string` |  Imagem do produto |


#### Buscar produtos por categoria
```http
  GET api/v1/products/{category}/LANCHE?page=0&size=10
```

| Parâmetro   | Tipo  | Default     | Descrição                                   |
| :---------- | :--------- |------- |:------------------------------------------ |
| `category`      | `string` | |**Obrigatório**. categorias para pesquisa: LANCHE, ACOMPANHAMENTO, BEBIDA, SOBREMESA |
| `page`      | `int` | 0| Valor 0 retornara do primeiro registro até o valor  do size|
| `size`      | `int` | 10 |Quantidade de registro que retornaram na resposta|

#### Buscar produtos por sku
```http
  GET api/v1/products/find/{sku}
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `sku`      | `string` | **Obrigatório**. Sku do produto|

#### Buscar produtos por skus
```http
  GET /api/v1/products/find?skus={listaSkus}
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `listaSkusku`      | `List` | **Obrigatório**. Lista de skus dos produtos|


#### Excluir produto
```http
  DELETE api/v1/customers/"delete/{sku}"
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `sku`      | `string` | **Obrigatório**. Sku do produto que você quer excluir |
