
public class ArvoreR {

    private static final int MAX = 4;
    private static final int MIN = 2;

    static class Retangulo {
        int minX, minY, maxX, maxY;

        Retangulo(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        int area() {
            return (maxX - minX) * (maxY - minY);
        }

        boolean sobrepoe(Retangulo outro) {
            return minX <= outro.maxX && maxX >= outro.minX
                && minY <= outro.maxY && maxY >= outro.minY;
        }

        Retangulo unir(Retangulo outro) {
            int x1 = minX < outro.minX ? minX : outro.minX;
            int y1 = minY < outro.minY ? minY : outro.minY;
            int x2 = maxX > outro.maxX ? maxX : outro.maxX;
            int y2 = maxY > outro.maxY ? maxY : outro.maxY;

            return new Retangulo(x1, y1, x2, y2);
        }

        int aumento(Retangulo outro) {
            return unir(outro).area() - area();
        }
    }

    static class Entrada {
        Retangulo retangulo;
        No filho;
        int id;

        Entrada(Retangulo retangulo, int id) {
            this.retangulo = retangulo;
            this.id = id;
            this.filho = null;
        }

        Entrada(Retangulo retangulo, No filho) {
            this.retangulo = retangulo;
            this.filho = filho;
            this.id = -1;
        }
    }

    static class No {
        boolean folha;
        int quantidade;
        Entrada[] entradas;
        No pai;

        No(boolean folha) {
            this.folha = folha;
            this.quantidade = 0;
            this.entradas = new Entrada[MAX + 1];
            this.pai = null;
        }
    }

    private No raiz;

    public ArvoreR() {
        raiz = new No(true);
    }

    private Retangulo retanguloDoNo(No no) {
        Retangulo resultado = no.entradas[0].retangulo;

        for (int i = 1; i < no.quantidade; i++) {
            resultado = resultado.unir(no.entradas[i].retangulo);
        }

        return resultado;
    }

    private void adicionar(No no, Entrada entrada) {
        no.entradas[no.quantidade] = entrada;
        no.quantidade++;

        if (entrada.filho != null) {
            entrada.filho.pai = no;
        }
    }

    private void removerEntrada(No no, int indice) {
        for (int i = indice; i < no.quantidade - 1; i++) {
            no.entradas[i] = no.entradas[i + 1];
        }

        no.quantidade--;
        no.entradas[no.quantidade] = null;
    }

    private void atualizarRetanguloPai(No no) {
        if (no.pai != null && no.quantidade > 0) {
            for (int i = 0; i < no.pai.quantidade; i++) {
                if (no.pai.entradas[i].filho == no) {
                    no.pai.entradas[i].retangulo = retanguloDoNo(no);
                }
            }
        }
    }

    private No escolherFolha(Retangulo retangulo) {
        No atual = raiz;

        while (!atual.folha) {
            int escolhido = 0;
            int menorAumento =
                atual.entradas[0].retangulo.aumento(retangulo);

            int menorArea = atual.entradas[0].retangulo.area();

            for (int i = 1; i < atual.quantidade; i++) {
                Retangulo r = atual.entradas[i].retangulo;
                int aumento = r.aumento(retangulo);
                int area = r.area();

                if (aumento < menorAumento
                    || (aumento == menorAumento && area < menorArea)) {
                    escolhido = i;
                    menorAumento = aumento;
                    menorArea = area;
                }
            }

            atual = atual.entradas[escolhido].filho;
        }

        return atual;
    }

    public void inserir(int id, int minX, int minY, int maxX, int maxY) {
        if (minX > maxX || minY > maxY) {
            System.out.println("Retangulo invalido.");
            return;
        }

        Retangulo retangulo =
            new Retangulo(minX, minY, maxX, maxY);

        No folha = escolherFolha(retangulo);
        adicionar(folha, new Entrada(retangulo, id));
        ajustar(folha);
    }

