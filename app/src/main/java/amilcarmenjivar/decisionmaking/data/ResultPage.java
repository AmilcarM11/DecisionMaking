package amilcarmenjivar.decisionmaking.data;

import amilcarmenjivar.decisionmaking.R;

/**
* Created by Amilcar Menjivar on 11/06/2015.
*/
public enum ResultPage {

    CANDIDATES_PER_PROFILE      (R.string.candidates_per_profile),
    ATTRIBUTES_PER_PROFILE      (R.string.attributes_per_profile),
    CANDIDATES_PER_ATTRIBUTE    (R.string.candidates_per_attribute),
    ATTRIBUTES_PER_CANDIDATE    (R.string.attributes_per_candidate),
    PROFILES_PER_CANDIDATE      (R.string.profiles_per_candidate),
    PROFILES_PER_ATTRIBUTE      (R.string.profiles_per_attribute);

    public final int stringRes;
    ResultPage(int stringRes){
        this.stringRes = stringRes;
    }

}
