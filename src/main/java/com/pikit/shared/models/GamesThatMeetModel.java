package com.pikit.shared.models;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GamesThatMeetModel {
    private List<GameThatMeetsModel> games;
}
