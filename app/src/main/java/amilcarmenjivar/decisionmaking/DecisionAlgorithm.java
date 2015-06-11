package amilcarmenjivar.decisionmaking;


import amilcarmenjivar.decisionmaking.data.Result;

/**
 *
 * Created by Amilcar Menjivar on 27/04/2015.
 */
public class DecisionAlgorithm {

    /*
    * Para cada atributo:
    *
    * Se tienen que comparar los candiatos por parejas, sometidos por el atributo actual.
    * Para esto, se arman las parejas y cada juez emite su valoración.
    * Por fines prácticos, esta valoración se guardará como enteros (-8,8) y se traducierá posteriormente.
    *
    * */


    public static Result getResults(int candidates, int attributes,int profiles, int judges,
            int[][][] attributesData, int[][][] profilesData ) {

        int cPairs = candidates * (candidates-1)/2;
        int aPairs = attributes * (attributes-1)/2;

        // Matriz de Preferencias por Atributo (Candidatos x Atributos)
        double[][][] mcaj = new double[attributes][cPairs][judges];
        double[][] mvca = new double[attributes][cPairs];
        double[][][] mmpa = new double[attributes][candidates][candidates];
        double[][] mpca = new double[candidates][attributes];

        for (int j = 0; j < attributes; j++) {
            // Translate preferences
            double[][] preferences = translatePreferences(attributesData[j]);
            mcaj[j] = preferences;

            // Vector de comparación (cPairs x Judges)
            double[] vc = vectorComparacion(preferences, cPairs, judges);
            mvca[j] = vc;

            // Matriz de Preferencia
            double[][] mp = matrizPreferencia(candidates, vc);
            mmpa[j] = mp;

            // Vector de Preferencia
            double[] vp = vectorPreferencia(candidates, mp);
            for (int i = 0; i < vp.length; i++) {
                mpca[i][j] = vp[i];
            }
        }

        // Matriz de Preferencias por Perfil (Atributos*Perfil)
        double[][][] mcpj = new double[profiles][aPairs][judges];
        double[][] mvcp = new double[profiles][aPairs];
        double[][][] mmpp = new double[profiles][attributes][attributes];
        double[][] mpap = new double[attributes][profiles];
        for (int j = 0; j < profiles; j++) {
            // Translate preferences
            double[][] preferences = translatePreferences(profilesData[j]);
            mcpj[j] = preferences;

            // Vector de comparación
            double[] vc = vectorComparacion(preferences, aPairs, judges);
            mvcp[j] = vc;

            // Matriz de Preferencia
            double[][] mp = matrizPreferencia(attributes, vc);
            mmpp[j] = mp;

            // Vector de Preferencia
            double[] vp = vectorPreferencia(attributes, mp);
            for (int i = 0; i < vp.length; i++) {
                mpap[i][j] = vp[i];
            }
        }

        // Multiplicar (Candidatos*Atributos)x(Atributos*Perfiles)=(Candidatos*Perfiles)
        double[][] mMultiply = multiply(mpca, mpap);

        return new Result(attributesData, profilesData,
                mcaj, mvca, mmpa, mpca, mcpj, mvcp, mmpp, mpap, mMultiply);

        // 4. Presentar información
        // Gráfico de Radar (Candidatos*Atributos)
        // Gráfico de Radar (Atributos
        // Indicar el mejor candidato por cada perfil
        // Tabla (Atributos*Perfil)
        // Tabla (Candidatos*Atributos)
        // Tabla (Candiatos*Perfil)

    }

    public static final double MAX_ALLOWED_CONSISTENT_VALUE = 0.10;

    public static boolean isConsistencyAcceptable(double consistency) {
        return consistency < MAX_ALLOWED_CONSISTENT_VALUE;
    }

    public static boolean isPreferenceConsistent(int elements, double[] vector) {
        double consistency = getPreferenceConsistency(elements, vector);
        return isConsistencyAcceptable(consistency);
    }

    public static double getPreferenceConsistency(int n, double[] vector) {
        double[] v = getPreferenceConsistencyVector(n, vector);
        return calculateConsistency(n, v);
    }

    public static double calculateConsistency(int n, double[] consistencyVector) {
        double nMax = 0.0;
        for(int i = 0; i<n; i++) {
            nMax += consistencyVector[i];
        }
        double CI = (nMax - n) / (n-1);
        double RI = 1.98 * (n-2) / n;

        return CI / RI;
    }

    public static double[] getPreferenceConsistencyVector(int n, double[] preferenceVector) {
        double[][] mp = matrizPreferencia(n, preferenceVector);
        double[] vp = vectorPreferencia(n, mp);

        double[] consistencyVector = new double[n];
        for(int i = 0; i<n; i++) {
            consistencyVector[i] = 0.0;
            for(int e = 0; e<n; e++) {
                consistencyVector[i] += mp[i][e] * vp[e];
            }
        }
        return consistencyVector;
    }

