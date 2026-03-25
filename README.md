# Mastermind

Este projeto consiste no desenvolvimento de uma aplicação web do jogo clássico Mastermind.
O objetivo foi construir uma solução full-stack completa, partindo do zero, com foco em boas práticas e uma experiência funcional para o usuário na sua parte Web.

O projeto foi dividido em 3 parte

- Infra estrutura
	- Banco de dados MariaDB
- frontend
	- Website desenvolvivo usando Angular19
- backend
	- API RESTFul desenvolvida usando Spring Boot

Para entender o que cada parte faz e ver exemplos acesse as pastas de cada parte.

Para rodar o projeto você irá precisar de

A) Docker<br>
- A opção mais recomendada, pois toda a aplicação foi "dockerizada" para facilitar sua execução.<br>
- A versão do Docker utilizada foi a 29.3.0.

B) Caso você não queria usar o Docker terá que instalar os seguintes programas:

- Java versão 21
- maven versao 3.9.9
- node versão 22.22.0 (tem que ser a 22 por causa da versão do CLI do Angular)
- npm 10.9.4
- angular cli 19.2.22
- banco de dados MariaDB

> [!NOTE]
> Dentro das pastas das parte do projeto há outro README.md detalhando a instação.

## Execução usando Docker

1) Depois de clonar esse repositório, preencha as variáveis de ambiente dentro do arquivo ``.env``.
Dentro do arquivo há comentários do que é cada variável

2) Abra o terminal e execute o comando abaixo do docker compose:<br>
	``docker compose up -d``

Esse comando irá executar o docker compose e irá montar todo o ambiente. No fim da execução basta acessar as portas que foram configuradas no <code>.env</code>

Para ver o website, acesse a porta ``WEBSERVER_HOST_PORT``

## Execução sem Docker

Para executar sem o Docker será preciso subir cada ambiente manualmente.

1) Na pasta de cada parte do código há um tutorial de com executar cada parte.

2) Primeira pasta ``infra/database``, nela há o banco de dados.

3) Segunda pasta ``backend/mastermind``, nela há o banco de dados.

4) Terceira pasta ``frontend/mastermind``, nela há o site da aplicação.

# Docker schema

![Docker schema](https://raw.githubusercontent.com/phrxn/phrxn/refs/heads/master/mastermind/docker_schema.png)

