package io.github.rogeriocisi.airlab.catalog;

import io.github.rogeriocisi.airlab.catalog.Questionnaire.Field;
import io.github.rogeriocisi.airlab.catalog.Questionnaire.Option;
import io.github.rogeriocisi.airlab.catalog.Questionnaire.Use;
import io.github.rogeriocisi.airlab.support.Text;
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
 * <p>Version {@code v1-rascunho} was the first draft. Version {@code v2-rascunho} (SPEC 0.25)
 * brought twenty scored fields instead of thirteen, chosen by the criteria of SPEC question 9 — one
 * verifiable fact per field, no dominant option, nothing the first name or the stratum gives away,
 * nothing that identifies the person alone, and careful language. Version {@code v3-rascunho}
 * (SPEC 0.28, section 1.5, rule 9) is the current one: <strong>the same twenty-three fields, the
 * same options and the same keys as the {@code v2-rascunho}</strong>, with the labels in both
 * languages of the interface. A new version, and not an edit, because the frozen annex of the
 * preregistration includes the labels, and a frozen document does not change (Constitution P2).
 *
 * <p>The pairing fields and bands have not changed since the first version; they are the same as
 * the P2 ones ({@code TargetRegistration}), so that pairing is the same rule in both protocols.
 *
 * <p>In {@code v1-rascunho} and {@code v2-rascunho} the English side of each label repeats the
 * Portuguese. That is not a missing translation: those two versions existed only before the
 * interface had two languages, and both are drafts, which a study may not freeze outside the local
 * profile (E13, phase C), so whoever answered one of them answered it in Portuguese, and the
 * Portuguese label is what they read.
 */
public final class Questionnaires {

    public static final String GENDER = "genero";
    public static final String AGE_BAND = "faixa-etaria";
    public static final String DEATH_PERIOD = "periodo-falecimento";

    private static final String NAME_GIVES_IT_AWAY_PT =
            "Fica fora da nota: o primeiro nome costuma entregar o gênero, e ele é critério de pareamento.";
    private static final String EQUAL_IN_PAIR_PT =
            "Fica fora da nota: é critério de pareamento, igual nos dois pedidos do par, e não distingue um do outro.";

    private static final Text NAME_GIVES_IT_AWAY = Text.of(NAME_GIVES_IT_AWAY_PT,
            "Out of the score: the first name usually gives the gender away, and gender is a pairing criterion.");
    private static final Text EQUAL_IN_PAIR = Text.of(EQUAL_IN_PAIR_PT,
            "Out of the score: it is a pairing criterion, equal in both requests of the pair, so it tells "
                    + "neither of them apart.");

