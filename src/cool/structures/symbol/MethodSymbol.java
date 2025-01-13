package cool.structures.symbol;

import cool.ast.nodes.Method;
import cool.structures.scope.Scope;
import cool.structures.symbol.abstracts.Symbol;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MethodSymbol extends IdSymbol implements Scope {
    private final Scope parentScope;
    private final Map<String, Symbol> symbols;
    private Method method;

    public MethodSymbol(String name, TypeSymbol type, Scope parentScope, Method method) {
        super(name, type);
        if (parentScope == this) {
            throw new IllegalStateException("A scope cannot be its own parent.");
        }
        this.parentScope = parentScope;
        this.symbols = new LinkedHashMap<>();
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public boolean add(Symbol sym) {
        return symbols.putIfAbsent(sym.getName(), sym) == null;
    }

    @Override
    public Symbol lookup(String str) {
        return Optional.ofNullable(symbols.get(str))
                .orElseGet(() -> parentScope != null ? parentScope.lookup(str) : null);
    }

    @Override
    public Scope getParent() {
        return parentScope;
    }
}
