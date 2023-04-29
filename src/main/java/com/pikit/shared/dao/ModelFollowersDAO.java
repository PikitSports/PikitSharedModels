package com.pikit.shared.dao;

import com.pikit.shared.exceptions.PersistenceException;

import java.util.List;

public interface ModelFollowersDAO {

    /**
     * Given a user and a model, add this model to the users following list.
     * @param userId User to follow
     * @param modelId Model to follow
     * @throws PersistenceException when we are unable to add model to the users follow list
     */
    void followModel(String userId, String modelId) throws PersistenceException;

    /**
     * Given a user and model, unfollow this model for the user
     * @param userId User to unfollow
     * @param modelId Model to unfollow
     * @throws PersistenceException When we are unable to unfollow the model
     */
    void unfollowModel(String userId, String modelId) throws PersistenceException;

    /**
     * Given a user, retrieve all modelIds that the user follows
     * @param userId User to retrieve followed models for
     * @return List of modelIds that the user follows
     * @throws PersistenceException WHen we are unable to retrieve the models followed
     */
    List<String> getModelsUserFollows(String userId) throws PersistenceException;

    /**
     * Given a model, retrieve all users that follow this model
     * @param modelId Model to retrieve followers for
     * @return List of userIds that follow this model
     * @throws PersistenceException When we are unable to retrieve the list of users following.
     */
    List<String> getFollowersForModel(String modelId) throws PersistenceException;
}
