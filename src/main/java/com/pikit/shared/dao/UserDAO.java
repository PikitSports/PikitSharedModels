package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.model.DDBUser;
import com.pikit.shared.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    /**
     * Given user object, create the user and save them.
     * @param user User to create
     * @throws PersistenceException when we are unable to create the user
     */
    void createUser(DDBUser user) throws PersistenceException;

    /**
     * Given a user with updated information, update the user
     * @param user User to update
     * @throws PersistenceException when we are unable to update the user
     */
    void updateUser(DDBUser user) throws PersistenceException;

    /**
     * Given a user, retrieve all information for that user
     * @param userId User to retrieve information for
     * @return All user information stored
     * @throws PersistenceException When we are unable to retrieve user
     */
    Optional<DDBUser> getUser(String userId) throws PersistenceException;

    /**
     * Retrieve all users stored
     * @return List of userIds of every user stored
     * @throws PersistenceException When we are unable to retrieve all users.
     * Note that this is VERY inefficient but need to figure out a way to get around
     * sending emails to every user.
     */
    List<String> getAllUsers() throws PersistenceException;
}
