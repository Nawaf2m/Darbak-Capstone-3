package com.example.tuwaiqcapstone3.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FootballResponseDTO {

    private Integer results;
    private JsonNode errors;
    private PagingDTO paging;
    private List<MatchDTO> response;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PagingDTO {

        private Integer current;
        private Integer total;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MatchDTO {

        private FixtureDTO fixture;
        private LeagueDTO league;
        private TeamsDTO teams;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureDTO {

        private Integer id;
        private OffsetDateTime date;
        private VenueDTO venue;
        private StatusDTO status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VenueDTO {

        private Integer id;
        private String name;
        private String city;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusDTO {

        @JsonProperty("short")
        private String shortStatus;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueDTO {

        private Integer id;
        private Integer season;
        private String round;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamsDTO {

        private TeamDTO home;
        private TeamDTO away;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamDTO {

        private Integer id;
        private String name;
    }
}