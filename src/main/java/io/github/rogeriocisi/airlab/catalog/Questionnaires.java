package io.github.rogeriocisi.airlab.catalog;

import io.github.rogeriocisi.airlab.catalog.Questionnaire.Field;
import io.github.rogeriocisi.airlab.catalog.Questionnaire.Option;
import io.github.rogeriocisi.airlab.catalog.Questionnaire.Use;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The questionnaires of P1, by version (SPEC E13, annex).
 *
 * <p>A constant, like the catalog: a questionnaire changes only with a new version, and the old
 * one stays here, because requests answered under it must still be readable and verifiable.
 *
 * <p>Version {@code v1-rascunho} was the first draft. Version {@code v2-rascunho} (SPEC 0.25) is
 * the current one, still awaiting review by researchers of the field: twenty scored fields
 * instead of thirteen, chosen by the criteria of SPEC question 9 — one verifiable fact per field,
 * no dominant option, nothing the first name or the stratum gives away, nothing that identifies
 * the person alone, and careful language. The pairing fields and bands did not change; they are
 * the same as the P2 ones ({@code TargetRegistration}), so that pairing is the same rule in both
 * protocols.
 */
public final class Questionnaires {

    public static final String GENDER = "genero";
    public static final String AGE_BAND = "faixa-etaria";
    public static final String DEATH_PERIOD = "periodo-falecimento";

    private static final String NAME_GIVES_IT_AWAY =
            "Fica fora da nota: o primeiro nome costuma entregar o gênero, e ele é critério de pareamento.";
    private static final String EQUAL_IN_PAIR =
            "Fica fora da nota: é critério de pareamento, igual nos dois pedidos do par, e não distingue um do outro.";

    private static final Questionnaire V1_DRAFT = new Questionnaire("v1-rascunho", List.of(
            pairing(GENDER, "Gênero", NAME_GIVES_IT_AWAY,
                    "feminino", "Feminino",
                    "masculino", "Masculino",
                    "outro", "Outro"),
            pairing(AGE_BAND, "Idade ao falecer", EQUAL_IN_PAIR,
                    "ate-17", "Até 17 anos",
                    "18-39", "De 18 a 39 anos",
                    "40-59", "De 40 a 59 anos",
                    "60-79", "De 60 a 79 anos",
                    "80-mais", "80 anos ou mais"),
            pairing(DEATH_PERIOD, "Há quanto tempo faleceu", EQUAL_IN_PAIR,
                    "menos-1-ano", "Menos de 1 ano",
                    "1-5-anos", "De 1 a 5 anos",
                    "5-15-anos", "De 5 a 15 anos",
                    "mais-15-anos", "Mais de 15 anos"),
            scored("como-faleceu", "Como foi a morte",
                    "subita", "De forma súbita",
                    "prolongada", "Após doença prolongada"),
            scored("causa", "Causa principal",
                    "coracao", "Coração ou circulação",
                    "cancer", "Câncer",
                    "respiratoria", "Doença respiratória",
                    "acidente", "Acidente",
                    "violencia", "Violência",
                    "outra-doenca", "Outra doença"),
            scored("local-falecimento", "Onde faleceu",
                    "hospital", "No hospital",
                    "casa", "Em casa",
                    "rua-transito", "Na rua ou no trânsito",
                    "outro", "Em outro lugar"),
            scored("estado-civil", "Estado civil",
                    "solteiro", "Solteiro(a)",
                    "casado", "Casado(a) ou em união",
                    "viuvo", "Viúvo(a)",
                    "separado", "Separado(a)"),
            scored("filhos", "Tinha filhos",
                    "sim", "Sim",
                    "nao", "Não"),
            scored("ocupacao", "Ocupação principal",
                    "campo", "Trabalho no campo",
                    "oficio", "Ofício manual ou indústria",
                    "comercio", "Comércio",
                    "escritorio", "Escritório ou serviços",
                    "saude", "Saúde",
                    "educacao", "Educação",
                    "casa", "Cuidou da casa",
                    "outra", "Outra"),
            scored("onde-viveu", "Onde viveu a maior parte da vida",
                    "cidade-grande", "Cidade grande",
                    "cidade-media-pequena", "Cidade média ou pequena",
                    "rural", "Zona rural"),
            scored("temperamento", "Temperamento",
                    "expansivo", "Mais expansivo",
                    "reservado", "Mais reservado"),
            scored("estatura", "Estatura",
                    "abaixo", "Abaixo da média",
                    "media", "Na média",
                    "acima", "Acima da média"),
            scored("religiosidade", "Religiosidade",
                    "praticante", "Praticante",
                    "nao-praticante", "Não praticante",
                    "sem-religiao", "Sem religião"),
            scored("passatempo", "Passatempo mais marcante",
                    "musica", "Música",
                    "trabalhos-manuais", "Trabalhos manuais",
                    "esporte", "Esporte",
                    "leitura", "Leitura",
                    "cozinha", "Cozinha",
                    "plantas", "Plantas",
                    "outro", "Outro"),
            scored("oculos", "Usava óculos",
                    "sim", "Sim",
                    "nao", "Não"),
            scored("fumava", "Fumava",
                    "sim", "Sim",
                    "nao", "Não")));

