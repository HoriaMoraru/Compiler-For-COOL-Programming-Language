package cool.structures.symbol;

import cool.structures.symbol.abstracts.Symbol;

import java.util.Objects;

public class TypeSymbol extends Symbol {
    public TypeSymbol(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "TypeSymbol{name='" + name + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TypeSymbol that = (TypeSymbol) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
