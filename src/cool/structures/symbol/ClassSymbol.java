package cool.structures.symbol;

import cool.structures.scope.Scope;
import cool.structures.symbol.abstracts.Symbol;

import java.util.*;

public class ClassSymbol extends Symbol implements Scope {
    private Scope parentScope;
    private final Map<String, Symbol> attributes;
    private final Map<String, Symbol> methods;

    public ClassSymbol(String name, Scope parentScope) {
        super(name);
        if (parentScope == this) {
            throw new IllegalStateException("A scope cannot be its own parent.");
        }
        this.parentScope = parentScope;
        this.attributes = new LinkedHashMap<>();
        this.methods = new LinkedHashMap<>();
    }

    public void setParentScope(Scope parentScope) {
        this.parentScope = parentScope;
    }

    @Override
    public boolean add(Symbol sym) {
        if (sym instanceof MethodSymbol) {
            if (methods.containsKey(sym.getName())) {
                return false;
            }
            methods.put(sym.getName(), sym);
        } else if (sym instanceof IdSymbol) {
            if (attributes.containsKey(sym.getName())) {
                return false;
            }
            attributes.put(sym.getName(), sym);
        } else {
            System.out.println("Adding a symbol of unknown type to the class");
        }
        return true;
    }

    @Override
    public Symbol lookup(String name) {
        Symbol sym = attributes.get(name);
        if (sym == null) {
            sym = methods.get(name);
        }

        if (sym != null) {
            return sym;
        }

        if (parentScope != null) {
            return parentScope.lookup(name);
        }

        return null;
    }

    public boolean isSubtype(ClassSymbol potentialSubtype) {
        ClassSymbol current = potentialSubtype;

        while (current != null) {
            if (this.equals(current)) {
                return true;
            }
            if (!(current.getParent() instanceof ClassSymbol)) {
                break;
            }
            current = (ClassSymbol) current.getParent();
        }
        return false;
    }

    public ClassSymbol findLCA(ClassSymbol other) {
        final Set<ClassSymbol> thisAncestors = new HashSet<>();
        ClassSymbol current = this;
        while (current != null) {
            thisAncestors.add(current);
            if (!(current.getParent() instanceof ClassSymbol)) {
                break;
            }
            current = (ClassSymbol) current.getParent();
        }

        current = other;
        while (current != null) {
            if (thisAncestors.contains(current)) {
                return current;
            }
            if (!(current.getParent() instanceof ClassSymbol)) {
                break;
            }
            current = (ClassSymbol) current.getParent();
        }
        return null;
    }

    @Override
    public Scope getParent() {
        return parentScope;
    }

    public Map<String, Symbol> getMethods() {
        return methods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassSymbol that = (ClassSymbol) o;
        return this.getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