    private static final Questionnaire V2_DRAFT = new Questionnaire("v2-rascunho", List.of(
            pairing(GENDER, "Gênero", NAME_GIVES_IT_AWAY,
                    "feminino", "Feminino",
                    "masculino", "Masculino",
                    "outro", "Outro"),
            pairing(AGE_BAND, "Idade ao falecer", EQUAL_IN_PAIR,
                    "ate-17", "Até 17 anos",
                    "18-39", "De 18 a 39 anos",
                    "40-59", "De 40 a 59 anos",
                    "60-79", "De 60 a 79 anos",
                    "80-mais", "80 anos ou mais"),
            pairing(DEATH_PERIOD, "Há quanto tempo faleceu", EQUAL_IN_PAIR,
                    "menos-1-ano", "Menos de 1 ano",
                    "1-5-anos", "De 1 a 5 anos",
                    "5-15-anos", "De 5 a 15 anos",
                    "mais-15-anos", "Mais de 15 anos"),
            // Circumstances of the death
            scored("como-faleceu", "Como foi a morte",
                    "subita", "De forma súbita, sem aviso",
                    "dias-semanas", "Depois de dias ou semanas de doença",
                    "meses-anos", "Depois de meses ou anos de doença"),
            scored("causa", "Causa principal",
                    "coracao", "Coração ou circulação, inclusive AVC",
                    "cancer", "Câncer",
                    "infeccao", "Infecção, inclusive covid-19 e pneumonia",
                    "respiratoria", "Outra doença respiratória",
                    "outra-doenca", "Outra doença",
                    "acidente", "Acidente",
                    "outra-externa", "Outra causa externa"),
            scored("local-falecimento", "Onde faleceu",
                    "hospital", "No hospital",
                    "casa", "Em casa",
                    "rua-transito", "Na rua ou no trânsito",
                    "outro", "Em outro lugar"),
            // Family
            scored("estado-civil", "Estado civil na época",
                    "solteiro", "Solteiro(a)",
                    "casado", "Casado(a) ou em união",
                    "viuvo", "Viúvo(a)",
                    "separado", "Separado(a)"),
            scored("quantos-filhos", "Quantos filhos teve",
                    "nenhum", "Nenhum",
                    "um", "Um",
                    "dois", "Dois",
                    "tres", "Três",
                    "quatro-mais", "Quatro ou mais"),
            scored("lugar-irmaos", "Lugar entre os irmãos",
                    "unico", "Filho(a) único(a)",
                    "mais-velho", "O mais velho ou a mais velha",
                    "meio", "Do meio",
                    "cacula", "O caçula ou a caçula"),
            scored("quem-pede", "Parentesco de quem fez o pedido",
                    "filho", "Filho ou filha",
                    "conjuge", "Cônjuge ou companheiro(a)",
                    "pai-mae", "Pai ou mãe",
                    "irmao", "Irmão ou irmã",
                    "neto", "Neto ou neta",
                    "outro-parente", "Outro parente",
                    "amigo", "Amigo ou amiga"),
            // Life course
            scored("escolaridade", "Até onde estudou",
                    "fundamental-incompleto", "Não completou o ensino fundamental",
                    "fundamental", "Completou o ensino fundamental (antigo ginásio)",
                    "medio", "Completou o ensino médio",
                    "superior", "Completou o ensino superior"),
            scored("ocupacao", "Ocupação principal",
                    "campo", "Trabalho no campo",
                    "industria", "Indústria, construção ou ofício manual",
                    "comercio", "Comércio ou vendas",
                    "transporte", "Transporte, como motorista",
                    "escritorio", "Escritório ou serviços",
                    "saude", "Saúde",
                    "educacao", "Educação",
                    "seguranca", "Segurança, polícia ou forças armadas",
                    "casa", "Cuidou da casa e da família",
                    "outra", "Outra"),
            scored("onde-viveu", "Onde viveu a maior parte da vida",
                    "cidade-grande", "Cidade grande",
                    "cidade-media-pequena", "Cidade média ou pequena",
                    "rural", "Zona rural"),
            // Personality
            scored("temperamento", "Temperamento",
                    "expansivo", "Mais expansivo",
                    "reservado", "Mais reservado"),
            scored("traco", "Traço mais marcante do jeito de ser",
                    "bem-humorado", "Bem-humorado ou brincalhão",
                    "carinhoso", "Carinhoso ou afetuoso",
                    "serio", "Sério ou exigente",
                    "determinado", "Determinado ou teimoso",
                    "calmo", "Calmo ou paciente",
                    "preocupado", "Preocupado ou ansioso"),
            // Appearance
            scored("estatura", "Estatura, comparada à de pessoas do mesmo gênero e da mesma geração",
                    "abaixo", "Abaixo da média",
                    "media", "Na média",
                    "acima", "Acima da média"),
            scored("porte", "Porte físico na maior parte da vida adulta",
                    "magro", "Magro",
                    "medio", "Médio",
                    "robusto", "Robusto ou acima do peso"),
            scored("cabelo", "Cabelo, como a pessoa é mais lembrada",
                    "escuro", "Escuro",
                    "claro", "Claro, loiro ou ruivo",
                    "grisalho", "Grisalho ou branco",
                    "calvo", "Calvo ou quase calvo"),
            scored("oculos", "Usava óculos",
                    "sim", "Sim",
                    "nao", "Não"),
            // Beliefs and habits
            scored("religiao", "Religião que seguia",
                    "catolica", "Católica",
                    "evangelica", "Evangélica",
                    "espirita", "Espírita",
                    "matriz-africana", "Umbanda, candomblé ou outra de matriz africana",
                    "outra", "Outra religião",
                    "nenhuma", "Nenhuma religião"),
            scored("passatempo", "Passatempo mais marcante",
                    "musica", "Música",
                    "trabalhos-manuais", "Trabalhos manuais",
                    "esporte", "Futebol ou outro esporte",
                    "leitura", "Leitura",
                    "cozinha", "Cozinha",
                    "plantas", "Plantas ou jardinagem",
                    "pesca", "Pesca ou vida ao ar livre",
                    "jogos", "Jogos de cartas ou de tabuleiro",
                    "outro", "Outro"),
            scored("fumo", "Fumo",
                    "nunca", "Nunca fumou",
                    "parou", "Fumou e parou",
                    "fumava", "Fumava na época em que faleceu"),
            scored("animal", "Animal de estimação mais marcante",
                    "cachorro", "Cachorro",
                    "gato", "Gato",
                    "passaro", "Pássaro",
                    "outro", "Outro animal",
                    "nenhum", "Não teve animal de estimação")));

