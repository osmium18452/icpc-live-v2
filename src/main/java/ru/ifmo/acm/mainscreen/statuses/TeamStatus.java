package ru.ifmo.acm.mainscreen.statuses;

import ru.ifmo.acm.events.ContestInfo;
import ru.ifmo.acm.events.EventsLoader;
import ru.ifmo.acm.events.PCMS.PCMSEventsLoader;
import ru.ifmo.acm.events.TeamInfo;

import java.util.Arrays;
import ru.ifmo.acm.datapassing.Data;
import ru.ifmo.acm.datapassing.TeamData;

public class TeamStatus {
    public final ContestInfo info;
    public final String[] teamNames;
    private long changeTime;

    public TeamStatus(long changeTime) {
        EventsLoader loader = PCMSEventsLoader.getInstance();
        info = loader.getContestData();
        TeamInfo[] teamInfos = info.getStandings();
        teamNames = new String[teamInfos.length];
        for (int i = 0; i < teamNames.length; i++) {
            teamNames[i] = teamInfos[i].getShortName();
        }
        Arrays.sort(teamNames);
        this.changeTime = changeTime;
    }

    public void recache() {
        Data.cache.refresh(TeamData.class);
    }

    public synchronized boolean setInfoVisible(boolean visible, String type, String teamName) {
        if (infoTeam != null && (infoTeam.getShortName().equals(teamName) || (infoTimestamp + changeTime > System.currentTimeMillis()) && isInfoVisible)) {
            return false;
        }
        infoTimestamp = System.currentTimeMillis();
        isInfoVisible = visible;
        infoType = type;
        infoTeam = info.getParticipant(teamName);

        recache();
        return true;
    }

    public synchronized boolean isVisible() {
        return isInfoVisible;
    }

    public synchronized TeamInfo getTeam() {
        return infoTeam;
    }

    public synchronized String infoStatus() {
        return infoTimestamp + "\n" + isInfoVisible + "\n" + infoType + "\n" + (infoTeam == null ? null : infoTeam.getName());
    }

    public synchronized void initialize(TeamData data) {
        data.timestamp = infoTimestamp;
        data.isTeamVisible = isInfoVisible;
        data.infoType = infoType;
        data.teamId = infoTeam == null ? -1 : infoTeam.getId();
    }

    private long infoTimestamp;
    private boolean isInfoVisible;
    private String infoType;
    private TeamInfo infoTeam;
}
