package sync.slamtalk.team.event;

import sync.slamtalk.team.entity.TeamMatching;

public record TeamMatchingSupportEvent(TeamMatching teamMatching, String participantNickname , Long writerUserId) {
}
