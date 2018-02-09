package org.icpclive.events;

public interface RunInfo extends Comparable<RunInfo> {
    int getId();
    boolean isAccepted();
    boolean isJudged();
    String getResult();
    int getProblemNumber();
    int getTeamId();
    SmallTeamInfo getTeamInfoBefore();
    boolean isReallyUnknown();
    double getPercentage();

    long getTime();
    long getLastUpdateTime();

    default public int compareTo(RunInfo runInfo) {
        return Long.compare(getTime(), runInfo.getTime());
    }
}
