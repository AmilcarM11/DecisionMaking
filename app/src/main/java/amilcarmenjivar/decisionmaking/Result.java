package amilcarmenjivar.decisionmaking;

/**
 *
 * Created by Amilcar Menjivar on 30/04/2015.
 */
public class Result { // TODO: document

    // User preferences
    public final int[][][] attributeInfo;
    public final int[][][] profileInfo;

    // Candidates
    public final double[][][] mcaj;     // Attribute x (cPair x Judge)
    public final double[][] mvca;       // Attribute x cPair
    public final double[][][] mmpa;     // Attribute x (Candidate x Candidate)
    public final double[][] mpca;       // Candidate x Attribute

    // Attributes
    public final double[][][] mcpj;     // Profile x (aPair x Judge)
    public final double[][] mvcp;       // Profile x aPair
    public final double[][][] mmpp;     // Profile x (Attribute x Attribute)
    public final double[][] mpap;       // Attribute x Profile

    // Result
    public final double[][] result;     //

    // mvc* = Matriz de Vectores de Comparación
    // mmp* = Matriz de Matrices de Preferencia
    // mp** = Matriz de Prefecencia

    public Result(int[][][] attributeInfo, int[][][] profileInfo,
                  double[][][] mcaj, double[][] mvca, double[][][] mmpa, double[][] mpca,
                  double[][][] mcpj, double[][] mvcp, double[][][] mmpp, double[][] mpap,
                  double[][] result) {
        this.attributeInfo = attributeInfo;
        this.profileInfo = profileInfo;
        this.mcaj = mcaj;
        this.mvca = mvca;
        this.mmpa = mmpa;
        this.mpca = mpca;
        this.mcpj = mcpj;
        this.mvcp = mvcp;
        this.mmpp = mmpp;
        this.mpap = mpap;
        this.result = result;
    }



}