    private static final Questionnaire V1_DRAFT = new Questionnaire("v1-rascunho", List.of(
            pastPairing(GENDER, "Gênero", NAME_GIVES_IT_AWAY_PT,
                    "feminino", "Feminino",
                    "masculino", "Masculino",
                    "outro", "Outro"),
            pastPairing(AGE_BAND, "Idade ao falecer", EQUAL_IN_PAIR_PT,
                    "ate-17", "Até 17 anos",
                    "18-39", "De 18 a 39 anos",
                    "40-59", "De 40 a 59 anos",
                    "60-79", "De 60 a 79 anos",
                    "80-mais", "80 anos ou mais"),
            pastPairing(DEATH_PERIOD, "Há quanto tempo faleceu", EQUAL_IN_PAIR_PT,
                    "menos-1-ano", "Menos de 1 ano",
                    "1-5-anos", "De 1 a 5 anos",
                    "5-15-anos", "De 5 a 15 anos",
                    "mais-15-anos", "Mais de 15 anos"),
            pastScored("como-faleceu", "Como foi a morte",
                    "subita", "De forma súbita",
                    "prolongada", "Após doença prolongada"),
            pastScored("causa", "Causa principal",
                    "coracao", "Coração ou circulação",
                    "cancer", "Câncer",
                    "respiratoria", "Doença respiratória",
                    "acidente", "Acidente",
                    "violencia", "Violência",
                    "outra-doenca", "Outra doença"),
            pastScored("local-falecimento", "Onde faleceu",
                    "hospital", "No hospital",
                    "casa", "Em casa",
                    "rua-transito", "Na rua ou no trânsito",
                    "outro", "Em outro lugar"),
            pastScored("estado-civil", "Estado civil",
                    "solteiro", "Solteiro(a)",
                    "casado", "Casado(a) ou em união",
                    "viuvo", "Viúvo(a)",
                    "separado", "Separado(a)"),
            pastScored("filhos", "Tinha filhos",
                    "sim", "Sim",
                    "nao", "Não"),
            pastScored("ocupacao", "Ocupação principal",
                    "campo", "Trabalho no campo",
                    "oficio", "Ofício manual ou indústria",
                    "comercio", "Comércio",
                    "escritorio", "Escritório ou serviços",
                    "saude", "Saúde",
                    "educacao", "Educação",
                    "casa", "Cuidou da casa",
                    "outra", "Outra"),
            pastScored("onde-viveu", "Onde viveu a maior parte da vida",
                    "cidade-grande", "Cidade grande",
                    "cidade-media-pequena", "Cidade média ou pequena",
                    "rural", "Zona rural"),
            pastScored("temperamento", "Temperamento",
                    "expansivo", "Mais expansivo",
                    "reservado", "Mais reservado"),
            pastScored("estatura", "Estatura",
                    "abaixo", "Abaixo da média",
                    "media", "Na média",
                    "acima", "Acima da média"),
            pastScored("religiosidade", "Religiosidade",
                    "praticante", "Praticante",
                    "nao-praticante", "Não praticante",
                    "sem-religiao", "Sem religião"),
            pastScored("passatempo", "Passatempo mais marcante",
                    "musica", "Música",
                    "trabalhos-manuais", "Trabalhos manuais",
                    "esporte", "Esporte",
                    "leitura", "Leitura",
                    "cozinha", "Cozinha",
                    "plantas", "Plantas",
                    "outro", "Outro"),
            pastScored("oculos", "Usava óculos",
                    "sim", "Sim",
                    "nao", "Não"),
            pastScored("fumava", "Fumava",
                    "sim", "Sim",
                    "nao", "Não")));

