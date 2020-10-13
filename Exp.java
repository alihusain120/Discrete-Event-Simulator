import java.lang.Math;

public class Exp{

    public static void main(String[] args){
			double lambda;
			int N;
			try {
					lambda = Double.parseDouble(args[0]);
					N = Integer.parseInt(args[1]);
			} catch (Exception e){
					System.out.println("Error: Exp.java requires exactly two arguments, (double lambda, int N)");
					return;
			}
       
			for (int i = 0; i < N; i++){
					System.out.println(getExp(lambda));
			}
			return;
    }

    public static double getExp(double lambda){
			double Y = Math.random();
			double x = (Math.log(1-Y) / lambda) * -1.0;
			return x;
    }
}