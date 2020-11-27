package gnu.jel.hello;

import gnu.jel.Evaluator;
import gnu.jel.CompiledExpression;
import gnu.jel.Library;
import gnu.jel.CompilationException;

/**
 * This class is one of two classes needed for a "Hello World" (mavenized) Eclipse project of the Java Expressions Library 
 * that was created by Konstantin L. Metlov <metlov@fti.dn.ua>.
 * The main method below contains a few examples.
 * 
 * Note: While I have mavenized this project, currently mvnrepository.com only has the 2.0.1 version, 
 * while the version at https://www.gnu.org/software/jel/ is up to 2.1.1.  On my TODO list.
 * 
 * Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, 
 * Version 1.3 or any later version published by the Free Software Foundation
 */
public class JavaEvaluator {

	public static void main(String[] args) {
		System.out.println("\nJavaEvaluator.main: The simplest example does not use any variables.");
		System.out.println("   result: " + JavaEvaluator.evaluate("2+2"));   							// answer: 4
		System.out.println("\nJavaEvaluator.main: The following example uses one variable.");
		System.out.println("   result: " + JavaEvaluator.evaluate("2 + 3 / x", 4));   					// answer: 2.75
		System.out.println("\nJavaEvaluator.main: The following examples uses three variables.");
		System.out.println("   result: " + JavaEvaluator.evaluate("(y + x + z)", 1, 10, 100));			// answer: 111.0
		System.out.println("   result: " + JavaEvaluator.evaluate("(y + x + z) == 111", 1, 10, 100));	// answer: true
		System.out.println("   result: " + JavaEvaluator.evaluate("(y + x + z) == 111", 3, 4, 5));		// answer: false
		System.out.println("   result: " + JavaEvaluator.evaluate("(((x*x) + (y*y)) == (z*z))", 3, 4, 5));		// answer: true
		System.out.println("\nJavaEvaluator.main: The following example uses one variable in classes defined elsewhere (VariableProvider).");
		System.out.println("   result: " + JavaEvaluator.evaluate("isSquarePyramidNumber(x) && isFibonacci(y)", 30, 55));
		System.out.println("   result: " + JavaEvaluator.evaluate("isPrime(x) && isPerfectCube(y) && isPerfectSquare(z)", 67, 27, 64));
		
		System.out.println("\nIf lots of data must fed into the same expression, then compile it separately first.");
		VariableProvider variables = new VariableProvider();
		Object[] context = getContext(variables);
		Library library = JavaEvaluator.setupLibrary(variables);
		String expression = "(y + x + z)";
		CompiledExpression compiled = JavaEvaluator.compile(expression, library);
		for (int i=0; i<10; i++) {
			System.out.println(i + ".  Loop result ==> " + JavaEvaluator.run(expression, compiled, variables, context, i, i*2, i*3));
		}
	}
	
	
	/*
	 * Convenience functions
	 * We *could* have three entire evaluate methods, but the only difference would be
	 * the three lines variables.xVar=x;  variables.yVar=y; variables.zVar=z;
	 * If they are strings or BigDecimals, or whatever, then you'll need to change that too.
	 */
	static Object evaluate(String expression) {
		return evaluate(expression, 0, 0, 0);
	}
	
	static Object evaluate(String expression, int x) {
		return evaluate(expression, x, 0, 0);
	}
	
	static Object evaluate(String expression, int x, int y) {
		return evaluate(expression, x, y, 0);
	}


	/**
	 * Does everything in one function.
	 * See https://www.gnu.org/software/jel/manual.html
	 * @param expression
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	static Object evaluate(String expression, int x, int y, int z) {
		VariableProvider variables = new VariableProvider();
		Object[] context = getContext(variables);
		Library library = setupLibrary(variables);
		CompiledExpression expressionCompiled = compile(expression, library);
		return JavaEvaluator.run(expression, expressionCompiled, variables, context, x, y, z);
	}
	

	static Object[] getContext(VariableProvider variables) {
		Object[] context = new Object[1];
		context[0] = variables;
		return context;
	}

	
	static Library setupLibrary(VariableProvider variables) {
		Class[] staticLibrary = new Class<?>[2];
		try {
			staticLibrary[0] = Class.forName("java.lang.Math");
			staticLibrary[1] = Class.forName("gnu.jel.hello.VariableProvider");
			// If you add any more libraries that contain methods that you want to use in your expressions, 
			// then this is where you add them, but then you must increment the staticLibrary size above.
		} catch (ClassNotFoundException cnfex) {
			cnfex.printStackTrace();
		};
		Class[] dynamicLibrary = new Class[1];
		dynamicLibrary[0] = variables.getClass();
		return new Library(staticLibrary, dynamicLibrary, null, null, null);
	}

	
	/**
	 * Once an expression is compiled, it can be run much faster.
	 * @param expression
	 * @param library
	 * @return
	 */
	static CompiledExpression compile(String expression, Library library) {
		// See the JavaDoc on why the Math.random function needs to be marked as state dependent.
		try {
			library.markStateDependent("random", null);
		} catch (CompilationException cex) {
			cex.printStackTrace();
		};

		System.out.println("Compiling Expression: " + expression);
		CompiledExpression expressionCompiled = null;
		try {
			expressionCompiled = Evaluator.compile(expression, library);
		} catch (CompilationException cex) {
			System.err.print("--- COMPILATION ERROR :");
			System.err.println(cex.getMessage());
			System.err.print("                       ");
			System.err.println(expression);
			int column = cex.getColumn(); // Column, where error was found
			for(int i=0; i<column+23-1; i++) System.err.print(' ');
			System.err.println('^');
			return null;
		}
		return expressionCompiled;
	}


	/**
	 * Once you have the expression compiled, and the variables and libraries set, 
	 * you can re-run the expression with different variables much faster.
	 * Note that the expression here is not necessarily identical the one that is compiled
	 * (though it *should* be if you want output that makes sense.  It is not actually used in the computation).
	 * 
	 * @param expression
	 * @param expressionCompiled
	 * @param variables
	 * @param context
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static Object run(String expression, CompiledExpression expressionCompiled, VariableProvider variables, Object[] context, int x, int y, int z) {
		Object result = null;
		if (expressionCompiled != null) {
			try {
				// This is where are the incoming variables are moved to the variable provider to be evaluated in the context.
				variables.xVar=x; 
				variables.yVar=y;
				variables.zVar=z;
				result = expressionCompiled.evaluate(context);
				System.out.println("Evaluating Expression: " + expression + " where x=" + x + " y=" + y + " z=" + z);
			} catch (Throwable e) {
				System.err.println("Exception emerged from JEL compiled" + " code (IT'S OK):");
				System.err.print(e);
			};
			// Print result
			if (result==null) {  System.out.println("void"); }
			else { 
				//System.out.println("Expression " + expression + " ==> " + result.toString()); 
				return result;
			}
		} else {
			System.err.println("Missing compiler for expression: " + expressionCompiled.toString() + " where x=" + x + " y=" + y + " z=" + z);
			return null;
		}
		System.out.println("Expression: " + expressionCompiled.toString() + " where x=" + x + " y=" + y + " z=" + z + " result=" + result);
		return result;
	}

}