    private static final Questionnaire V2_DRAFT = new Questionnaire("v2-rascunho", List.of(
            pastPairing(GENDER, "Gênero", NAME_GIVES_IT_AWAY_PT,
                    "feminino", "Feminino",
                    "masculino", "Masculino",
                    "outro", "Outro"),
            pastPairing(AGE_BAND, "Idade ao falecer", EQUAL_IN_PAIR_PT,
                    "ate-17", "Até 17 anos",
                    "18-39", "De 18 a 39 anos",
                    "40-59", "De 40 a 59 anos",
                    "60-79", "De 60 a 79 anos",
                    "80-mais", "80 anos ou mais"),
            pastPairing(DEATH_PERIOD, "Há quanto tempo faleceu", EQUAL_IN_PAIR_PT,
                    "menos-1-ano", "Menos de 1 ano",
                    "1-5-anos", "De 1 a 5 anos",
                    "5-15-anos", "De 5 a 15 anos",
                    "mais-15-anos", "Mais de 15 anos"),
            // Circumstances of the death
            pastScored("como-faleceu", "Como foi a morte",
                    "subita", "De forma súbita, sem aviso",
                    "dias-semanas", "Depois de dias ou semanas de doença",
                    "meses-anos", "Depois de meses ou anos de doença"),
            pastScored("causa", "Causa principal",
                    "coracao", "Coração ou circulação, inclusive AVC",
                    "cancer", "Câncer",
                    "infeccao", "Infecção, inclusive covid-19 e pneumonia",
                    "respiratoria", "Outra doença respiratória",
                    "outra-doenca", "Outra doença",
                    "acidente", "Acidente",
                    "outra-externa", "Outra causa externa"),
            pastScored("local-falecimento", "Onde faleceu",
                    "hospital", "No hospital",
                    "casa", "Em casa",
                    "rua-transito", "Na rua ou no trânsito",
                    "outro", "Em outro lugar"),
            // Family
            pastScored("estado-civil", "Estado civil na época",
                    "solteiro", "Solteiro(a)",
                    "casado", "Casado(a) ou em união",
                    "viuvo", "Viúvo(a)",
                    "separado", "Separado(a)"),
            pastScored("quantos-filhos", "Quantos filhos teve",
                    "nenhum", "Nenhum",
                    "um", "Um",
                    "dois", "Dois",
                    "tres", "Três",
                    "quatro-mais", "Quatro ou mais"),
            pastScored("lugar-irmaos", "Lugar entre os irmãos",
                    "unico", "Filho(a) único(a)",
                    "mais-velho", "O mais velho ou a mais velha",
                    "meio", "Do meio",
                    "cacula", "O caçula ou a caçula"),
            pastScored("quem-pede", "Parentesco de quem fez o pedido",
                    "filho", "Filho ou filha",
                    "conjuge", "Cônjuge ou companheiro(a)",
                    "pai-mae", "Pai ou mãe",
                    "irmao", "Irmão ou irmã",
                    "neto", "Neto ou neta",
                    "outro-parente", "Outro parente",
                    "amigo", "Amigo ou amiga"),
            // Life course
            pastScored("escolaridade", "Até onde estudou",
                    "fundamental-incompleto", "Não completou o ensino fundamental",
                    "fundamental", "Completou o ensino fundamental (antigo ginásio)",
                    "medio", "Completou o ensino médio",
                    "superior", "Completou o ensino superior"),
            pastScored("ocupacao", "Ocupação principal",
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
            pastScored("onde-viveu", "Onde viveu a maior parte da vida",
                    "cidade-grande", "Cidade grande",
                    "cidade-media-pequena", "Cidade média ou pequena",
                    "rural", "Zona rural"),
            // Personality
            pastScored("temperamento", "Temperamento",
                    "expansivo", "Mais expansivo",
                    "reservado", "Mais reservado"),
            pastScored("traco", "Traço mais marcante do jeito de ser",
                    "bem-humorado", "Bem-humorado ou brincalhão",
                    "carinhoso", "Carinhoso ou afetuoso",
                    "serio", "Sério ou exigente",
                    "determinado", "Determinado ou teimoso",
                    "calmo", "Calmo ou paciente",
                    "preocupado", "Preocupado ou ansioso"),
            // Appearance
            pastScored("estatura", "Estatura, comparada à de pessoas do mesmo gênero e da mesma geração",
                    "abaixo", "Abaixo da média",
                    "media", "Na média",
                    "acima", "Acima da média"),
            pastScored("porte", "Porte físico na maior parte da vida adulta",
                    "magro", "Magro",
                    "medio", "Médio",
                    "robusto", "Robusto ou acima do peso"),
            pastScored("cabelo", "Cabelo, como a pessoa é mais lembrada",
                    "escuro", "Escuro",
                    "claro", "Claro, loiro ou ruivo",
                    "grisalho", "Grisalho ou branco",
                    "calvo", "Calvo ou quase calvo"),
            pastScored("oculos", "Usava óculos",
                    "sim", "Sim",
                    "nao", "Não"),
            // Beliefs and habits
            pastScored("religiao", "Religião que seguia",
                    "catolica", "Católica",
                    "evangelica", "Evangélica",
                    "espirita", "Espírita",
                    "matriz-africana", "Umbanda, candomblé ou outra de matriz africana",
                    "outra", "Outra religião",
                    "nenhuma", "Nenhuma religião"),
            pastScored("passatempo", "Passatempo mais marcante",
                    "musica", "Música",
                    "trabalhos-manuais", "Trabalhos manuais",
                    "esporte", "Futebol ou outro esporte",
                    "leitura", "Leitura",
                    "cozinha", "Cozinha",
                    "plantas", "Plantas ou jardinagem",
                    "pesca", "Pesca ou vida ao ar livre",
                    "jogos", "Jogos de cartas ou de tabuleiro",
                    "outro", "Outro"),
            pastScored("fumo", "Fumo",
                    "nunca", "Nunca fumou",
                    "parou", "Fumou e parou",
                    "fumava", "Fumava na época em que faleceu"),
            pastScored("animal", "Animal de estimação mais marcante",
                    "cachorro", "Cachorro",
                    "gato", "Gato",
                    "passaro", "Pássaro",
                    "outro", "Outro animal",
                    "nenhum", "Não teve animal de estimação")));

    /**
     * The current version (SPEC 0.28): the {@code v2-rascunho} field for field, option for option
     * and key for key, with every label in Portuguese and in English. The two sides of each label
     * are neighbours here on purpose — that is how a reviewer, and the ethics committee, read what
     * each participant is shown.
     */
    private static final Questionnaire V3_DRAFT = new Questionnaire("v3-rascunho", List.of(
            pairing(GENDER, Text.of("Gênero", "Gender"), NAME_GIVES_IT_AWAY,
                    option("feminino", "Feminino", "Female"),
                    option("masculino", "Masculino", "Male"),
                    option("outro", "Outro", "Other")),
            pairing(AGE_BAND, Text.of("Idade ao falecer", "Age at death"), EQUAL_IN_PAIR,
                    option("ate-17", "Até 17 anos", "Up to 17 years"),
                    option("18-39", "De 18 a 39 anos", "18 to 39 years"),
                    option("40-59", "De 40 a 59 anos", "40 to 59 years"),
                    option("60-79", "De 60 a 79 anos", "60 to 79 years"),
                    option("80-mais", "80 anos ou mais", "80 years or older")),
            pairing(DEATH_PERIOD, Text.of("Há quanto tempo faleceu", "How long ago they died"), EQUAL_IN_PAIR,
                    option("menos-1-ano", "Menos de 1 ano", "Less than 1 year"),
                    option("1-5-anos", "De 1 a 5 anos", "1 to 5 years"),
                    option("5-15-anos", "De 5 a 15 anos", "5 to 15 years"),
                    option("mais-15-anos", "Mais de 15 anos", "More than 15 years")),
            // Circumstances of the death
            scored("como-faleceu", Text.of("Como foi a morte", "How the death came"),
                    option("subita", "De forma súbita, sem aviso", "Suddenly, with no warning"),
                    option("dias-semanas", "Depois de dias ou semanas de doença",
                            "After days or weeks of illness"),
                    option("meses-anos", "Depois de meses ou anos de doença",
                            "After months or years of illness")),
            scored("causa", Text.of("Causa principal", "Main cause"),
                    option("coracao", "Coração ou circulação, inclusive AVC",
                            "Heart or circulation, including stroke"),
                    option("cancer", "Câncer", "Cancer"),
                    option("infeccao", "Infecção, inclusive covid-19 e pneumonia",
                            "Infection, including covid-19 and pneumonia"),
                    option("respiratoria", "Outra doença respiratória", "Another respiratory disease"),
                    option("outra-doenca", "Outra doença", "Another disease"),
                    option("acidente", "Acidente", "Accident"),
                    option("outra-externa", "Outra causa externa", "Another external cause")),
            scored("local-falecimento", Text.of("Onde faleceu", "Where they died"),
                    option("hospital", "No hospital", "In a hospital"),
                    option("casa", "Em casa", "At home"),
                    option("rua-transito", "Na rua ou no trânsito", "In the street or in traffic"),
                    option("outro", "Em outro lugar", "Somewhere else")),
            // Family
            scored("estado-civil", Text.of("Estado civil na época", "Marital status at the time"),
                    option("solteiro", "Solteiro(a)", "Single"),
                    option("casado", "Casado(a) ou em união", "Married or in a union"),
                    option("viuvo", "Viúvo(a)", "Widowed"),
                    option("separado", "Separado(a)", "Separated")),
            scored("quantos-filhos", Text.of("Quantos filhos teve", "How many children they had"),
                    option("nenhum", "Nenhum", "None"),
                    option("um", "Um", "One"),
                    option("dois", "Dois", "Two"),
                    option("tres", "Três", "Three"),
                    option("quatro-mais", "Quatro ou mais", "Four or more")),
            scored("lugar-irmaos", Text.of("Lugar entre os irmãos", "Place among the siblings"),
                    option("unico", "Filho(a) único(a)", "Only child"),
                    option("mais-velho", "O mais velho ou a mais velha", "The eldest"),
                    option("meio", "Do meio", "A middle child"),
                    option("cacula", "O caçula ou a caçula", "The youngest")),
            scored("quem-pede", Text.of("Parentesco de quem fez o pedido",
                            "How the person who made the request was related"),
                    option("filho", "Filho ou filha", "Son or daughter"),
                    option("conjuge", "Cônjuge ou companheiro(a)", "Spouse or partner"),
                    option("pai-mae", "Pai ou mãe", "Father or mother"),
                    option("irmao", "Irmão ou irmã", "Brother or sister"),
                    option("neto", "Neto ou neta", "Grandson or granddaughter"),
                    option("outro-parente", "Outro parente", "Another relative"),
                    option("amigo", "Amigo ou amiga", "A friend")),
            // Life course
            scored("escolaridade", Text.of("Até onde estudou", "How far they studied"),
                    option("fundamental-incompleto", "Não completou o ensino fundamental",
                            "Did not finish primary school"),
                    option("fundamental", "Completou o ensino fundamental (antigo ginásio)",
                            "Finished primary school"),
                    option("medio", "Completou o ensino médio", "Finished secondary school"),
                    option("superior", "Completou o ensino superior", "Finished higher education")),
            scored("ocupacao", Text.of("Ocupação principal", "Main occupation"),
                    option("campo", "Trabalho no campo", "Farm work"),
                    option("industria", "Indústria, construção ou ofício manual",
                            "Industry, construction or a manual trade"),
                    option("comercio", "Comércio ou vendas", "Retail or sales"),
                    option("transporte", "Transporte, como motorista", "Transport, such as driving"),
                    option("escritorio", "Escritório ou serviços", "Office or services"),
                    option("saude", "Saúde", "Health care"),
                    option("educacao", "Educação", "Education"),
                    option("seguranca", "Segurança, polícia ou forças armadas",
                            "Security, police or armed forces"),
                    option("casa", "Cuidou da casa e da família", "Looked after the home and the family"),
                    option("outra", "Outra", "Other")),
            scored("onde-viveu", Text.of("Onde viveu a maior parte da vida",
                            "Where they lived most of their life"),
                    option("cidade-grande", "Cidade grande", "A large city"),
                    option("cidade-media-pequena", "Cidade média ou pequena", "A medium or small town"),
                    option("rural", "Zona rural", "The countryside")),
            // Personality
            scored("temperamento", Text.of("Temperamento", "Temperament"),
                    option("expansivo", "Mais expansivo", "More outgoing"),
                    option("reservado", "Mais reservado", "More reserved")),
            scored("traco", Text.of("Traço mais marcante do jeito de ser",
                            "Most striking trait of their nature"),
                    option("bem-humorado", "Bem-humorado ou brincalhão", "Good-humored or playful"),
                    option("carinhoso", "Carinhoso ou afetuoso", "Warm or affectionate"),
                    option("serio", "Sério ou exigente", "Serious or demanding"),
                    option("determinado", "Determinado ou teimoso", "Determined or stubborn"),
                    option("calmo", "Calmo ou paciente", "Calm or patient"),
                    option("preocupado", "Preocupado ou ansioso", "Worried or anxious")),
            // Appearance
            scored("estatura", Text.of("Estatura, comparada à de pessoas do mesmo gênero e da mesma geração",
                            "Height, compared with people of the same gender and generation"),
                    option("abaixo", "Abaixo da média", "Below average"),
                    option("media", "Na média", "Average"),
                    option("acima", "Acima da média", "Above average")),
            scored("porte", Text.of("Porte físico na maior parte da vida adulta",
                            "Build through most of their adult life"),
                    option("magro", "Magro", "Thin"),
                    option("medio", "Médio", "Medium"),
                    option("robusto", "Robusto ou acima do peso", "Heavy-set or overweight")),
            scored("cabelo", Text.of("Cabelo, como a pessoa é mais lembrada",
                            "Hair, as the person is most remembered"),
                    option("escuro", "Escuro", "Dark"),
                    option("claro", "Claro, loiro ou ruivo", "Light, blond or red"),
                    option("grisalho", "Grisalho ou branco", "Gray or white"),
                    option("calvo", "Calvo ou quase calvo", "Bald or nearly bald")),
            scored("oculos", Text.of("Usava óculos", "Wore glasses"),
                    option("sim", "Sim", "Yes"),
                    option("nao", "Não", "No")),
            // Beliefs and habits
            scored("religiao", Text.of("Religião que seguia", "Religion they followed"),
                    option("catolica", "Católica", "Catholic"),
                    option("evangelica", "Evangélica", "Evangelical"),
                    option("espirita", "Espírita", "Spiritist"),
                    option("matriz-africana", "Umbanda, candomblé ou outra de matriz africana",
                            "Umbanda, candomblé or another religion of African origin"),
                    option("outra", "Outra religião", "Another religion"),
                    option("nenhuma", "Nenhuma religião", "No religion")),
            scored("passatempo", Text.of("Passatempo mais marcante", "Most memorable pastime"),
                    option("musica", "Música", "Music"),
                    option("trabalhos-manuais", "Trabalhos manuais", "Handicrafts"),
                    option("esporte", "Futebol ou outro esporte", "Soccer or another sport"),
                    option("leitura", "Leitura", "Reading"),
                    option("cozinha", "Cozinha", "Cooking"),
                    option("plantas", "Plantas ou jardinagem", "Plants or gardening"),
                    option("pesca", "Pesca ou vida ao ar livre", "Fishing or the outdoors"),
                    option("jogos", "Jogos de cartas ou de tabuleiro", "Card or board games"),
                    option("outro", "Outro", "Other")),
            scored("fumo", Text.of("Fumo", "Smoking"),
                    option("nunca", "Nunca fumou", "Never smoked"),
                    option("parou", "Fumou e parou", "Smoked and quit"),
                    option("fumava", "Fumava na época em que faleceu",
                            "Was smoking at the time of death")),
            scored("animal", Text.of("Animal de estimação mais marcante", "Most memorable pet"),
                    option("cachorro", "Cachorro", "Dog"),
                    option("gato", "Gato", "Cat"),
                    option("passaro", "Pássaro", "Bird"),
                    option("outro", "Outro animal", "Another animal"),
                    option("nenhum", "Não teve animal de estimação", "Had no pet"))));

    private static final List<Questionnaire> ALL = List.of(V1_DRAFT, V2_DRAFT, V3_DRAFT);

    private Questionnaires() {
    }

    /** The version new requests are answered under. */
    public static Questionnaire current() {
        return V3_DRAFT;
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

    private static Field scored(String key, Text label, Option... options) {
        return new Field(key, label, Use.SCORED, null, List.of(options));
    }

    private static Field pairing(String key, Text label, Text reason, Option... options) {
        return new Field(key, label, Use.PAIRING, reason, List.of(options));
    }

    private static Option option(String key, String pt, String en) {
        return new Option(key, Text.of(pt, en));
    }

    /**
     * A field of a version that existed before the interface had two languages: the English side
     * of every label repeats the Portuguese, which is the text whoever answered it actually read.
     */
    private static Field pastScored(String key, String label, String... keysAndLabels) {
        return new Field(key, Text.of(label, label), Use.SCORED, null, pastOptions(keysAndLabels));
    }

    private static Field pastPairing(String key, String label, String reason, String... keysAndLabels) {
        return new Field(key, Text.of(label, label), Use.PAIRING, Text.of(reason, reason),
                pastOptions(keysAndLabels));
    }

    private static List<Option> pastOptions(String... keysAndLabels) {
        if (keysAndLabels.length % 2 != 0) {
            throw new IllegalArgumentException("options come in key and label pairs");
        }
        Option[] out = new Option[keysAndLabels.length / 2];
        for (int i = 0; i < out.length; i++) {
            String label = keysAndLabels[2 * i + 1];
            out[i] = new Option(keysAndLabels[2 * i], Text.of(label, label));
        }
        return List.of(out);
    }
}
