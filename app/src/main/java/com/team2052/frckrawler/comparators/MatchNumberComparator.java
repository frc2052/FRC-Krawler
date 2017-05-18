package com.team2052.frckrawler.comparators;

import com.team2052.frckrawler.models.Match;

import java.util.Comparator;

public class MatchNumberComparator implements Comparator<Match> {
    @Override
    public int compare(Match lhs, Match rhs) {
        return Double.compare(lhs.getMatch_number(), rhs.getMatch_number());
    }
}
