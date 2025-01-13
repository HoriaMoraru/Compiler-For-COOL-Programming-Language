package cool.structures.symbol;

import cool.structures.symbol.abstracts.Symbol;

public class IdSymbol extends Symbol {
    private TypeSymbol type;

    public IdSymbol(String name, TypeSymbol type) {
        super(name);
        this.type = type;
    }

    public TypeSymbol getType() {
        return type;
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }
}
