package com.chessgrinder.chessgrinder.enums;

/**
 * Represents the status of a tournament event schedule.
 */
public enum TournamentEventScheduleStatus {
    /**
     * The schedule is active and events will be created according to it.
     */
    ACTIVE,

    /**
     * The schedule is paused and no new events will be created until it is activated again.
     */
    PAUSED,

    /**
     * The schedule is archived and will not be used anymore.
     */
    ARCHIVED
}