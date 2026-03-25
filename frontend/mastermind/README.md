# Mastermind

Este repositório contém o website (SPA) do projeto **Mastermind**.

(Veja a pasta images) na raiz desse repositório

A solução foi desenvolvida utilizando Angular na versão 19. Optei por essa versão por ser relativamente recente e estável, considerando que o Angular passa por mudanças frequentes entre releases, o que pode impactar significativamente suas características e funcionamento.

## Tecnologias utilizadas

- HTML
- CSS
- Typescript

Outros
---
- NodeJs Versão 22.22 *1
- Angular CLI Versão 21.2.2 *2
- npm Versão 10.9.4

**1* Essa versão foi escolhida, pois além de recente é compátivel com com o Angular CLI (que às vezes reclama ou dá conflito com a versão do Node.js<br>
**2* Embora a versão do Angular CLI ser a 21, o projeto foi desenvolvido usando a versão 19


# Execução rápida

## Ferramentas necessárias

- NodeJs Versão 22.22
- Angular CLI 21.2.2

1) Com o Node.js instalado na sua máquina. Abra um terminal (Bash - Linux ou Git bash - Windows)

2) Vamos instalar o NodeJs versão 22.22. Digite no terminal ``nvm install 22.22``

3) Vamos ativar o uso dessa versão do NodeJs ``nvm use  22.22``. Não se preocupe, a versão padrão do seu Angular não vai mudar, somente durante a execução desse terminal ela vai ser a 22.22

4) Com o Node.js versão 22.22 instalado execute a instalação do Angular CLI: ``npm install -g @angular/cli``. Atenção com o -g! Ele irá installar o Angular CLI no repositorio Global do Node. Se não quiser isso, remova o parâmetro -g.

5) Verifique a versão do Angular ``ng version``

6) Execute o comando ``npm install`` para instalar as depêndencias do projeto

7) Para visualizar o site execute ``ng serve``, o website site está disponível na porta 4200. para acessá-lo entre:
http://localhost:4200

> [!NOTE]
> Para o site funcionar 100% é preciso que a API + o Banco de dados já estejam no Ar. Para facilitar quaisquer ajuste de conexão com o API, uma tela que permite você alterar o endereço da API foi coloca na tela de Login e dentro do Perfil

![API change menu](https://github.com/phrxn/phrxn/blob/master/mastermind/website/api_change_address_example.png)


