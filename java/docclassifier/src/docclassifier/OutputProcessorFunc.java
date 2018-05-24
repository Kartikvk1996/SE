package docclassifier;

public interface OutputProcessorFunc {
    
    /**
     * Function to process the neural network's output and make it ready to use
     * in back propagation.
     * @param output : this is the current output of neural net
     * @return return the desired output.
     */
    double[] process(double output[]);

    public double[] deriv(double[] err);
}
