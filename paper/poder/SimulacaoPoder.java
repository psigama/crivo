import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.SplittableRandom;

/**
 * Simulação de poder do protocolo P1 (artigo técnico, seção 4.5).
 *
 * <p>Roda só com o JDK, sem compilar nada antes e sem dependência:
 *
 * <pre>
 *   java paper/poder/SimulacaoPoder.java
 * </pre>
 *
 * <p>O modelo gerador, declarado no artigo:
 * <ul>
 *   <li>As respostas verdadeiras do alvo e do chamariz de um par são sorteadas, de forma independente, das
 *       frequências supostas de cada opção num estrato típico (abaixo). Estão aqui só para dar a taxa de
 *       coincidência por acaso de cada campo; não fazem parte do protocolo nem do pré-registro.</li>
 *   <li>Cada consulente marca "não sei" em cada campo com probabilidade {@link #NAO_SEI}.</li>
 *   <li>Em cada campo, o médium marca "não recebi" com probabilidade {@link #NAO_RECEBI}; senão, com
 *       probabilidade θ (a taxa de informação), dá a resposta verdadeira de quem tem o nome que recebeu; senão,
 *       chuta segundo as mesmas frequências. Com θ = 0, vale a hipótese nula.</li>
 *   <li>A nota do par é d = [s(R_A,A) − s(R_A,B)] + [s(R_B,B) − s(R_B,A)], sobre os campos que os dois
 *       consulentes responderam, como em {@code structured.StructuredScoring}.</li>
 *   <li>O teste é o Wilcoxon unilateral da plataforma ({@code analysis.WilcoxonSignedRank}): zeros
 *       descartados, postos médios, exato até 20 diferenças não nulas, aproximação normal com correção de
 *       empates e de continuidade acima disso; α = 0,05.</li>
 * </ul>
 *
 * <p>Por campo, a diferença esperada entre a taxa de coincidência com o alvo e com o chamariz é
 * Δ = (1 − NAO_RECEBI) · θ · (1 − Σp²): um campo em que uma opção domina rende pouco sinal, qualquer que seja θ.
 * A semente é fixa, para que qualquer pessoa obtenha os mesmos números.
 */
public final class SimulacaoPoder {

    static final double NAO_SEI = 0.08;
    static final double NAO_RECEBI = 0.10;
    static final double ALFA = 0.05;
    static final long SEMENTE = 20261118L;
    static final int REPETICOES = 4000;

    /** v2-rascunho: os 20 campos da nota, com frequências supostas para um estrato de 60 a 79 anos. */
    static final double[][] V2 = {
            {0.35, 0.25, 0.40},                                          // como foi a morte
            {0.33, 0.22, 0.15, 0.06, 0.16, 0.04, 0.04},                  // causa principal
            {0.65, 0.25, 0.03, 0.07},                                    // onde faleceu
            {0.10, 0.55, 0.22, 0.13},                                    // estado civil
            {0.10, 0.15, 0.27, 0.22, 0.26},                              // quantos filhos
            {0.06, 0.22, 0.45, 0.27},                                    // lugar entre os irmãos
            {0.45, 0.20, 0.08, 0.10, 0.07, 0.05, 0.05},                  // parentesco de quem pede
            {0.40, 0.20, 0.25, 0.15},                                    // escolaridade
            {0.10, 0.18, 0.14, 0.06, 0.14, 0.05, 0.07, 0.03, 0.15, 0.08}, // ocupação
            {0.40, 0.42, 0.18},                                          // onde viveu
            {0.50, 0.50},                                                // sociabilidade
            {0.25, 0.25, 0.15, 0.15, 0.12, 0.08},                        // traço marcante
            {0.25, 0.50, 0.25},                                          // estatura
            {0.30, 0.40, 0.30},                                          // porte físico
            {0.30, 0.07, 0.45, 0.18},                                    // cabelo
            {0.70, 0.30},                                                // óculos
            {0.50, 0.22, 0.10, 0.03, 0.03, 0.12},                        // religião
            {0.15, 0.12, 0.18, 0.08, 0.12, 0.10, 0.07, 0.06, 0.12},      // passatempo
            {0.55, 0.25, 0.20},                                          // fumo
            {0.40, 0.12, 0.06, 0.04, 0.38},                              // animal de estimação
    };

