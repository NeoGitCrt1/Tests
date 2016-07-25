package practices.my.countstep.logger;

/**
 * Created by user on 2016/07/25.
 */
public class ShowTraceData {
    // Stores the beginning of the LogNode topology.
    private static LogNode mLogNode;

    /**
     * Returns the next LogNode in the linked list.
     */
    public static LogNode getLogNode() {
        return mLogNode;
    }

    /**
     * Sets the LogNode data will be sent to.
     */
    public static void setLogNode(LogNode node) {
        mLogNode = node;
    }

}
