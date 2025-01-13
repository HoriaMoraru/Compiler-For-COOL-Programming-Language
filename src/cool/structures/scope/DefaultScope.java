package cool.structures.scope;

import cool.structures.symbol.abstracts.Symbol;

import java.util.*;

public class DefaultScope implements Scope {
    
    private final Map<String, Symbol> symbols = new LinkedHashMap<>();
    
    private final Scope parent;
    
    public DefaultScope(Scope parent) {
        if (parent == this) {
            throw new IllegalStateException("A scope cannot be its own parent.");
        }
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        // Reject duplicates in the same scope.
        if (symbols.containsKey(sym.getName()))
            return false;
        
        symbols.put(sym.getName(), sym);
        
        return true;
    }

    @Override
    public Symbol lookup(String name) {
        var sym = symbols.get(name);
        
        if (sym != null)
            return sym;
        
        if (parent != null)
            return parent.lookup(name);
        
        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }
    
    @Override
    public String toString() {
        return symbols.values().toString();
    }

}