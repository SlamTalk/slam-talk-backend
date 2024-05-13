package sync.slamtalk.mate.service;

import sync.slamtalk.mate.entity.MatePost;

public record MateDeclineEvent(MatePost matePost, Long applicationUserId) {
}