    public static int[] getConsistentSuggestions(int n, double[] comparisonVector) {
        double[][] matrix = matrizPreferencia(n, comparisonVector);
        double[] vp = vectorPreferencia(n, matrix);
        double alpha = 0.4;

        // Assuming this method will only be invoked when preference vector is inconsistent
        double consistency;
        int maximumIterationsAllowed = 100;

        // Generate new preferences until we have a consistent matrix.
        do {
            // Generate next preferences
            for(int i = 0; i<n; i++) {
                for(int j = 0; j<n; j++) {
                    double value = Math.pow(matrix[i][j], alpha) * Math.pow(vp[i]/vp[j], 1-alpha);
                    value = Math.min(value, 9);
                    value = Math.max(value, 1.0/9.0);
                    matrix[i][j] = value;
                }
            }
            vp = vectorPreferencia(n, matrix);

            // Check consistency
            double[] consistencyVector = new double[n];
            for(int i = 0; i<n; i++) {
                consistencyVector[i] = 0.0;
                for(int e = 0; e<n; e++) {
                    consistencyVector[i] += matrix[i][e] * vp[e];
                }
            }
            consistency = calculateConsistency(n, consistencyVector);

        } while(maximumIterationsAllowed-- > 0 && isConsistencyAcceptable(consistency));

        // Transform the new matrix into a comparison vector.
        int pairs = n * (n-1) / 2;
        int[] suggestion = new int[pairs];
        int x = 0;
        for (int i = 0; i < n; i++) {
            for(int j = i+1; j<n; j++) {
                // Did we find a consistent matrix? if not, just use the original preference vector
                double value = maximumIterationsAllowed == 0 ? comparisonVector[x] : matrix[i][j];

                // Convert the new preferences back to integer form
                if( value < 1 ) {
                    suggestion[x] = (int) (-Math.round(1.0/value));
                    if(suggestion[x] < -9)
                        suggestion[x] = -9;
                } else {
                    suggestion[x] = (int) (Math.round(value));
                    if(suggestion[x] > 9)
                        suggestion[x] = 9;
                }
                if(suggestion[x] == -1) // Just because this is how ComboSeekBar is designed to work.
                    suggestion[x] = 1;
                x++;
            }
        }

        return suggestion;
    }



    public static double[] translatePreferences(int[] rawPreferences) {
        double[] preferences = new double[rawPreferences.length];
        for (int j = 0; j < rawPreferences.length; j++) {
            preferences[j] = translatePreference(rawPreferences[j]);
        }
        return preferences;
    }

    // Esto tiene que transformar las preferencias (candidatos por atributos) al vector resultado del atributo
    private static double[][] translatePreferences(int[][] rawPreferences){
        // Translate integer preferences to valid values.
        double[][] preferences = new double[rawPreferences.length][];
        for(int i = 0; i<rawPreferences.length; i++) {
            preferences[i] = translatePreferences(rawPreferences[i]);
        }

        return preferences;
    }

    private static double[] vectorComparacion(double[][] preferences, int pairs, int judges) {

        // Vector de Comparación
        double[] vc = new double[pairs];
        for(int i=0; i<pairs; i++){
            double x = 1.0;
            int count = 0;
            for(int j=0; j<judges; j++){
                x *= preferences[i][j];
                count++;
            }
            vc[i] = Math.pow(x,1.0/count);
        }

        return vc;
    }

    private static double[][] matrizPreferencia(int c, double[] vc) {
        // Matriz de Preferencias
        double[][] mp = new double[c][c]; // matriz de preferencias
        int x = 0;
        for (int i = 0; i < c; i++) {
            for(int j = i+1; j<c; j++) {
                // Triangulo superior (NorthEast)
                mp[i][j] = vc[x];

                // Triangulo inferior  (SouthWest)
                mp[j][i] = 1.0 / vc[x];

                x++;
            }
            // Diagonal Principal
            mp[i][i] = 1.0;
        }

        return mp;
    }

    private static double[] vectorPreferencia(int c, double[][] mp) {
        // Normalizar Matriz de Preferncias
        double[][] mpn = new double[c][c];
        for (int col = 0; col < c; col++) {
            double sum = 0.0;
            for(int row =0; row < c; row++) {
                sum += mp[row][col];
            }
            for(int row =0; row < c; row++) {
                mpn[row][col] = mp[row][col] / sum;
            }
        }

        // Vector de Preferencia resultante
        double[] vp = new double[c];
        for(int row=0; row< c; row++) {
            double sum = 0.0;
            for (int col = 0; col < c; col++) {
                sum += mpn[row][col];
            }
            vp[row] = sum / c;
        }
        return vp;
    }

    public static double translatePreference(int p) {
        int maxValue = 9;
        int minValue = -9;

        if(p>maxValue) p = maxValue;
        if(p<minValue) p = minValue;

        if(p==0) {
            return 1;
        } else if(p > 0) {
            return p;
        } else {
            return 1.0 / (Math.abs(p));
        }
    }


    public static double[][] multiply(double[][] A, double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Columns: " + aColumns + " did not match B:Rows " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < C[i].length; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }

}
