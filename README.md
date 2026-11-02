# Árvore R

Implementação de uma Árvore R em Java, baseada no artigo *R-Trees: A Dynamic Index Structure for Spatial Searching*, de Antonin Guttman e Michael Stonebraker (1983).

Trabalho da disciplina Resolução de Problemas Estruturados em Computação (RPEC) - Ciência da Computação, 4º período - PUCPR.

## Vídeo de apresentação

https://youtu.be/Iv3bWaEn2QM

## O que é

A Árvore R é uma estrutura de índice para dados espaciais, ou seja, objetos que ocupam área, e não apenas pontos. Cada objeto é guardado junto com o seu retângulo envolvente mínimo (MBR), e os retângulos são agrupados em caixas maiores à medida que a árvore sobe. A busca fica eficiente porque descarta regiões inteiras que não cruzam a consulta, em vez de percorrer todos os registros.

Diferente de uma árvore binária de busca, os retângulos de nós irmãos podem se sobrepor. Por isso uma busca pode precisar descer por mais de um caminho.

## Operações

- `inserir(id, minX, minY, maxX, maxY)` - insere um registro, escolhendo a folha de menor aumento de área e dividindo o nó se ele estourar
- `buscar(minX, minY, maxX, maxY)` - imprime os ids dos registros que cruzam a região consultada
- `remover(id)` - remove o registro, condensa a árvore e reinsere as entradas órfãs

A capacidade dos nós é M = 4 e m = 2.

## Estrutura do código

Arquivo único, `ArvoreR.java`, com três classes internas:

- `Retangulo` - os limites do MBR e as operações de área, sobreposição, união e aumento
- `Entrada` - um registro (com id) ou um ponteiro para um nó filho
- `No` - folha ou nó interno, com o vetor de entradas e a referência ao pai

A divisão de nós usa o algoritmo de divisão quadrática do artigo. A remoção segue os quatro passos do `Delete`, incluindo a condensação com reinserção.

O código não usa nenhum `import`. Tudo foi feito com vetores, laços e condicionais.


## Integrantes

- Hugo Souza Jamielniak
- João Pedro Macena Correa
- Ana Luiza Bruginski de Melo
- Nathan Bueno Catenacci Okazaki

## Referência

GUTTMAN, A.; STONEBRAKER, M. R-Trees: A Dynamic Index Structure for Spatial Searching. In: Proceedings of the 1984 ACM SIGMOD International Conference on Management of Data, 1984. p. 47-57.
