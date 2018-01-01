package bhwWords.input;

public class ItemPresenter {
    public static float getImportanceAlpha(int importance) {
        switch (importance) {
        case 5:
            return 1;
        case 4:
            return 0.8F;
        case 3:
            return 0.6F;
        case 2:
            return 0.4F;
        case 1:
            return 0.2F;
        case 0:
        default:
            return 0.8F;
        }
    }
}
