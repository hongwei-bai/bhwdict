package bhwWords.test;

import java.util.ArrayList;
import java.util.Random;

public class RandomRules extends Rules {
    public RandomRules() {
    }

    public RandomRules(int passOrFailRule) {
        super(passOrFailRule);
    }

    @Override
    protected ArrayList<WordData> doRules(ArrayList<WordData> orgList) {
        ArrayList<WordData> resultList = new ArrayList<>();
        Random random = new Random();
        int nextPos = 0;
        int remain;
        while (!orgList.isEmpty()) {
            remain = orgList.size();
            nextPos = random.nextInt(remain);
            resultList.add(orgList.get(nextPos));
            orgList.remove(nextPos);
        }
        return resultList;
    }

    @Override
    public String getType() {
        return "r";
    }

}
