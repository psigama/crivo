package com.psigama.airlab;

import com.psigama.airlab.cli.PackageVerification;
import com.psigama.airlab.web.StaticSite;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Crivo (codiname AIR Lab): a modular monolith for blind mediumship research protocols.
 *
 * <p>Started from the command line on Windows and Linux alike, with no container runtime
 * involved (Constitution, architectural rule 8). See {@code scripts/start.sh} and
 * {@code scripts/start.ps1}.
 *
 * <p>{@code airlab verificar-pacote} does not start Spring either: it checks a P1 verification package
 * (SPEC E9) from the file alone, which is the point of it.
 *
 * <p>{@code airlab site} does not start Spring: it writes the static site
 * with the editorial pages (SPEC 0.21, section 1.3) and needs no database, so the workflow that
 * publishes the site has nothing to connect to.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AirLabApplication {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("site")) {
            System.exit(StaticSite.run(args, System.out, System.err));
        }
        if (args.length > 0 && args[0].equals(PackageVerification.COMMAND)) {
            System.exit(PackageVerification.run(args, System.out, System.err));
        }
        SpringApplication.run(AirLabApplication.class, args);
    }
}
