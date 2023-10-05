package com.pikit.shared.dao;

import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.activity.ActivityData;
import com.pikit.shared.models.activity.ActivityFeed;
import com.pikit.shared.models.activity.ActivityType;
import com.pikit.shared.models.activity.LastActivitySeen;

import java.util.Optional;

public interface ActivityFeedDAO {

    /**
     * Given an activity and user, save this activity to the users' activity feed.
     * @param activityId ID of the activity.
     * @param user User to save activity to their feed
     * @param activityData Data for the activity.
     */
    void saveActivity(String activityId, String user, ActivityType activityType, ActivityData activityData) throws PersistenceException;

    /**
     * Given a user, retrieve a paginated list of activities
     * @param user User to retrieve activities for
     * @param pageSize Number of activities to retrieve from the feed.
     * @param lastActivitySeenOptional Optional field to indicate where to start pagination from.
     * @return List of activities from the user feed.
     */
    ActivityFeed getActivityFeed(String user, int pageSize, Optional<LastActivitySeen> lastActivitySeenOptional) throws PersistenceException;
}
