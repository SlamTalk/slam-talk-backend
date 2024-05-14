package sync.slamtalk.mate.event;

import sync.slamtalk.mate.entity.MatePost;

public record MateDeclineEvent(MatePost matePost, Long applicationUserId) {
}
