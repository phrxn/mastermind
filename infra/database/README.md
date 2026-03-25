# Mastermind Database

Este repositório contém os scripts SQL responsáveis pela criação das tabelas do projeto **Mastermind**.

![Database schema](https://raw.githubusercontent.com/phrxn/phrxn/refs/heads/master/mastermind/database_schema.png)

O banco de dados utilizado é o MariaDB. Além de ser uma solução robusta, é open source e se apresenta como uma alternativa ao MySQL, que, apesar de possuir uma versão community, é mantido pela Oracle.

## Estrutura

O banco é composto por 3 tabelas principais:

users
games
guesses
Relações

users 1 : N games <br>
games 1 : N guesses<br>

Ou seja:

Um usuário pode possuir vários games;<br>
Um game pode possuir vários guesses;<br>
Embora um game possa possuir vários guesses, o sistema limita cada game a no máximo 10 guesses

Campos

Nas tabelas users e games, existe uma coluna chamada uuid_public.
Essa coluna foi criada com o objetivo de evitar a exposição dos IDs internos dos registros.

Determinadas operações exigem um identificador fornecido pelo usuário.
Por exemplo: consulta de um jogo específico.