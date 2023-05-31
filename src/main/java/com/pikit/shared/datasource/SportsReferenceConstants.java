package com.pikit.shared.datasource;

import com.google.common.collect.ImmutableMap;
import com.pikit.shared.enums.League;

public final class SportsReferenceConstants {
    public SportsReferenceConstants() {}

    private static final ImmutableMap<String, String> NFL_MAP = ImmutableMap.<String, String>builder()
            .put("NO", "NOR")
            .put("PIT", "PIT")
            .put("NE", "NWE")
            .put("WAS", "WAS")
            .put("TB", "TAM")
            .put("PHI", "PHI")
            .put("ATL", "ATL")
            .put("CLE", "CLE")
            .put("CIN", "CIN")
            .put("LAC", "SDG")
            .put("BUF", "BUF")
            .put("NYG", "NYG")
            .put("DET", "DET")
            .put("LAR", "RAM")
            .put("CAR", "CAR")
            .put("SF", "SFO")
            .put("IND", "CLT")
            .put("SEA", "SEA")
            .put("ARI", "CRD")
            .put("HOU", "HTX")
            .put("TEN", "OTI")
            .put("JAX", "JAX")
            .put("CHI", "CHI")
            .put("MIA", "MIA")
            .put("LV", "RAI")
            .put("NYJ", "NYJ")
            .put("BAL", "RAV")
            .put("KC", "KAN")
            .put("DEN", "DEN")
            .put("GB", "GNB")
            .put("MIN", "MIN")
            .put("DAL", "DAL")
            .put("STL", "RAM")
            .put("SD", "SDG")
            .put("OAK", "RAI")
            .build();

    private static final ImmutableMap<String, String> MLB_MAP = ImmutableMap.<String, String>builder()
            .put("BOS", "BOS")
            .put("NYY", "NYA")
            .put("WSH", "WAS")
            .put("PHI", "PHI")
            .put("NYM", "NYN")
            .put("MIA", "MIA")
            .put("CIN", "CIN")
            .put("STL", "SLN")
            .put("PIT", "PIT")
            .put("LAD", "LAN")
            .put("MIL", "MIL")
            .put("COL", "COL")
            .put("ATL", "ATL")
            .put("CHC", "CHN")
            .put("ARI", "ARI")
            .put("SD", "SDN")
            .put("HOU", "HOU")
            .put("SF", "SFN")
            .put("CHW", "CHA")
            .put("CLE", "CLE")
            .put("TEX", "TEX")
            .put("TOR", "TOR")
            .put("KC", "KCA")
            .put("DET", "DET")
            .put("LAA", "ANA")
            .put("MIN", "MIN")
            .put("OAK", "OAK")
            .put("SEA", "SEA")
            .put("TB", "TBA")
            .put("BAL", "BAL")
            .build();

    private static final ImmutableMap<String, String> NBA_MAP = ImmutableMap.<String, String>builder()
            .put("BOS", "BOS")
            .put("MIA", "MIA")
            .put("POR", "POR")
            .put("PHO", "PHO")
            .put("LAL", "LAL")
            .put("HOU", "HOU")
            .put("BKN", "BRK")
            .put("DET", "DET")
            .put("CLE", "CLE")
            .put("TOR", "TOR")
            .put("NY", "NYK")
            .put("PHI", "PHI")
            .put("MEM", "MEM")
            .put("ATL", "ATL")
            .put("MIN", "MIN")
            .put("SAC", "SAC")
            .put( "NO", "NOP")
            .put("MIL", "MIL")
            .put("OKC", "OKC")
            .put("CHI", "CHI")
            .put("DAL", "DAL")
            .put("CHA", "CHO")
            .put("SA", "SAS")
            .put("IND", "IND")
            .put("DEN", "DEN")
            .put("UTA", "UTA")
            .put("GS", "GSW")
            .put("LAC", "LAC")
            .put("ORL", "ORL")
            .put("WAS", "WAS")
            .build();

    public static String getSportsReferenceTeamName(League league, String teamName) {
        switch (league) {
            case NFL: return getNFLSportsReferenceTeamName(teamName);
            case MLB: return getMLBSportsReferenceTeamName(teamName);
            case NBA: return getNBASportsReferenceTeamName(teamName);
            default: throw new RuntimeException("Invalid league provided: " + league);
        }
    }

    private static String getNFLSportsReferenceTeamName(String teamName) {
        if (NFL_MAP.containsKey(teamName)) {
            return NFL_MAP.get(teamName);
        } else {
            throw new RuntimeException("Could not find NFL team name mapping: " + teamName);
        }
    }

    private static String getNBASportsReferenceTeamName(String teamName) {
        if (NBA_MAP.containsKey(teamName)) {
            return NBA_MAP.get(teamName);
        } else {
            throw new RuntimeException("Could not find NBA team name mapping: " + teamName);
        }
    }

    private static String getMLBSportsReferenceTeamName(String teamName) {
        if (MLB_MAP.containsKey(teamName)) {
            return MLB_MAP.get(teamName);
        } else {
            throw new RuntimeException("Could not find MLB team name mapping: " + teamName);
        }
    }
}
