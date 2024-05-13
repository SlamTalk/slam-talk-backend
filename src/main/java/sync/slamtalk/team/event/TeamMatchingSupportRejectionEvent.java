package sync.slamtalk.team.event;

import sync.slamtalk.team.entity.TeamMatching;

public record TeamMatchingSupportRejectionEvent(TeamMatching teamMatching, Long applicationUserId) {
}
