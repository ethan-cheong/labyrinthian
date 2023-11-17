package byow.Core;

/**
 * The Difficulty enum contains hard-coded parameters for game difficulty.
 * It is used by the Engine to determine various aspects of world generation, such as
 * the number of enemies and their parameters.
 */
public enum Difficulty {

    EASY (7, 2, 10000, 1, 10, 1),
    MEDIUM (5, 3, 100, 1, 10, 1),
    HARD (5, 4, 100, 2, 15, 1);

    Difficulty(int visionRange, int crawlerNum, int dread, int dreadDecay, int crawlerRange,
               int crawlerSpeed) {
        VISION_RANGE = visionRange;
        CRAWLER_NUM = crawlerNum;
        DREAD = dread;
        DREAD_DECAY = dreadDecay;
        CRAWLER_RANGE = crawlerRange;
        CRAWLER_SPEED = crawlerSpeed;
    }

    public final int VISION_RANGE;
    public final int CRAWLER_NUM;
    public final int DREAD;
    public final int DREAD_DECAY;
    public final int CRAWLER_RANGE;
    public final int CRAWLER_SPEED;


}
