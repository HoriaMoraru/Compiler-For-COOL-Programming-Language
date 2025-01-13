package cool.structures.symbol.abstracts;

public abstract class Symbol {
    protected String name;
    
    protected Symbol(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }

}
