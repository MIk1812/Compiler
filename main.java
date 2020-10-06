import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.CharStreams;
import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException{

	// we expect exactly one argument: the name of the input file
	if (args.length!=1) {
	    System.err.println("\n");
	    System.err.println("Simple calculator\n");
	    System.err.println("=================\n\n");
	    System.err.println("Please give as input argument a filename\n");
	    System.exit(-1);
	}
	String filename=args[0];

	// open the input file
	CharStream input = CharStreams.fromFileName(filename);
	    //new ANTLRFileStream (filename); // depricated
	
	// create a lexer/scanner
	implLexer lex = new implLexer(input);
	
	// get the stream of tokens from the scanner
	CommonTokenStream tokens = new CommonTokenStream(lex);
	
	// create a parser
	implParser parser = new implParser(tokens);
	
	// and parse anything from the grammar for "start"
	ParseTree parseTree = parser.start();

	// Construct an interpreter and run it on the parse tree
	Interpreter interpreter = new Interpreter();
	interpreter.visit(parseTree);
    }
}

// We write an interpreter that implements interface
// "implVisitor<T>" that is automatically generated by ANTLR
// This is parameterized over a return type "<T>" which is in our case
// simply a Double.

class Interpreter extends AbstractParseTreeVisitor<Double> implements implVisitor<Double> {

    static Environment env = new Environment();
    
    public Double visitStart(implParser.StartContext ctx){
		for(implParser.CommandContext c:ctx.cs) visit(c);
		return null;
    };

    public Double visitSingleCommand(implParser.SingleCommandContext ctx){
		return visit(ctx.c);
    }

    public Double visitMultipleCommands(implParser.MultipleCommandsContext ctx){
		for(implParser.CommandContext c:ctx.cs) visit(c);
		return null;
    }
    
    public Double visitAssignment(implParser.AssignmentContext ctx){
		Double v=visit(ctx.e);
		env.setVariable(ctx.x.getText(),v);
		return null;
    }
    
    public Double visitOutput(implParser.OutputContext ctx){
		Double v=visit(ctx.e);
		System.out.println(v);
		return null;
    }
    
    public Double visitParenthesisExpr(implParser.ParenthesisExprContext ctx){
		return visit(ctx.e);
    };
    
    public Double visitVariable(implParser.VariableContext ctx){
		return env.getVariable(ctx.x.getText());
    };
    
    public Double visitAddSub(implParser.AddSubContext ctx){
		String sign = ctx.s.getText();
		if(sign.equals("+"))
			return visit(ctx.e1)+visit(ctx.e2);
		else
			return visit(ctx.e1)-visit(ctx.e2);
    };

    public Double visitMulDev(implParser.MulDevContext ctx){
		String sign = ctx.s.getText();
		if(sign.equals("*"))
			return visit(ctx.e1)*visit(ctx.e2);
		else
			return visit(ctx.e1)/visit(ctx.e2);
    };

    public Double visitPrefix(implParser.PrefixContext ctx){
		String sign = ctx.s.getText();
		Double num = Double.parseDouble(ctx.e.getText())
		if(sign.equals("+"))
			return num;
		else
			return -num;
    };

    public Double visitConstant(implParser.ConstantContext ctx){
		return Double.parseDouble(ctx.c.getText());
    };

    // -- EXPR --


	public Double visitArrayGet(implParser.ArrayGetContext ctx){

		String id = ctx.x.getText();
		int index = visit(ctx.e).intValue();

		return env.getVariable(id+"["+index+"]");
	};


    // -- COMMANDS --

	//ArraySet
	public Double visitArraySet(implParser.ArraySetContext ctx){

		String id = ctx.x.getText();
		int index = visit(ctx.e1).intValue();
		Double equals = visit(ctx.e2);

		env.setVariable(id+"["+index+"]",equals);
		return null;

	};

	//WhileLoop
	public Double visitWhileLoop(implParser.WhileLoopContext ctx){

		while(visit(ctx.c).equals(1.0)){
			visit(ctx.p);
		}

		return null;
	};

	//IfStatement
	public Double visitIfStatement(implParser.IfStatementContext ctx){

		if(visit(ctx.c).equals(1.0))
			visit(ctx.p);

		return null;
	};

	//ForLoop
	public Double visitForLoop(implParser.ForLoopContext ctx){

		String id = ctx.x.getText();

		int n1 = visit(ctx.n1).intValue();
		int n2 = visit(ctx.n2).intValue();

		for (int i = n1; i < n2; i++) {

			env.setVariable(id, i + 0.0);
			visit(ctx.p);
		}
		return null;
	};

	// -- CONDITIONS --

	// !=
    public Double visitUnequal(implParser.UnequalContext ctx){

    	Double v1=visit(ctx.e1);
		Double v2=visit(ctx.e2);

		if (v1.equals(v2)) return 0.0;
		else return 1.0;
    }

    // ==
    public Double visitEqual(implParser.EqualContext ctx){
		Double v1=visit(ctx.e1);
		Double v2=visit(ctx.e2);

		if (v1.equals(v2)) return 1.0;
		else return 0.0;
	}

	// <
	public Double visitLessThan(implParser.LessThanContext ctx){
		Double v1=visit(ctx.e1);
		Double v2=visit(ctx.e2);

		if (v1 < v2) return 1.0;
		else return 0.0;
	}

	// >
	public Double visitGreaterThan(implParser.GreaterThanContext ctx){
		Double v1=visit(ctx.e1);
		Double v2=visit(ctx.e2);

		if (v1 > v2) return 1.0;
		else return 0.0;
	}

	// <=
	public Double visitLessThanOrEqual(implParser.LessThanOrEqualContext ctx){
		Double v1=visit(ctx.e1);
		Double v2=visit(ctx.e2);

		if (v1 <= v2) return 1.0;
		else return 0.0;
	}

	// >=
	public Double visitGreaterThanOrEqual(implParser.GreaterThanOrEqualContext ctx){
		Double v1=visit(ctx.e1);
		Double v2=visit(ctx.e2);

		if (v1 >= v2) return 1.0;
		else return 0.0;
	}

	// &&
	public Double visitAnd(implParser.AndContext ctx){

		Double v1=visit(ctx.c1);
		Double v2=visit(ctx.c2);

		if (v1*v2 > 0 ) return 1.0;
		else return 0.0;
	}

	// |
	public Double visitOr(implParser.OrContext ctx){

		Double v1=visit(ctx.c1);
		Double v2=visit(ctx.c2);

		if (v1+v2 > 0 ) return 1.0;
		else return 0.0;
	}

	// !
	public Double visitNot(implParser.NotContext ctx){

		Double v1=visit(ctx.c);

		if (v1 == 1) return 0.0;
		else return 1.0;
	}

	// ()
	public Double visitParenthesisCondition(implParser.ParenthesisConditionContext ctx){
		return visit(ctx.c);
	};

}

