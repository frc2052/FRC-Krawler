package com.team2052.frckrawler.comparators;

import com.team2052.frckrawler.models.Robot;

import java.util.Comparator;

public class RobotTeamNumberComparator implements Comparator<Robot> {
    @Override
    public int compare(Robot lhs, Robot rhs) {
        return Double.compare(lhs.getTeam_id(), rhs.getTeam_id());
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
