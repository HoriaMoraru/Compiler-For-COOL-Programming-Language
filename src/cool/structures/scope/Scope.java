package cool.structures.scope;

import cool.structures.symbol.abstracts.Symbol;

public interface Scope {
    boolean add(Symbol sym);
    
    Symbol lookup(String str);
    
    Scope getParent();
}
