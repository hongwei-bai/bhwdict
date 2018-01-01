package bhwWords.dict.txtparser;

public class WordProperty {
    public final static String noun = "n";
    public final static String pronoun = "pron";
    public final static String adjective = "adj";
    public final static String adverb = "adv";
    public final static String adverb_dot = "adv.";
    public final static String verb = "v";
    public final static String numeral = "num";
    public final static String article = "art";
    public final static String indefinite_article = "indef art";
    public final static String preposition = "prep";
    public final static String conjunction = "conj";
    public final static String interjection = "interj";

    public int _id;
    public String property;
    public String pronunciationRaw;
    public String pronunciation;
    public String content;
    public Hierarchy items;
    public WordIdiom idiom;
}
