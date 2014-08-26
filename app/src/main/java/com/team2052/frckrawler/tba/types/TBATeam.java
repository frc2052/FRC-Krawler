package com.team2052.frckrawler.tba.types;

public class TBATeam {

    private String team_number;
    private String nickname;
    private String website;
    private String locality;
    private String region;

    public TBATeam() {
    }

    public String getNumber() {
        return team_number;
    }

    public String getName() {
        return nickname;
    }

    public String getWebsite() {
        return website;
    }

    public String getCity() {
        return locality;
    }

    public String getState() {
        return region;
    }

    @Override
    public String toString() {
        return nickname;
    }
}
