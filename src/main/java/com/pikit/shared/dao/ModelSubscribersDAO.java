package com.pikit.shared.dao;

import com.pikit.shared.exceptions.PersistenceException;

import java.util.List;

public interface ModelSubscribersDAO {

    /**
     * Given a user and a model, subscribe this user to the model.
     * @param userId User to subscribe
     * @param modelId Model to subscribe user to
     * @throws PersistenceException when we are unable to subscribe user to the model.
     */
    void subscribeUserToModel(String userId, String modelId) throws PersistenceException;

    /**
     * Given a user and model, unsubscribe this user from the model
     * @param userId User to unsubscribe
     * @param modelId Model to unsubscribe user from
     * @throws PersistenceException When we are unable to unsubscribe the user
     */
    void unsubscribeUserFromModel(String userId, String modelId) throws PersistenceException;

    /**
     * Given a user, retrieve all modelIds that the user is subscribed to
     * @param userId User to retrieve subscribed models for
     * @return List of modelIds that the user subscribes to
     * @throws PersistenceException WHen we are unable to retrieve the models subscribed to
     */
    List<String> getModelsUserSubscribesTo(String userId) throws PersistenceException;

    /**
     * Given a model, retrieve all users that are subscribed to this model
     * @param modelId Model to retrieve subscribers for
     * @return List of userIds that are subssribed to model.
     * @throws PersistenceException When we are unable to retrieve the list of users subscribed.
     */
    List<String> getSubscribersForModel(String modelId) throws PersistenceException;
}
