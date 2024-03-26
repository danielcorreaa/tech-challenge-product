
# Microsserviço tech-challenge-product

Microsserviço responsável pelo gerenciamento de produtos


## Autores

- [@danielcorreaa](https://github.com/danielcorreaa)

## Stack utilizada


**Back-end:** Java, Spring Boot, Mongodb


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
| `listaSkus`      | `List` | **Obrigatório**. Lista de skus dos produtos|


#### Excluir produto
```http
  DELETE api/v1/customers/"delete/{sku}"
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `sku`      | `string` | **Obrigatório**. Sku do produto que você quer excluir |




## OWASP ZAP
*Realizei ataque na api usando o OWASP Zap, e deu apenas um alerta de nível baixo, fiz a correção, segue links com o antes e depois*

- [@report-before](https://danielcorreaa.github.io/tech-challenge-product/before/cardapio/2024-03-11-ZAP-Report-.html)


- [@report-after](https://danielcorreaa.github.io/tech-challenge-product/after/cardapio/2024-03-11-ZAP-Report-.html)

## Relatório RIPD
*RELATÓRIO DE IMPACTO À PROTEÇÃO DE DADOS PESSOAIS*

- [@RIPD](https://danielcorreaa.github.io/tech-challenge-product/RIPD.pdf)



#### Desenhos

- [@Desenho Padrão Saga coreografado.](https://danielcorreaa.github.io/tech-challenge-orders/images/saga-diagrama.png)


- [@Desenho arquitetura.](https://danielcorreaa.github.io/tech-challenge-orders/images/diagrama-arquitetura.png)



## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-product.git
```

Entre no diretório do projeto

```bash
  cd tech-challenge-product
```

Docker

```bash
  docker compose up -d
```

No navegador

```bash
  http://localhost:8085/
```



## Deploy

### Para subir a aplicação usando kubernetes

#### Infraestrutura:

Clone o projeto com a infraestrutura

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-infra-terraform-kubernetes.git
```
Entre no diretório do projeto

```bash
  cd tech-challenge-infra-terraform-kubernetes/
````

Execute os comandos

```bash   
- run: kubectl apply -f kubernetes/metrics.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-secrets.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-configmap.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-pvc.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-service.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-statefulset.yaml

````

#### Aplicação:

docker hub [@repositorio](https://hub.docker.com/r/daniel36/tech-challenge-product/tags)

Clone o projeto

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-product.git
```

Entre no diretório do projeto

```bash
  cd tech-challenge-product
```

Execute os comandos
```bash   
- run: kubectl apply -f k8s/products-deployment.yaml
- run: kubectl apply -f k8s/products-service.yaml
- run: kubectl apply -f k8s/products-hpa.yaml

````

