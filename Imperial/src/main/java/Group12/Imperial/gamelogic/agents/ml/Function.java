package Group12.Imperial.gamelogic.agents.ml;

public class Function {

    private int type;
    
    public Function(String functionType) {
        if(functionType == "relu") {
            this.type = 0;
        } else if(functionType == "sigmoid") {
            this.type = 1;
        } else if(functionType == "mse") {
            this.type = 2;
        }
    }

    public String getType() {
        if(type == 0) {
            return "relu";
        } else if(type == 1) {
            return "sigmoid";
        } else if(type == 2) {
            return "mse";
        } else {
            System.out.println("Function has no known type");
            return "";
        }
    }

    public double apply(double x) {
        if(type == 0) {
            return Math.max(0.0, x);
        } else if(type == 1) {
            return 1.0 / (1.0 + Math.exp(-x));
        } else if(type == 2) {
            return Math.pow(x, 2);
        } else {
            System.out.println("Function has no known type");
            return 0.0;
        }
    }

    public double applyDerivative(double x) {
        if(type == 0) {
            if(x > 0.0) { return 1.0; 
            } else { return 0.0; }
        } else if(type == 1) {
            return x * (1.0 - x);
        } else if(type == 2) {
            return 2.0 * x;
        } else {
            System.out.println("Function has no known type");
            return 0.0;
        }
    }
}
