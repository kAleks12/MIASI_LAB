package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.stringtemplate.v4.ST;

public class InsertDocListener  extends JavaParserBaseListener{
    TokenStreamRewriter rewriter;
    public InsertDocListener(CommonTokenStream tokens) {
        rewriter = new TokenStreamRewriter(tokens);
    }

    @Override
    public void exitConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        ParserRuleContext ctx1 = ctx.getParent().getParent();
        ST docST = new ST("/**\n\t<name>\n*/\n");
        docST.add("name",ctx.identifier().getText());
        rewriter.insertBefore(ctx1.start,docST.render());
    }

    @Override
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        ParserRuleContext ctx1 = ctx.getParent().getParent();
        String doc = String.format("/**\n%s %s\n*/\n",
                ctx.typeTypeOrVoid().getText(), ctx.identifier().getText());
        rewriter.insertBefore(ctx1.start,doc);
    }
}
