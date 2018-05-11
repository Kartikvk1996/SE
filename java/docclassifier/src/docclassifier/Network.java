package docclassifier;

public class Network {

    private final ErrorFunc efunc;
    private final ActivationFunc[] afuncs;
    private final OutputProcessorFunc oproc;
    private final double[][][] weights;
    private final double[][] acts;
    private final int[] nperl;
    private final double eta;

    public Network(double eta, ErrorFunc efunc, OutputProcessorFunc proc, int... nperl) {
        this.eta = eta;
        this.oproc = proc;
        this.nperl = nperl;
        this.efunc = efunc;
        acts = new double[nperl.length][];
        this.afuncs = new ActivationFunc[nperl.length - 1];
        weights = new double[nperl.length - 1][][];

        for (int i = 1; i < nperl.length; i++) {
            weights[i] = new double[nperl[i - 1]][nperl[i]];
        }

        /* set default activation as sigmoid. */
        SigmoidFunc sfunc = new SigmoidFunc();
        for (int i = 0; i < nperl.length; i++) {
            afuncs[i] = sfunc;
        }
    }

    public void setActivation(int index, ActivationFunc actfunc) {
        afuncs[index] = actfunc;
    }

    public double[] forward(double[] input) {
        double[] out = input;
        for (int i = 0; i < weights.length; i++) {
            out = afuncs[i].compute(multiply(out, weights[i]));
        }
        return out;
    }

    public double[] backprop(double[] input, double[] eout) {
        double[] out = forward(input);
        double[] err = efunc.deriv(eout, out);
        
        if(oproc != null) {
            err = oproc.deriv(err);
        }
        
        err = hadmard(err, afuncs[afuncs.length - 1].derivative(acts[acts.length - 1]));
        
        
        for (int i = weights.length - 1; i > -1; i--) {
            
            
            
        }
        
        return out;
    }

    private double[] hadmard(double[] a, double[] b) {
        double[] m = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            m[i] = a[i] * b[i];
        }
        return m;
    }
    
    /**
     * Difference of a and b (a - b)
     *
     * @param a : vector a
     * @param b : vector b
     * @return
     */
    private double[] diff(double[] a, double[] b) {
        double[] d = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            d[i] = a[i] - b[i];
        }
        return d;
    }

    private double[] multiply(double[] a, double[][] b) {
        double[] out = new double[b[0].length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                out[j] += a[i] * b[i][j];
            }
        }
        return out;
    }
}
