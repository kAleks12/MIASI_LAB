package compiler;


import org.antlr.v4.runtime.tree.TerminalNode;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import grammar.firstBaseVisitor;
import grammar.firstParser;

public class EmitVisitor extends firstBaseVisitor<ST> {
    private final STGroup stGroup;
    private Integer counter = 0;

    public EmitVisitor(STGroup group) {
        super();
        this.stGroup = group;
    }

    @Override
    protected ST defaultResult() {
        return stGroup.getInstanceOf("deflt");
    }

    @Override
    protected ST aggregateResult(ST aggregate, ST nextResult) {
        if(nextResult!=null)
            aggregate.add("elem",nextResult);
        return aggregate;
    }


    @Override
    public ST visitTerminal(TerminalNode node) {
        return new ST("Terminal node:<n>").add("n",node.getText());
    }

    @Override
    public ST visitInt_tok(firstParser.Int_tokContext ctx) {
        ST st = stGroup.getInstanceOf("int");
        st.add("i",ctx.INT().getText());
        return st;
    }

    @Override
    public ST visitBinOp(firstParser.BinOpContext ctx) {
        ST st_add = stGroup.getInstanceOf("dodaj");
        ST st_sub = stGroup.getInstanceOf("odejmij");
        ST st_div = stGroup.getInstanceOf("podziel");
        ST st_mul = stGroup.getInstanceOf("mnoz");
        return switch (ctx.op.getType()) {
            case firstParser.ADD -> st_add.add("p1",visit(ctx.l)).add("p2",visit(ctx.r));
            case firstParser.SUB -> st_sub.add("p1",visit(ctx.l)).add("p2",visit(ctx.r));
            case firstParser.DIV -> st_div.add("p1",visit(ctx.l)).add("p2",visit(ctx.r));
            case firstParser.MUL -> st_mul.add("p1",visit(ctx.l)).add("p2",visit(ctx.r));
            default ->  null;
        };
    }

    @Override
    public ST visitIf_stat(firstParser.If_statContext ctx) {
        ST st_if = stGroup.getInstanceOf("warunek");
        counter++;
        return st_if.add("cond", visit(ctx.cond))
                .add("counter", counter)
                .add("then", visit(ctx.then))
                .add("_else", visit(ctx.else_));
    }
}
