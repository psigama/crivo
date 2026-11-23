/**
 * The protocol catalog of the platform (SPEC, section 1.1).
 *
 * <p>No state, and no dependency on any domain module: the catalog is specification turned into
 * code, so the pages, the study creation and the verification package all read the same list.
 *
 * <p>The one dependency is {@code support}, for {@code Text}: since SPEC 0.28 (section 1.5, rule 6)
 * the name, the summary and the declared limits of a protocol exist in the two languages of the
 * interface, and the questionnaire labels with them. {@code support} is the shared primitives
 * module, open to every domain module and with no knowledge of any of them.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "catalog",
        allowedDependencies = {"support"})
package io.github.rogeriocisi.airlab.catalog;
