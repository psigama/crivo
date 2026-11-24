package com.psigama.airlab.catalog;

import com.psigama.airlab.support.Text;
import java.util.List;
import java.util.Optional;

/**
 * The protocol catalog of the platform (SPEC, section 1.1).
 *
 * <p>Deliberately a constant, not a table: the catalog is part of the specification, changes
 * only with a new SPEC version, and must read the same in the application, in the exported
 * verification package and in the repository.
 *
 * <p>The text is interface, and it is written out in full, with accents, because it is rendered to
 * people as it stands here. Since SPEC 0.28 (section 1.5, rule 6) it is written out in both
 * languages, Portuguese first: the limits of a protocol are the text catalog rule 2 makes
 * mandatory, and until SPEC 0.27 the English page quoted them in Portuguese for lack of anywhere
 * to put a translation.
 */
public final class ProtocolCatalog {

    private static final Protocol PAIRED_READING = new Protocol(
            "P2",
            "leitura-pareada",
            Text.of("Leitura pareada cega", "Blind paired reading"),
            "supervisionado",
            Protocol.Branch.AIR,
            1, 2,
            Protocol.Automation.PARTIAL,
            Protocol.Status.DISCONTINUED,
            Text.of("O médium produz uma leitura sobre um falecido-alvo conhecendo apenas o primeiro nome, "
                            + "entregue por um experimentador que não sabe nada sobre essa pessoa. A leitura é "
                            + "convertida em itens verificáveis por quem não sabe de quem ela é. O consulente "
                            + "recebe duas leituras, a do alvo dele e a do chamariz, sem rótulo, pontua os "
                            + "itens e escolhe qual acredita ser a sua.",
                    "The medium produces a reading about a target decedent knowing nothing but the first "
                            + "name, handed over by an experimenter who knows nothing about that person. The "
                            + "reading is turned into verifiable items by someone who does not know whose "
                            + "reading it is. The sitter receives two readings, the one for their target and "
                            + "the one for the decoy, unlabeled, scores the items and picks the one they "
                            + "believe is theirs."),
            List.of(
                    Text.of("Não distingue sobrevivência de telepatia com pessoas vivas: um acerto acima do "
                                    + "acaso diz que houve informação, não de onde ela veio.",
                            "It does not distinguish survival from telepathy with living people: a hit rate "
                                    + "above chance says information got through, not where it came from."),
                    Text.of("Não é automatizável por inteiro. A sessão permanece síncrona, com experimentador "
                                    + "proxy, porque nos dois estudos em que o médium não interagiu com ninguém o "
                                    + "resultado foi nulo (Silva e Moreira-Almeida, 2022).",
                            "It cannot be automated end to end. The session stays synchronous, with a proxy "
                                    + "experimenter, because in the two studies where the medium interacted "
                                    + "with no one the result was null (Silva and Moreira-Almeida, 2022)."),
                    Text.of("Depende de julgamento humano na pontuação, e por isso a escala entra congelada no "
                                    + "pré-registro.",
                            "It depends on human judgement in the scoring, which is why the scale is frozen "
                                    + "into the preregistration.")),
            "E1 a E9");