    /** v1-rascunho: os 13 campos da nota, com as mesmas suposições, para comparação. */
    static final double[][] V1 = {
            {0.40, 0.60},                                                // como foi a morte
            {0.35, 0.22, 0.12, 0.04, 0.04, 0.23},                        // causa principal
            {0.65, 0.25, 0.03, 0.07},                                    // onde faleceu
            {0.10, 0.55, 0.22, 0.13},                                    // estado civil
            {0.90, 0.10},                                                // tinha filhos
            {0.10, 0.20, 0.14, 0.18, 0.05, 0.07, 0.15, 0.11},            // ocupação
            {0.40, 0.42, 0.18},                                          // onde viveu
            {0.50, 0.50},                                                // temperamento
            {0.25, 0.50, 0.25},                                          // estatura
            {0.45, 0.40, 0.15},                                          // religiosidade
            {0.15, 0.12, 0.18, 0.08, 0.12, 0.10, 0.25},                  // passatempo
            {0.70, 0.30},                                                // óculos
            {0.75, 0.25},                                                // fumava
    };

    static final double[] TETAS = {0.02, 0.04, 0.06, 0.08, 0.10, 0.15, 0.20, 0.25, 0.30};
    static final int[] NS = {20, 30, 40, 50, 60, 80, 100, 120, 150, 200, 250, 300, 400};

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);
        for (double[][] q : List.of(V2, V1)) {
            String nome = q == V2 ? "v2-rascunho" : "v1-rascunho";
            System.out.printf("%n== %s: %d campos na nota, heterogeneidade media 1 - soma(p^2) = %.3f ==%n",
                    nome, q.length, heterogeneidadeMedia(q));
            System.out.println("teta   delta(pp)  P(d>0|d!=0)  dz     N(PowerCheck)  N(80%, simulado)  poder em N=30/50/80/120");
            SplittableRandom rnd = new SplittableRandom(SEMENTE);
            System.out.printf("nulo: taxa de rejeicao em N=50: %.3f%n", poder(q, 0.0, 50, rnd));
            for (double teta : TETAS) {
                double[] momentos = momentos(q, teta, 200_000, rnd);
                double dz = momentos[0] / momentos[1];
                int nPowerCheck = (int) Math.ceil(Math.pow(1.6448536269514722 + 0.8416212335729143, 2)
                        / (dz * dz) * 1.15);
                int nSimulado = -1;
                for (int n : NS) {
                    if (poder(q, teta, n, rnd) >= 0.80) {
                        nSimulado = n;
                        break;
                    }
                }
                System.out.printf("%.2f   %5.1f      %.3f        %.3f  %5d          %s               %.2f / %.2f / %.2f / %.2f%n",
                        teta, 100 * deltaMedio(q, teta), momentos[2], dz, nPowerCheck,
                        nSimulado < 0 ? ">400" : nSimulado == NS[0] ? "<=20" : String.format("%4d", nSimulado),
                        poder(q, teta, 30, rnd), poder(q, teta, 50, rnd), poder(q, teta, 80, rnd),
                        poder(q, teta, 120, rnd));
            }
        }
    }

    static double heterogeneidadeMedia(double[][] q) {
        double soma = 0;
        for (double[] p : q) {
            double s2 = 0;
            for (double x : p) {
                s2 += x * x;
            }
            soma += 1 - s2;
        }
        return soma / q.length;
    }

    static double deltaMedio(double[][] q, double teta) {
        return (1 - NAO_RECEBI) * teta * heterogeneidadeMedia(q);
    }

    /** Média e desvio-padrão de d, e P(d > 0 | d ≠ 0). */
    static double[] momentos(double[][] q, double teta, int pares, SplittableRandom rnd) {
        double soma = 0;
        double soma2 = 0;
        int positivos = 0;
        int naoNulos = 0;
        for (int i = 0; i < pares; i++) {
            int d = notaDoPar(q, teta, rnd);
            soma += d;
            soma2 += (double) d * d;
            if (d != 0) {
                naoNulos++;
                if (d > 0) {
                    positivos++;
                }
            }
        }
        double media = soma / pares;
        double dp = Math.sqrt(soma2 / pares - media * media);
        return new double[] {media, dp, (double) positivos / naoNulos};
    }

    static double poder(double[][] q, double teta, int n, SplittableRandom rnd) {
        int rejeicoes = 0;
        int[] d = new int[n];
        for (int r = 0; r < REPETICOES; r++) {
            for (int i = 0; i < n; i++) {
                d[i] = notaDoPar(q, teta, rnd);
            }
            if (wilcoxonUnilateral(d) <= ALFA) {
                rejeicoes++;
            }
        }
        return (double) rejeicoes / REPETICOES;
    }

    static int notaDoPar(double[][] q, double teta, SplittableRandom rnd) {
        int d = 0;
        for (double[] p : q) {
            int a = sorteia(p, rnd);
            int b = sorteia(p, rnd);
            boolean comparavel = rnd.nextDouble() >= NAO_SEI && rnd.nextDouble() >= NAO_SEI;
            int leituraA = leitura(p, a, teta, rnd);
            int leituraB = leitura(p, b, teta, rnd);
            if (!comparavel) {
                continue;
            }
            d += (leituraA == a ? 1 : 0) - (leituraA == b ? 1 : 0);
            d += (leituraB == b ? 1 : 0) - (leituraB == a ? 1 : 0);
        }
        return d;
    }

    /** -1 é "não recebi", que nunca coincide. */
    static int leitura(double[] p, int verdadeira, double teta, SplittableRandom rnd) {
        if (rnd.nextDouble() < NAO_RECEBI) {
            return -1;
        }
        if (rnd.nextDouble() < teta) {
            return verdadeira;
        }
        return sorteia(p, rnd);
    }

    static int sorteia(double[] p, SplittableRandom rnd) {
        double u = rnd.nextDouble();
        double acumulado = 0;
        for (int k = 0; k < p.length; k++) {
            acumulado += p[k];
            if (u < acumulado) {
                return k;
            }
        }
        return p.length - 1;
    }

    /** O mesmo teste de {@code analysis.WilcoxonSignedRank}, em forma compacta. */
    static double wilcoxonUnilateral(int[] diferencas) {
        List<Integer> naoNulas = new ArrayList<>();
        for (int x : diferencas) {
            if (x != 0) {
                naoNulas.add(x);
            }
        }
        int n = naoNulas.size();
        if (n == 0) {
            return 1.0;
        }
        Integer[] ordem = new Integer[n];
        for (int i = 0; i < n; i++) {
            ordem[i] = i;
        }
        Arrays.sort(ordem, (i, j) -> Integer.compare(Math.abs(naoNulas.get(i)), Math.abs(naoNulas.get(j))));
        int[] postosDobrados = new int[n];
        double correcaoEmpates = 0;
        int i = 0;
        while (i < n) {
            int j = i;
            while (j + 1 < n && Math.abs(naoNulas.get(ordem[j + 1])) == Math.abs(naoNulas.get(ordem[i]))) {
                j++;
            }
            int postoDobrado = (i + 1) + (j + 1); // 2 × posto médio
            for (int k = i; k <= j; k++) {
                postosDobrados[ordem[k]] = postoDobrado;
            }
            long t = j - i + 1L;
            correcaoEmpates += (t * t * t - t) / 48.0;
            i = j + 1;
        }
        int wDobrado = 0;
        for (int k = 0; k < n; k++) {
            if (naoNulas.get(k) > 0) {
                wDobrado += postosDobrados[k];
            }
        }
        if (n <= 20) {
            // Distribuição exata de 2·W+ sob a nula, por programação dinâmica sobre os 2^n sinais.
            int maximo = Arrays.stream(postosDobrados).sum();
            double[] dist = new double[maximo + 1];
            dist[0] = 1;
            int alcance = 0;
            for (int r : postosDobrados) {
                for (int s = alcance; s >= 0; s--) {
                    if (dist[s] != 0) {
                        dist[s + r] += dist[s];
                    }
                }
                alcance += r;
            }
            double cauda = 0;
            for (int s = wDobrado; s <= maximo; s++) {
                cauda += dist[s];
            }
            return cauda / Math.pow(2, n);
        }
        double media = n * (n + 1) / 4.0;
        double variancia = n * (n + 1) * (2.0 * n + 1) / 24.0 - correcaoEmpates;
        double z = (wDobrado / 2.0 - media - 0.5) / Math.sqrt(variancia);
        return 1 - normal(z);
    }

    static double normal(double z) {
        return 0.5 * (1 + erf(z / Math.sqrt(2)));
    }

    static double erf(double x) {
        double t = 1 / (1 + 0.3275911 * Math.abs(x));
        double y = 1 - (((((1.061405429 * t - 1.453152027) * t) + 1.421413741) * t - 0.284496736) * t
                + 0.254829592) * t * Math.exp(-x * x);
        return x >= 0 ? y : -y;
    }
}
