# language: pt
Funcionalidade: API - Products

  @smoke
  Cenário: Cadastrar Produto
    Dado que quero cadastrar um novo produto
    Quando quando informar todos os campos obrigatórios
    Entao devo conseguir cadastrar o produto

  Cenário: Atualizar Produto
    Dado que tenho um produto cadastrado
    E quero atualizar alguns dados
    Entao devo conseguir atualizar os dados

  Cenário: Busca um Produto
    Dado que preciso buscar um produto específico
    Quando fizer a requisição informando o sku
    Entao devo obter o produto do sku informado

  Cenário: Busca vários Produtos
    Dado que preciso buscar vários produtos
    Quando informo vários skus
    Entao devo conseguir obter todos dos skus informados

  Cenário: Busca por Categorias
    Dado que preciso buscar todos os produtos de uma categoria
    Quando informar uma categoria
    Entao devo conseguir obter todos os produtos dessa categoria

  Cenário: Delete Produto
    Dado que preciso deletar um produto
    Quando informar o sku do produto
    Entao o produto deve deixar de existir na consulta

