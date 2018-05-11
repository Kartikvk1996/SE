package docclassifier;

interface ErrorFunc {
    
    /**
     * Computes the error of the network given expected output and actual output
     * @param eout : expected output
     * @param out : actual output
     * @return : error vector
     */
    public double[] error(double[] eout, double[] out);
    
    /**
     * Computes the derivative of network.
     * @param eout : expected output
     * @param out : actual output 
     * @return 
     */
    public double[] deriv(double[] eout, double[] out);
}