    private static final List<Questionnaire> ALL = List.of(V1_DRAFT, V2_DRAFT);

    private Questionnaires() {
    }

    /** The version new requests are answered under. */
    public static Questionnaire current() {
        return V2_DRAFT;
    }

    public static Optional<Questionnaire> byVersion(String version) {
        return ALL.stream().filter(questionnaire -> questionnaire.version().equals(version)).findFirst();
    }

    /**
     * Every version, the current one first and then the older ones, newest first. Old versions
     * stay because requests answered under them must remain readable (SPEC E13).
     */
    public static List<Questionnaire> all() {
        List<Questionnaire> older = new ArrayList<>(ALL);
        older.remove(current());
        Collections.reverse(older);
        List<Questionnaire> all = new ArrayList<>(older.size() + 1);
        all.add(current());
        all.addAll(older);
        return List.copyOf(all);
    }

    private static Field scored(String key, String label, String... keysAndLabels) {
        return new Field(key, label, Use.SCORED, null, options(keysAndLabels));
    }

    private static Field pairing(String key, String label, String reason, String... keysAndLabels) {
        return new Field(key, label, Use.PAIRING, reason, options(keysAndLabels));
    }

    private static List<Option> options(String... keysAndLabels) {
        if (keysAndLabels.length % 2 != 0) {
            throw new IllegalArgumentException("options come in key and label pairs");
        }
        Option[] out = new Option[keysAndLabels.length / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = new Option(keysAndLabels[2 * i], keysAndLabels[2 * i + 1]);
        }
        return List.of(out);
    }
}
