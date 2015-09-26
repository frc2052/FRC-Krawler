package com.team2052.frckrawler.comparators;

import com.team2052.frckrawler.db.Robot;

import java.util.Comparator;

/**
 * Created by Adam on 9/25/2015.
 */
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
