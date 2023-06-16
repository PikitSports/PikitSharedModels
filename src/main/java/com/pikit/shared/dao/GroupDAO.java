package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.model.DDBGroup;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

public interface GroupDAO {

    /**
     * Given a user, a model, and group name, create and store the group.
     * @param userId User that is creating the model
     * @param modelId ID of the model being added to the group
     * @param groupName name of the group
     * @return ID of the model that is created
     * @throws PersistenceException
     */
    String createGroup(String userId, String modelId, String groupName) throws PersistenceException;

    /**
     * Given a groupId and a modelId, add the model to the group
     * @param groupId ID of the group to be updated
     * @param modelId ID of the  model to be added
     * @throws PersistenceException
     */
    void addModelToGroup(String groupId, String modelId) throws PersistenceException, NotFoundException;

    /**
     * Given a groupId, retrieve the group
     * @param groupId ID of the group to retrieve
     * @return Group object
     * @throws PersistenceException
     */
    Optional<DDBGroup> getGroupFromId(String groupId) throws PersistenceException;

    /**
     * Given a user, retrieve all groups created by that user
     * @param userId User ID to retrieve models for
     * @return List of groups that this user created
     * @throws PersistenceException
     */
    List<DDBGroup> getGroupsForUser(String userId) throws PersistenceException;

    /**
     * Given a groupId, delete the group. Note that this doesn't delete all data we store for the group. It just deletes it from the Groups table.
     * @param groupId ID of the group to be deleted
     * @throws PersistenceException
     */
    void deleteGroup(String groupId) throws PersistenceException;

}
