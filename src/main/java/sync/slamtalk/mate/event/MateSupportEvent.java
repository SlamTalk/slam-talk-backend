package sync.slamtalk.mate.event;

import sync.slamtalk.mate.entity.MatePost;

public record MateSupportEvent(MatePost matePost, String participantNickname , Long writerUserId) {
}
