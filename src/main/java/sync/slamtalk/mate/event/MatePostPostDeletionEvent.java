package sync.slamtalk.mate.event;

import sync.slamtalk.mate.entity.MatePost;

import java.util.Set;

public record MatePostPostDeletionEvent(MatePost matePost, Set<Long> participantUserIds) {
}