    private void ajustar(No no) {
        while (no != null) {
            if (no.quantidade > MAX) {
                No irmao = dividir(no);

                if (no.pai == null) {
                    No novaRaiz = new No(false);

                    adicionar(novaRaiz,
                        new Entrada(retanguloDoNo(no), no));
                    adicionar(novaRaiz,
                        new Entrada(retanguloDoNo(irmao), irmao));

                    raiz = novaRaiz;
                    no.pai = novaRaiz;
                    irmao.pai = novaRaiz;
                    return;
                }

                No pai = no.pai;
                atualizarRetanguloPai(no);
                adicionar(pai,
                    new Entrada(retanguloDoNo(irmao), irmao));
                no = pai;
            } else {
                atualizarRetanguloPai(no);
                no = no.pai;
            }
        }
    }

    private No dividir(No no) {
        Entrada[] antigas = new Entrada[MAX + 1];

        for (int i = 0; i < no.quantidade; i++) {
            antigas[i] = no.entradas[i];
        }

        int total = no.quantidade;
        No irmao = new No(no.folha);

        no.quantidade = 0;

        for (int i = 0; i < no.entradas.length; i++) {
            no.entradas[i] = null;
        }

        boolean[] usada = new boolean[MAX + 1];

        int s1 = 0;
        int s2 = 1;
        int maiorDesperdicio = -1;

        for (int i = 0; i < total; i++) {
            for (int j = i + 1; j < total; j++) {
                Retangulo uniao =
                    antigas[i].retangulo.unir(antigas[j].retangulo);

                int desperdicio = uniao.area()
                    - antigas[i].retangulo.area()
                    - antigas[j].retangulo.area();

                if (desperdicio > maiorDesperdicio) {
                    maiorDesperdicio = desperdicio;
                    s1 = i;
                    s2 = j;
                }
            }
        }

        adicionar(no, antigas[s1]);
        adicionar(irmao, antigas[s2]);
        usada[s1] = true;
        usada[s2] = true;

        int distribuidas = 2;

        while (distribuidas < total) {
            if (no.quantidade + (total - distribuidas) == MIN) {
                for (int i = 0; i < total; i++) {
                    if (!usada[i]) {
                        adicionar(no, antigas[i]);
                        usada[i] = true;
                        distribuidas++;
                    }
                }
                break;
            }

            if (irmao.quantidade + (total - distribuidas) == MIN) {
                for (int i = 0; i < total; i++) {
                    if (!usada[i]) {
                        adicionar(irmao, antigas[i]);
                        usada[i] = true;
                        distribuidas++;
                    }
                }
                break;
            }

            int escolhido = -1;
            int maiorDiferenca = -1;

            for (int i = 0; i < total; i++) {
                if (!usada[i]) {
                    int a = retanguloDoNo(no).aumento(
                        antigas[i].retangulo);
                    int b = retanguloDoNo(irmao).aumento(
                        antigas[i].retangulo);

                    int diferenca = a - b;
                    if (diferenca < 0) {
                        diferenca = -diferenca;
                    }

                    if (escolhido == -1
                        || diferenca > maiorDiferenca) {
                        escolhido = i;
                        maiorDiferenca = diferenca;
                    }
                }
            }

            Retangulo r = antigas[escolhido].retangulo;
            int aumentoA = retanguloDoNo(no).aumento(r);
            int aumentoB = retanguloDoNo(irmao).aumento(r);

            if (aumentoA < aumentoB) {
                adicionar(no, antigas[escolhido]);
            } else if (aumentoB < aumentoA) {
                adicionar(irmao, antigas[escolhido]);
            } else if (retanguloDoNo(no).area()
                       < retanguloDoNo(irmao).area()) {
                adicionar(no, antigas[escolhido]);
            } else if (retanguloDoNo(irmao).area()
                       < retanguloDoNo(no).area()) {
                adicionar(irmao, antigas[escolhido]);
            } else if (no.quantidade <= irmao.quantidade) {
                adicionar(no, antigas[escolhido]);
            } else {
                adicionar(irmao, antigas[escolhido]);
            }

            usada[escolhido] = true;
            distribuidas++;
        }

        irmao.pai = no.pai;
        return irmao;
    }

