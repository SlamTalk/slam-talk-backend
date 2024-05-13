package sync.slamtalk.mate.event;

import sync.slamtalk.mate.entity.MatePost;

public record MateSupportAcceptanceEvent(MatePost matePost, Long applicationUserId) {
}
