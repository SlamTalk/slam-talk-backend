package sync.slamtalk.team.event;

import sync.slamtalk.team.entity.TeamMatching;

public record TeamMatchingSupportAcceptanceEvent(TeamMatching teamMatching, Long applicationUserId) {
}
