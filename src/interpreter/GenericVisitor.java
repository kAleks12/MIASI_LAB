package interpreter;

import grammar.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

public class GenericVisitor extends firstBaseVisitor<Result> {
    private TokenStream tokStream = null;
    private CharStream input = null;

    public GenericVisitor(CharStream inp) {
        super();
        this.input = inp;
    }

    public GenericVisitor(TokenStream tok) {
        super();
        this.tokStream = tok;
    }

    public GenericVisitor(CharStream inp, TokenStream tok) {
        super();
        this.input = inp;
        this.tokStream = tok;
    }

    private String getText(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        if (input == null) throw new RuntimeException("Input stream undefined");
        return input.getText(new Interval(a, b));
    }

    @Override
    public Result visitIf_stat(firstParser.If_statContext ctx) {
        Result ifResult = null;
        if (visit(ctx.cond).getAsBoolean()) {
            ifResult = visit(ctx.then);
        } else {
            if (ctx.else_ != null)
                ifResult = visit(ctx.else_);
        }
        return ifResult;
    }

    @Override
    public Result visitPrint_stat(firstParser.Print_statContext ctx) {
        var st = ctx.expr();
        var result = visit(st);
//        System.out.printf("|%s=%d|\n", st.getText(), result); //nie drukuje ukrytych ani pominiętych spacji
//        System.out.printf("|%s=%d|\n", getText(st),  result); //drukuje wszystkie spacje
        switch (result.clazz()) {
            case ERROR -> System.out.printf("(%s) = %s\n", tokStream.getText(st), result.value());
            case INTEGER -> System.out.printf("(%s) = %d\n", tokStream.getText(st), result.getAsInt());
            case DOUBLE -> System.out.printf("(%s) = %f\n", tokStream.getText(st), result.getAsDouble());
            case BOOLEAN -> System.out.printf("(%s) = %b\n", tokStream.getText(st), result.getAsBoolean());
        }
         //drukuje spacje z ukrytego kanału, ale nie ->skip
        return result;
    }

    @Override
    public Result visitInt_tok(firstParser.Int_tokContext ctx) {
        return new Result(ctx.INT().getText(), Type.INTEGER);
    }

    @Override
    public Result visitDouble_tok(firstParser.Double_tokContext ctx) {
        return new Result(ctx.DOUBLE().getText(), Type.DOUBLE);
    }

    @Override
    public Result visitPars(firstParser.ParsContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Result visitBinOp(firstParser.BinOpContext ctx) {
        Result opResult = null;
        Result leftRes = visit(ctx.l);
        Result rightRes = visit(ctx.r);
        Type opType = getBinOpType(leftRes, rightRes);
        switch (ctx.op.getType()) {
            case firstLexer.ADD -> {
                if (opType == Type.DOUBLE) {
                    Double value = leftRes.getAsDouble() + rightRes.getAsDouble();
                    opResult = new Result(value.toString(), Type.DOUBLE);
                } else if (opType == Type.INTEGER) {
                    Integer value = leftRes.getAsInt() + rightRes.getAsInt();
                    opResult = new Result(value.toString(), Type.INTEGER);
                } else {
                    opResult = new Result("Failed type resolution", Type.ERROR);
                }
            }
            case firstLexer.SUB -> {
                if (opType == Type.DOUBLE) {
                    Double value = leftRes.getAsDouble() - rightRes.getAsDouble();
                    opResult = new Result(value.toString(), Type.DOUBLE);
                } else if (opType == Type.INTEGER) {
                    Integer value = leftRes.getAsInt() - rightRes.getAsInt();
                    opResult = new Result(value.toString(), Type.INTEGER);
                } else {
                    opResult = new Result("Failed type resolution", Type.ERROR);
                }
            }
            case firstLexer.MUL -> {
                if (opType == Type.DOUBLE) {
                    Double value = leftRes.getAsDouble() * rightRes.getAsDouble();
                    opResult = new Result(value.toString(), Type.DOUBLE);
                } else if (opType == Type.INTEGER) {
                    Integer value = leftRes.getAsInt() * rightRes.getAsInt();
                    opResult = new Result(value.toString(), Type.INTEGER);
                } else {
                    opResult = new Result("Failed type resolution", Type.ERROR);
                }
            }
            case firstLexer.DIV -> {
                if (opType == Type.DOUBLE || opType == Type.INTEGER) {
                    try {
                        Double value = leftRes.getAsDouble() / rightRes.getAsDouble();
                        opResult = new Result(value.toString(), Type.DOUBLE);
                    } catch (Exception e) {
                        opResult = new Result("Division by zero", Type.ERROR);
                    }
                } else {
                    opResult = new Result("Failed type resolution", Type.ERROR);
                }
            }
        }
        return opResult;
    }

    @Override
    public Result visitLogOp(firstParser.LogOpContext ctx) {
        Boolean rawValue = switch (ctx.op.getType()) {
            case firstLexer.AND -> visit(ctx.l).getAsBoolean() && visit(ctx.r).getAsBoolean();
            case firstLexer.OR -> visit(ctx.l).getAsBoolean() || visit(ctx.r).getAsBoolean();
            case firstLexer.NOT -> !visit(ctx.r).getAsBoolean();
            case firstLexer.EQ -> visit(ctx.l).getAsDouble().equals(visit(ctx.r).getAsDouble());
            case firstLexer.NEQ -> !visit(ctx.l).getAsDouble().equals(visit(ctx.r).getAsDouble());
            default -> false;
        };
        return new Result(rawValue.toString(), Type.BOOLEAN);
    }

    private Type getBinOpType(Result part1, Result part2) {
        if (getPartBinOpType(part1) == getPartBinOpType(part2)) {
            return Type.INTEGER;
        } else {
            return Type.DOUBLE;
        }
    }

    private Type getPartBinOpType(Result part) {
        return switch (part.clazz()) {
            case BOOLEAN, INTEGER -> Type.INTEGER;
            case DOUBLE -> Type.DOUBLE;
            case ERROR -> Type.ERROR;
        };
    }
}
