package compiler.symbol;

import compiler.symbol.entry.Entry;
import compiler.type.ScopeType;

import java.util.HashMap;

public class Scope {
    private final ScopeType type;
    private final Scope parent;
    private final HashMap<String, Entry> entrys;

    public Scope(ScopeType type, Scope parent) {
        this.type = type;
        this.parent = parent;
        this.entrys = new HashMap<>();
    }

    public Scope getInnerLoopScope() {
        if (type == ScopeType.WHILE) {
            return this;
        }
        if (parent == null) {
            return null;
        }
        return parent.getInnerLoopScope();
    }

    public Scope getInnerFuncScope() {
        if (type == ScopeType.FUNC) {
            return this;
        }
        if (parent == null) {
            return null;
        }
        return parent.getInnerFuncScope();
    }

    public Scope getParent() {
        return parent;
    }

    public void insert(Entry entry) throws Exception {
        String name = entry.getName();
        if (entrys.containsKey(name)) {
            throw new Exception();
        }
        entrys.put(name, entry);
    }

    public Entry consult(String name) throws Exception {
        System.out.println("当前符号表" + entrys + "，查询 name=" + name);
        if (entrys.containsKey(name)) {
            System.out.println("存在" + name);
            return entrys.get(name);
        }
        if (parent == null) {
            throw new Exception();
        }
        return parent.consult(name);
    }

    public ScopeType getType() {
        return type;
    }
}