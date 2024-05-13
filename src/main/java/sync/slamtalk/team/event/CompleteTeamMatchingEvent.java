package sync.slamtalk.team.event;

import sync.slamtalk.team.entity.TeamMatching;

import java.util.Set;

public record CompleteTeamMatchingEvent(TeamMatching teamMatching, Set<Long> participantUserIds) {
}