    private static final Protocol PRECOGNITIVE_FORCED_CHOICE = new Protocol(
            "P3",
            "escolha-forcada",
            Text.of("Escolha forçada precognitiva", "Precognitive forced choice"),
            "precognição",
            Protocol.Branch.ESP,
            1, 4,
            Protocol.Automation.FULL,
            Protocol.Status.IMPLEMENTED,
            Text.of("A cada tentativa a pessoa escolhe entre quatro alternativas. O alvo daquela tentativa é "
                            + "derivado de uma semente sorteada no início da sessão e só é calculado depois que "
                            + "a resposta está travada. No fim da sessão a semente é revelada, e qualquer pessoa "
                            + "recomputa todos os alvos e confere que nenhum deles foi escolhido depois da "
                            + "resposta.",
                    "On each trial the person chooses between four alternatives. The target of that trial is "
                            + "derived from a seed drawn at the start of the session and is computed only "
                            + "after the answer is locked. At the end of the session the seed is revealed, and "
                            + "anyone can recompute every target and check that none of them was picked after "
                            + "the answer."),
            List.of(
                    Text.of("Não diz nada sobre falecidos nem sobre mediunidade: é um teste de precognição.",
                            "It says nothing about the deceased or about mediumship: it is a precognition "
                                    + "test."),
                    Text.of("As replicações grandes dos achados de Bem deram nulo (Ritchie, Wiseman e French, "
                                    + "2012; Galak et al., 2012). A expectativa razoável para este teste é "
                                    + "resultado nulo.",
                            "The large replications of Bem's findings came out null (Ritchie, Wiseman and "
                                    + "French, 2012; Galak et al., 2012). The reasonable expectation for this "
                                    + "test is a null result."),
                    Text.of("O conjunto público é exploratório, nunca confirmatório: não se sabe quem testou, "
                                    + "quantas vezes nem em que condições.",
                            "The public data set is exploratory, never confirmatory: there is no knowing who "
                                    + "took the test, how many times, or under what conditions.")),
            "E11");

    private static final Protocol REMOTE_VIEWING = new Protocol(
            "P4",
            "visao-remota",
            Text.of("Visão remota com quatro alvos", "Remote viewing with four targets"),
            "visão remota",
            Protocol.Branch.ESP,
            1, 4,
            Protocol.Automation.FULL,
            Protocol.Status.IMPLEMENTED,
            Text.of("O sistema sorteia uma figura-alvo de um banco versionado. A pessoa escreve a impressão "
                            + "dela antes de ver qualquer candidata, e essa impressão é travada. Só então "
                            + "aparecem quatro figuras, o alvo e três chamarizes, para ela ordenar da mais "
                            + "parecida com a impressão à menos parecida. Acerto é o alvo em primeiro lugar.",
                    "The system draws a target picture from a versioned pool. The person writes down their "
                            + "impression before seeing any candidate, and that impression is locked. Only "
                            + "then do four pictures appear — the target and three decoys — to be ranked from "
                            + "the most like the impression to the least. A hit is the target ranked first."),
            List.of(
                    Text.of("Não diz nada sobre falecidos nem sobre mediunidade.",
                            "It says nothing about the deceased or about mediumship."),
                    Text.of("Quem julga a correspondência é a própria pessoa, o que reintroduz a crítica de "
                                    + "\"como se pontua\" que a escolha forçada não tem. É o preço do formato de "
                                    + "resposta livre.",
                            "The person judges the match themselves, which brings back the \"how is this "
                                    + "scored\" objection that forced choice does not have. That is the price "
                                    + "of a free-response format."),
                    Text.of("O conjunto público é exploratório, pelas mesmas razões do P3.",
                            "The public data set is exploratory, for the same reasons as P3.")),
            "E12");

