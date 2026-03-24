package sk.ukf.model;

import java.util.Set;

public class Variable {
    private final String name;
    private final Set<Integer> domain;

    public Variable(String name, Set<Integer> domain) {
        this.name = name;
        this.domain = domain;
    }

    public String getName() { return name; }
    public Set<Integer> getDomain() { return domain; }
}