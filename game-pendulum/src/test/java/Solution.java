import java.util.Scanner;

public class Solution {
	static double dt = .05, m = 1, l = 1, g = 10;
	
    public static void main(String[] args) {
        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
        
        int turn = 0;
        
        while (true) {
        	turn++;
        	
        	// read inputs
        	String s = scanner.nextLine();
            System.err.println(s);
            String[] input = s.split(" ");
        	float cosTheta = Float.parseFloat(input[0]);
            float sinTheta = Float.parseFloat(input[1]);
            float dotTheta = Float.parseFloat(input[2]);            
            
            // write output
	float torque = 1;
            System.out.println(torque);
        }
                
    }
    private static double get_newthdot(double dotTheta, double theta, double torque) {
    	return dotTheta + (-3 * g / (2 * l) * Math.sin(theta + Math.PI) + 3. / (m * l * l) * torque) * dt;
    	
    }
    private static double get_torque_des(double dotTheta_des, double dotTheta, double theta) {
    	return ((dotTheta_des - dotTheta) / dt + 3 * g / (2 * l) * Math.sin(Math.PI + theta)) * m * l * l / 3;        	
    }
    
    private static double _clip(double x, double low, double high) {
		return Math.min(Math.max(x, low),high);
	}
}
