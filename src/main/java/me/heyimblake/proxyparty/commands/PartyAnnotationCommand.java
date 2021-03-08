package me.heyimblake.proxyparty.commands;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PartyAnnotationCommand {

    /**
     * The name of the subcommand.
     *
     * @return subcommand name
     */
    String name();

    /**
     * The usage of the subcommand.
     *
     * @return subcommand syntax
     */
    String syntax();

    /**
     * Information about the subcommand.
     *
     * @return subcommand description
     */
    String description();

    /**
     * Subcommand requires that the user complete arguments.
     *
     * @return true if requires argument completion
     */
    boolean requiresArgumentCompletion();

    /**
     * Does the subcommand require the command sender to be the party leader
     *
     * @return true if command sender must be the party leader
     */
    boolean leaderExclusive() default false;

    /**
     * Does the subcommand require the command sender to be in a party
     *
     * @return true if sender must be in a party, false otherwise
     */
    boolean mustBeInParty() default true;
}