package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.model.ModelStatus;
import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.ModelConfiguration;
import com.pikit.shared.models.ModelPerformance;
import com.pikit.shared.dao.ddb.model.DDBModel;
import com.pikit.shared.models.ModelProfitabilityStats;

import java.util.List;
import java.util.Optional;

public interface ModelDAO {

    /**
     * Given a user and model configuration, create and store the model.
     * @param userId User that is creating the model
     * @param modelConfiguration Configuration of the model
     * @return ID of the model that is created
     * @throws PersistenceException
     */
    String createModel(String userId, ModelConfiguration modelConfiguration) throws PersistenceException;

    /**
     * Given a model and a new model configuration, update the model configuration
     * @param modelId ID of the model to be updated
     * @param modelConfiguration New model configuration to be stored
     * @throws PersistenceException
     */
    void updateModelConfiguration(String modelId, ModelConfiguration modelConfiguration) throws PersistenceException, NotFoundException;

    /**
     * Given a modelId, retrieve the model
     * @param modelId ID of the model to retrieve
     * @return Model object
     * @throws PersistenceException
     */
    Optional<DDBModel> getModelFromId(String modelId) throws PersistenceException;

    /**
     * Given a user, retrieve all models created by that user
     * @param user User to retrieve models for
     * @return List of models that this user created
     * @throws PersistenceException
     */
    List<DDBModel> getModelsForUser(String user) throws PersistenceException;

    /**
     * Given model, and all data associated with it, update the model object after the latest run.
     * @param modelId ID of the model to update
     * @param modelPerformance Performance of the model
     */
    void updateModelAfterModelRun(String modelId, ModelPerformance modelPerformance, ModelProfitabilityStats modelProfitabilityStats)
            throws PersistenceException, NotFoundException;

    /**
     * Given a modelId, delete the model. Note that this doesn't delete all data we store for the model. It just deletes it from the Models table.
     * @param modelId ID of the model to be deleted
     * @throws PersistenceException
     */
    void deleteModel(String modelId) throws PersistenceException;

    /**
     * Given a modelId, retrieve the model status. I.e. whether the model is currently processing, failed processing or is completed.
     * @param modelId Model to get the status for
     * @return Status of the model
     * @throws PersistenceException
     */
    ModelStatus getModelStatus(String modelId) throws PersistenceException, NotFoundException;

    /**
     * This method is used when a model workflow run has been kicked off / updated / completed and we want to update the
     * data to reflect this status.
     * @param modelId ID of the model to update
     * @param modelStatus Status of the model
     * @param workflowExecutionId Current ID of the execution running for the model.
     */
    void updateModelRunInformation(String modelId, ModelStatus modelStatus, String workflowExecutionId) throws PersistenceException, NotFoundException;

    /**
     * Given a league, retrieve all models that exist for this league. NOTE THAT THIS SHOULD BE UPDATED TO USE PAGINATION
     * @param league League to retrieve models for
     * @return List of all models that belong to this league
     * @throws PersistenceException when we are unable to retrieve models for league.
     */
    List<DDBModel> getModelsForLeague(League league) throws PersistenceException;

    /**
     * Given a league, retrieve the most recently created models that exist for this league.
     * @param league League to retreive models for
     * @param pageSize Number of models to return
     * @return List of pageSize number of models for the league
     * @throws PersistenceException When we are unable to retrieve this list of models.
     */
    List<DDBModel> getRecentModelsForLeague(League league, int pageSize) throws PersistenceException;

    /**
     * Given a league, retrieve the top {pageSize} models over last 10 games
     * @param league League models are for
     * @param pageSize how many models to return
     * @return Top {pageSize} models over the last 10 games.
     * @throws PersistenceException when we are unable to retrieve the top models.
     */
    List<DDBModel> getTopModelsFromLast10Games(League league, int pageSize) throws PersistenceException;

    /**
     * Given a league, retrieve the top {pageSize} models over last 50 games
     * @param league League models are for
     * @param pageSize how many models to return
     * @return Top {pageSize} models over the last 50 games.
     * @throws PersistenceException when we are unable to retrieve the top models.
     */
    List<DDBModel> getTopModelsFromLast50Games(League league, int pageSize) throws PersistenceException;

    /**
     * Given a league, retrieve the top {pageSize} models over last 100 games
     * @param league League models are for
     * @param pageSize how many models to return
     * @return Top {pageSize} models over the last 100 games.
     * @throws PersistenceException when we are unable to retrieve the top models.
     */
    List<DDBModel> getTopModelsFromLast100Games(League league, int pageSize) throws PersistenceException;
}
