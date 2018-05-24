package docclassifier;

public interface ActivationFunc {
    
    /**
     * Compute activation f(input) given the input
     * @param input : array of inputs to f
     * @return set of f(x)
     */
    double[] compute(double[] input);
    
    /**
     * Compute the derivative of the above function.
     * @param input : array of inputs for which derivative should be computed
     * @return : derivative[i] of above function for value input[i]
     */
    double[] derivative(double[] input);
}
