package bhwWords.test;

import java.util.ArrayList;

public class NormalRules extends Rules {

    public NormalRules() {
    }

    public NormalRules(int passOrFailRule) {
        super(passOrFailRule);
    }

    @Override
    protected ArrayList<WordData> doRules(ArrayList<WordData> orgList) {
        return orgList;
    }

    @Override
    public String getType() {
        return "n";
    }

}
