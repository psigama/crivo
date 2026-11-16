package io.github.rogeriocisi.airlab.cli;

import io.github.rogeriocisi.airlab.structured.VerificationPackageCheck;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@code airlab verificar-pacote ARQUIVO}: checks a P1 verification package (SPEC E9) with no
 * database and no server — only the file and this artefact, which anyone can build from the
 * public repository. Like {@code airlab site}, it does not start Spring.
 *
 * <p>Exit code 0 when every check holds, 1 when any fails, 2 when the file cannot be read.
 */
public final class PackageVerification {

    public static final String COMMAND = "verificar-pacote";

    private PackageVerification() {
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        String file = null;
        for (int i = 1; i < args.length; i++) {
            if (!args[i].startsWith("--")) {
                file = args[i];
                break;
            }
        }
        if (file == null) {
            err.println("Uso: airlab " + COMMAND + " pacote-EST-XXXXXXXX.json");
            return 2;
        }
        String json;
        try {
            json = Files.readString(Path.of(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            err.println("Nao foi possivel ler " + file + ".");
            return 2;
        }
        VerificationPackageCheck.Result result = VerificationPackageCheck.check(json);
        out.println("Crivo (AIR Lab) - verificacao do pacote, sem banco e sem servidor");
        out.println("SHA-256 do arquivo: " + result.sha256());
        out.println("");
        for (VerificationPackageCheck.Line line : result.lines()) {
            out.println((line.ok() ? "  ok    " : "  FALHA ") + line.text());
        }
        out.println("");
        out.println(result.ok() ? "Resultado: tudo confere." : "Resultado: ha falhas.");
        return result.ok() ? 0 : 1;
    }
}
