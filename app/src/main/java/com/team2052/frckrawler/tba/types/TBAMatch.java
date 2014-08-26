package com.team2052.frckrawler.tba.types;

public class TBAMatch {
    private String event_key;
    private String comp_level;
    private int match_number;
    private Alliances alliances;

    public TBAMatch() {
    }

    public String getEventKey() {
        return event_key;
    }

    public String getCompLevel() {
        return comp_level;
    }

    public int getMatchNumber() {
        return match_number;
    }

    public Alliances getAlliances() {
        return alliances;
    }

    public static class Alliances {
        private Alliance blue;
        private Alliance red;

        public Alliances() {
        }

        public Alliance getBlue() {
            return blue;
        }

        public Alliance getRed() {
            return red;
        }

        public static class Alliance {
            private int score;
            private String[] teams;

            public Alliance() {
            }

            public int getScore() {
                return score;
            }

            public int getTeam(int station) {
                if (station > 3 || teams.length < station)
                    return -1;
                try {
                    return Integer.parseInt(teams[station - 1].replace("frc", ""));
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
    }
}
