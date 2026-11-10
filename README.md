# Sistema de Controle de Cursos

Desafio final do **Módulo 5 (3035 Teach) — Back-end Java e Banco de Dados**.

Aplicação de console para uma escola de cursos livres. O sistema cadastra alunos, cursos e matrículas, grava tudo no PostgreSQL com JPA (Hibernate) e gera um relatório de engajamento por curso.

## O que cada opção faz

| Opção | Função |
| --- | --- |
| 1 | Cadastra um aluno (nome, e-mail e data de nascimento) |
| 2 | Lista todos os alunos |
| 3 | Busca um aluno pelo e-mail |
| 4 | Cadastra um curso (nome, descrição e carga horária) |
| 5 | Lista todos os cursos |
| 6 | Busca cursos pelo nome ou por parte dele |
| 7 | Matricula um aluno em um curso |
| 8 | Lista as matrículas com o nome do aluno e o nome do curso |
| 9 | Relatório de engajamento (bônus): total de alunos, média de idade e matrículas dos últimos 30 dias |
| 0 | Encerra o programa |

## Validações

- O e-mail precisa ter um formato válido (`nome@escola.com`) e não pode se repetir. A comparação ignora maiúsculas e minúsculas.
- O mesmo aluno não pode ser matriculado duas vezes no mesmo curso.
- Data inválida, data futura e número inválido (ou menor ou igual a zero na carga horária) fazem o sistema pedir o valor de novo, sem encerrar.
- Campo obrigatório em branco também é pedido de novo.
- Na matrícula, um id inexistente é recusado. Digite `0` para cancelar.
- Na data da matrícula, Enter usa a data de hoje.

As datas são pedidas no formato brasileiro `dd/MM/aaaa`. Exemplo: `15/03/2001`.

## Tecnologias

- Java 17 ou superior
- Maven
- Hibernate 6 como implementação de JPA
- Driver JDBC do PostgreSQL
- Sem Spring: o acesso ao banco fica em `JPAUtil` + repositórios com `EntityManager`

## Estrutura dos pacotes

```
br.com.escola
├── Main.java                 ponto de entrada
├── model                     Aluno, Curso e Matricula (anotações JPA)
├── dto                       linha do relatório de engajamento
├── repository                consultas JPQL e o SQL agregado do relatório
├── service                   regras de negócio
├── view                      menus com Scanner
├── util                      JPAUtil e validação de e-mail
└── exception                 erros de regra e fim de entrada
```

O relacionamento da matrícula é **ManyToOne** com aluno e **ManyToOne** com curso. As tabelas `aluno`, `curso` e `matricula` são criadas e atualizadas pelo Hibernate a partir das anotações (`hibernate.hbm2ddl.auto=update`). Não é obrigatório rodar o SQL na mão para usar o programa.

## Banco de dados

Valor padrão da conexão (pode ser trocado por variável de ambiente):

| Variável | Padrão |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/escola` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |

Exemplo:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/escola
export DB_USER=postgres
export DB_PASSWORD=postgres
```

Se a variável não existir, vale o que está em `src/main/resources/META-INF/persistence.xml`.

### Subir o PostgreSQL com Docker

Na raiz do projeto:

```bash
docker compose up -d
```

O serviço já cria o banco `escola` com usuário e senha `postgres`. Para parar:

```bash
docker compose down
```

### Sem Docker

Com o PostgreSQL instalado, crie o banco (as tabelas a aplicação cria sozinha):

```sql
CREATE DATABASE escola;
```

O usuário da conexão precisa poder criar tabelas nesse banco. O arquivo `sql/consultas.sql` mostra o `CREATE TABLE` equivalente, os joins das listagens e o `SELECT` agregado do relatório.

## Como executar

Requisito: JDK 17+ e Maven no `PATH`, e o PostgreSQL acessível.

Pelo Maven, sem gerar o jar:

```bash
mvn compile exec:java
```

Gerando o jar executável (com as dependências dentro):

```bash
mvn package
java -jar target/sistema-controle-cursos.jar
```

Para ver o SQL que o Hibernate envia ao banco, altere `hibernate.show_sql` para `true` no `persistence.xml` e compile de novo.

### Exemplo automatizado

O arquivo `scripts/exemplo-entrada.txt` percorre o menu: cadastro com data, e-mail e número inválidos (o programa pede de novo), e-mail repetido, busca de aluno e de curso, matrícula (incluindo data futura rejeitada e matrícula duplicada) e o relatório. Com o banco vazio:

```bash
java -jar target/sistema-controle-cursos.jar < scripts/exemplo-entrada.txt
```

## Relatório de engajamento

Para cada curso o relatório mostra:

- total de alunos matriculados
- média de idade, em anos completos (`AGE` do PostgreSQL)
- quantas matrículas foram feitas nos últimos 30 dias (de hoje até 30 dias atrás, inclusive)

Curso sem aluno entra no relatório com total zero e média "sem dados". A consulta está em `MatriculaRepository` e, em SQL puro, no final de `sql/consultas.sql`.
