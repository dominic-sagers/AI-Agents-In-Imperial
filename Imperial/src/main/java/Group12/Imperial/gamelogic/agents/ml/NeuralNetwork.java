package Group12.Imperial.gamelogic.agents.ml;

import java.util.ArrayList;

public class NeuralNetwork { 

    private ArrayList<Layer> layers;
    private double learningRate;
    /*
    TODO:
        1. Make Layer Class
        2. ReLu backprop
        3. 
    */

    public NeuralNetwork(double learningRate) {
        this.learningRate = learningRate;
    }

    public void addLayer(String label, int inputSize, int outputSize, String activationFunctionType) {
        Layer layer = new Layer(label, inputSize, outputSize, new Matrix(outputSize, 1), new Function(activationFunctionType));
        layers.add(layer);
    }

    public void predict(double[] x){
        Matrix input = Matrix.fromArray(x);
        for(Layer layer : layers) {
            input = layer.feedForward(input);
        }
        
    }

    class Layer {
        
        private Matrix weights;
        private Matrix bias;
        private Function activationFunction;

        public final String label;

        public Layer(String label, int inputSize, int outputSize, Matrix bias, Function activationFunction) {
            this.label = label;
            weights = new Matrix(outputSize, inputSize);
            weights.fillRandom();
            this.bias = bias;
            this.bias.fillOnes();
            this.activationFunction = activationFunction;
        }

        public Matrix feedForward(Matrix input) {
            Matrix output = Matrix.multiply(weights, input, true);
            output.add(bias);
            output.applyFunction(activationFunction);
            return output;
        }

        public void backProp(Matrix input ,Matrix outputError , double learningRate){
            Matrix gradientW = Matrix.multiply(outputError, Matrix.transpose(input), true);
            Matrix gradientB = outputError;

            gradientW.multiply(learningRate);
            this.weights = Matrix.subtract(this.weights, gradientW);

            gradientB.multiply(learningRate);
            this.bias = Matrix.subtract(this.bias, gradientB);
        }

        public String getLabel() { return label; }
    }

}