    public void buscar(int minX, int minY, int maxX, int maxY) {
        Retangulo consulta =
            new Retangulo(minX, minY, maxX, maxY);

        System.out.println("Registros encontrados:");
        buscarRecursivo(raiz, consulta);
    }

    private void buscarRecursivo(No no, Retangulo consulta) {
        for (int i = 0; i < no.quantidade; i++) {
            Entrada entrada = no.entradas[i];

            if (entrada.retangulo.sobrepoe(consulta)) {
                if (no.folha) {
                    System.out.println("ID: " + entrada.id);
                } else {
                    buscarRecursivo(entrada.filho, consulta);
                }
            }
        }
    }

    private boolean encontrar(No no, int id, No[] folhaEncontrada,
                              int[] indiceEncontrado) {
        for (int i = 0; i < no.quantidade; i++) {
            if (no.folha) {
                if (no.entradas[i].id == id) {
                    folhaEncontrada[0] = no;
                    indiceEncontrado[0] = i;
                    return true;
                }
            } else {
                if (encontrar(no.entradas[i].filho, id,
                              folhaEncontrada, indiceEncontrado)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void coletarRegistros(No no, Entrada[] registros,
                                  int[] quantidade) {
        for (int i = 0; i < no.quantidade; i++) {
            if (no.folha) {
                registros[quantidade[0]++] = no.entradas[i];
            } else {
                coletarRegistros(no.entradas[i].filho,
                                 registros, quantidade);
            }
        }
    }

    private void removerFilho(No pai, No filho) {
        for (int i = 0; i < pai.quantidade; i++) {
            if (pai.entradas[i].filho == filho) {
                removerEntrada(pai, i);
                return;
            }
        }
    }

    public boolean remover(int id) {
        No[] folhaEncontrada = new No[1];
        int[] indiceEncontrado = new int[1];

        if (!encontrar(raiz, id, folhaEncontrada, indiceEncontrado)) {
            System.out.println("Registro nao encontrado: " + id);
            return false;
        }

        No folha = folhaEncontrada[0];
        removerEntrada(folha, indiceEncontrado[0]);

        Entrada[] registrosOrfaos = new Entrada[10000];
        int[] quantidadeOrfaos = new int[1];

        No atual = folha;

        while (atual != raiz) {
            No pai = atual.pai;

            if (atual.quantidade < MIN) {
                coletarRegistros(atual, registrosOrfaos,
                                 quantidadeOrfaos);
                removerFilho(pai, atual);
            } else {
                atualizarRetanguloPai(atual);
            }

            atual = pai;
        }

        if (raiz.quantidade == 0) {
            raiz = new No(true);
        } else if (!raiz.folha && raiz.quantidade == 1) {
            raiz = raiz.entradas[0].filho;
            raiz.pai = null;
        }

        for (int i = 0; i < quantidadeOrfaos[0]; i++) {
            Entrada registro = registrosOrfaos[i];
            inserir(registro.id,
                    registro.retangulo.minX,
                    registro.retangulo.minY,
                    registro.retangulo.maxX,
                    registro.retangulo.maxY);
        }

        System.out.println("Registro removido: " + id);
        return true;
    }

    public static void main(String[] args) {
        ArvoreR arvore = new ArvoreR();

        arvore.inserir(1, 0, 0, 10, 10);
        arvore.inserir(2, 5, 5, 15, 15);
        arvore.inserir(3, 20, 20, 30, 30);
        arvore.inserir(4, 8, 8, 12, 12);
        arvore.inserir(5, 25, 25, 35, 35);
        arvore.inserir(6, 2, 20, 9, 28);

        arvore.buscar(7, 7, 13, 13);

        arvore.remover(2);

        arvore.buscar(7, 7, 13, 13);
    }
}
