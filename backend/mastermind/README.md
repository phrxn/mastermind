# Mastermind API

Este repositório contém a API do projeto **Mastermind**.

![API Showroom](https://github.com/phrxn/phrxn/blob/master/mastermind/api_showroom.gif)


A aplicação foi desenvolvida em **Java**, utilizando o **Spring Framework**. A escolha por essa stack se deve ao fato de o Spring ser uma solução consolidada no mercado, amplamente utilizada e com um ecossistema robusto e em constante evolução.

## Tecnologias utilizadas

Principais projetos do ecossistema Spring utilizados:

- Spring Boot
- Spring Web
- Spring Security
- SpringDoc OpenAPI

Outros
---
- Maven como gerenciador de projeto
- Java JDK versão 21 [**A escolha da versão 21 se deve ao fato de ser a versão LTS anterior à versão LTS atual (25)**]

---

# Escolhas técnicas

## Documentação da API

Para documentar a API, foi escolhido o Swagger.

Por meio dele, é possível visualizar todos os endpoints disponíveis, entender os contratos e testar as requisições diretamente pelo navegador.

Com a API em execução, acesse em: `http://localhost:8080/api/v1/swagger-ui/index.html`

## Autenticação com JWT

Foi adotado o uso de **JWT (JSON Web Token)** como mecanismo de autenticação, por ser uma abordagem segura e padrão de mercado para APIs.

Principais vantagens:

- Permite identificar quem está acessando o sistema
- Possibilita incluir informações no token, trafegando entre cliente e servidor
- Dispensa o uso de sessões no servidor, diferentemente de **Session + Cookies**
- Permite definir tempo de expiração, garantindo segurança após o vencimento do token

## Persistência

- Foi optado pela utilização de **JPA** em vez de JDBC, considerando que as queries não são complexas

## Segurança dos IDs

- Para as entidades que precisam enviar um valor de identificação único (ID) para o lado do cliente, foi implementada uma propriedade chamada `uuid_publico`, para proteger o valor real do ID

## Validações

Foi implementado um sistema de validação para as requisições do cliente, a fim de garantir que os dados respeitem as regras de negócio antes do processamento

## Tratamento de erros padronizado

As respostas de erro seguem um padrão estruturado:

```json
{
  "status": XXX,
  "error": "mensagem de erro"
}
```

# Execução rápida

**Somente será necessária, se você não utilzar o docker**

## Ferramentas necessárias

- JDK versão 21
- Maven versão 3.9.9

Dentro desta pasta, abra o seu terminal [Se você está utilizando Linux, opte pelo bash. Se você está usando Windows, opte pelo **Git Bash**]

### Linux

1) Execute `chmod +x mvnw`
2) Execute o código abaixo

```bash
export \
MARIADB_ROOT_PASSWORD=XXXX \
MARIADB_DATABASE=XXXX \
MARIADB_USER=XXXX \
MARIADB_PASSWORD=XXXX \
MARIADB_HOST=XXXX \
MARIADB_PORT=XXXX \
JWT_SECRET=XXXX \
JWT_EXPIRATION_SECONDS=XXXX ; ./mvnw -f ./pom.xml spring-boot:run
```

### Windows

1) Execute o código abaixo

```bash
export \
MARIADB_ROOT_PASSWORD=XXXX \
MARIADB_DATABASE=XXXX \
MARIADB_USER=XXXX \
MARIADB_PASSWORD=XXXX \
MARIADB_HOST=XXXX \
MARIADB_PORT=XXXX \
JWT_SECRET=XXXX \
JWT_EXPIRATION_SECONDS=XXXX ; ./mvnw.cmd -f ./pom.xml spring-boot:run
```

> [!IMPORTANT]
> Repare que os valores XXXX acima são campos que DEVEM ser preenchidos. A descrição deles está no arquivo `.env` na pasta raiz deste projeto.

Se tudo der certo, o Spring vai "subir".

# Compilação

> [!NOTE]
> Essa parte é necessária se você quiser criar o `.jar` para executá-lo

Dentro desta pasta, abra o seu terminal [Se você está utilizando Linux, opte pelo bash. Se você está usando Windows, opte pelo **Git Bash**]

1) digite o comando `mvn clean package`

2) uma pasta chamada `target` será criada e dentro dela terá o arquivo `.jar` final. O nome será `mastermind-X.X.X.jar` (onde X.X.X é a versão atual do projeto).

3) Para executar, de dentro da pasta `target` digite:

```bash
export \
MARIADB_ROOT_PASSWORD=XXXX \
MARIADB_DATABASE=XXXX \
MARIADB_USER=XXXX \
MARIADB_PASSWORD=XXXX \
MARIADB_HOST=XXXX \
MARIADB_PORT=XXXX \
JWT_SECRET=XXXX \
JWT_EXPIRATION_SECONDS=XXXX ; java -jar mastermind-1.0.0.jar
```