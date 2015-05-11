package amilcarmenjivar.decisionmaking;

import java.util.List;

/**
* Created by Amilcar Menjivar on 11/05/2015.
*/
public interface ResultProvider {

    public List<String> getCriteriaForPage(int page);

    public List<String> getElementsForPage(int page);

    public double[][] getDataForPage(int page);

}
