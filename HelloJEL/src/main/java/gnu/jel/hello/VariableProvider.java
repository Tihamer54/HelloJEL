package gnu.jel.hello;

/**
 * This class is the supporting class needed for a "Hello World" example of the Java Expression created by Konstantin L. Metlov <metlov@fti.dn.ua>
 * It holds any variables or methods in the expression described in JavaEvaluator.
 * 
 * Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, 
 * Version 1.3 or any later version published by the Free Software Foundation
 * 
 * @author ttoth-fejel
 */
public class VariableProvider {
	// Specify all your variables, and their types.
	public int xVar;
	public int yVar;
	public int zVar;

	// The above variables must be accessible.
	public int x() {return xVar;}
	public int y() {return yVar;}
	public int z() {return zVar;}
	
	
	public static boolean isPrime(double number) {
		return isPrime((int)number);
	}
	
	public static boolean isPrime(int number) {
		boolean flag = false;
		for (int i=2; i<= Math.floor(Math.sqrt(number)); ++i) {
			if (number%i == 0) {
				return false;
			}
		}
		return true;
	}

	
	/**
	 *  We *could* cheat and just have an array: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233...
	 * @param x
	 * @return
	 */
	public static boolean isFibonacci(int x) {
        int previousPreviousNumber, previousNumber = 0, currentNumber = 1;
        for (int i=1; i<1000 ; i++) {
            previousPreviousNumber = previousNumber;
            previousNumber = currentNumber;
            currentNumber = previousPreviousNumber + previousNumber;
            //System.out.println("Current Number=" + currentNumber);
            if (x == currentNumber) { return true; } 
            else if (x < currentNumber) { return false; } 
        }
        return false;
    }
	
	
	/**
	 * Checks if the square root of x is a whole number.
	 * @param x
	 * @return
	 */
	public static boolean isPerfectSquare(int x) { 
		double square = Math.sqrt((double)x); 
		return ((square - Math.floor(square)) == 0); 
	} 


	/**
	 * Checks if the cube root of x is a whole number.
	 * @param x
	 * @return
	 */
	public static boolean isPerfectCube(int x) { 
		
		double cube = Math.cbrt((double)x); 
		return ((cube - Math.floor(cube)) == 0); 
	} 
	

	/**
	 * A number that corresponds to a configuration of points which form a square pyramid.
	 * The top layer has one point; the second has four, and the third has nine, fourth has 16, etc. 
	 * Looks like this from front and side (but it's in 3D):
	 *       *
	 *      * *
	 *     * * *
	 *    * * * *
	 */
	public static boolean isSquarePyramidNumber(int x) {
		for (int i=1; i<17; i++) {
			int squarePyramidNumber = (i * (i + 1) * ((2*i) + 1))/6;
			if (squarePyramidNumber == x) { return true; }
		}
		return false;
	}
		
}
