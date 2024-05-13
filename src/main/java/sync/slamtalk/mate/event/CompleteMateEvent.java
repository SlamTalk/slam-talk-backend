package sync.slamtalk.mate.event;

import sync.slamtalk.mate.entity.MatePost;

import java.util.Set;

public record CompleteMateEvent(MatePost matePost, Set<Long> userIds) {
}