    private static final Protocol STRUCTURED_READING = new Protocol(
            "P1",
            "leitura-estruturada",
            Text.of("Leitura estruturada automatizada", "Automated structured reading"),
            "automático",
            Protocol.Branch.AIR,
            1, 2,
            Protocol.Automation.FULL,
            Protocol.Status.IN_PROGRESS,
            Text.of("O consulente cria uma conta, informa o primeiro nome do falecido e responde a um "
                            + "questionário fechado sobre ele; as respostas são travadas antes de qualquer "
                            + "médium receber o pedido. O médium recebe só o primeiro nome e responde ao mesmo "
                            + "questionário. O sistema confere as respostas campo a campo contra o alvo "
                            + "verdadeiro e contra um chamariz de perfil semelhante, e ninguém julga se uma "
                            + "afirmação encaixa. Tudo acontece pelo site, sem ninguém da equipe no circuito.",
                    "The sitter creates an account, gives the first name of the deceased person and answers "
                            + "a closed questionnaire about them; the answers are locked before any medium "
                            + "receives the request. The medium receives nothing but the first name and "
                            + "answers the same questionnaire. The system compares the answers field by field "
                            + "against the true target and against a decoy of similar profile, and no one "
                            + "judges whether a statement fits. All of it happens through the website, with "
                            + "no one from the team in the loop."),
            List.of(
                    Text.of("Não distingue sobrevivência de telepatia com pessoas vivas: as respostas "
                                    + "existem na memória do consulente e no banco de dados.",
                            "It does not distinguish survival from telepathy with living people: the answers "
                                    + "exist in the sitter's memory and in the database."),
                    Text.of("Não tem experimentador proxy. Nos dois estudos em que o médium não interagiu "
                                    + "com ninguém o resultado foi nulo (Silva e Moreira-Almeida, 2022), e um "
                                    + "nulo aqui não refuta os protocolos com proxy da literatura.",
                            "There is no proxy experimenter. In the two studies where the medium interacted "
                                    + "with no one the result was null (Silva and Moreira-Almeida, 2022), and a "
                                    + "null result here does not refute the proxy protocols of the literature."),
                    Text.of("O questionário fechado só captura o que cabe nos campos: as afirmações livres e "
                                    + "específicas, que costumam ser as mais marcantes, ficam fora da nota.",
                            "The closed questionnaire captures only what fits in its fields: the free, "
                                    + "specific statements, usually the most striking ones, stay out of the "
                                    + "score."),
                    Text.of("O primeiro nome carrega informação, como o gênero e às vezes a geração. O "
                                    + "pareamento e os campos fora da nota reduzem esse vazamento, mas não o "
                                    + "eliminam.",
                            "The first name carries information, such as gender and sometimes generation. "
                                    + "The pairing and the fields kept out of the score reduce that leak, but "
                                    + "do not remove it."),
                    Text.of("Com inscrição aberta, o conluio entre médium e consulente que se conhecem e usam "
                                    + "e-mails diferentes não é detectável com certeza.",
                            "With open sign-up, collusion between a medium and a sitter who know each other "
                                    + "and use different e-mail addresses cannot be detected with certainty.")),
            "E13");

    /** In code order, which since SPEC 0.17 is also the order of importance. */
    private static final List<Protocol> ALL =
            List.of(STRUCTURED_READING, PAIRED_READING, PRECOGNITIVE_FORCED_CHOICE, REMOTE_VIEWING);

    /**
     * The AIR protocols: the automated one, featured, and the supervised paired reading, which
     * needs a team, was discontinued in SPEC 0.14 and stays as a reference only.
     */
    private static final List<Protocol> AIR = List.of(STRUCTURED_READING, PAIRED_READING);

    /**
     * The ESP tests, which say nothing about mediumship. Listed on the Crivo site again since
     * SPEC 0.17, apart from the AIR protocols, with their branch declared (catalog rule 4).
     */
    private static final List<Protocol> ESP = List.of(PRECOGNITIVE_FORCED_CHOICE, REMOTE_VIEWING);

    private ProtocolCatalog() {
    }

    /**
     * Every protocol, in code order, discontinued ones included. Codes were renumbered once, in
     * SPEC 0.17, before any real data; they are never renumbered or reused again.
     */
    public static List<Protocol> all() {
        return ALL;
    }

    /** AIR protocols, in the order the site presents them: the automated one first. */
    public static List<Protocol> air() {
        return AIR;
    }

    /** The ESP tests, quick and anonymous, listed apart from the AIR protocols. */
    public static List<Protocol> esp() {
        return ESP;
    }

    public static Protocol structuredReading() {
        return STRUCTURED_READING;
    }

    public static Optional<Protocol> bySlug(String slug) {
        return ALL.stream().filter(protocol -> protocol.slug().equals(slug)).findFirst();
    }

    /** By code, as in {@code /protocolo/P1}; case is ignored, so {@code p1} finds it too. */
    public static Optional<Protocol> byCode(String code) {
        return ALL.stream().filter(protocol -> protocol.code().equalsIgnoreCase(code)).findFirst();
    }

    public static Protocol pairedReading() {
        return PAIRED_READING;
    }

    public static Protocol precognitiveForcedChoice() {
        return PRECOGNITIVE_FORCED_CHOICE;
    }

    public static Protocol remoteViewing() {
        return REMOTE_VIEWING;
    }
}
