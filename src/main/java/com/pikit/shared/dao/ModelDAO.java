package com.pikit.shared.dao;

import com.pikit.shared.enums.League;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.ModelConfiguration;
import com.pikit.shared.models.ModelPerformance;
import com.pikit.shared.models.TopGameData;
import com.pikit.shared.dao.ddb.model.DDBModel;

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
     * Given model, and all data associated with it, update the model object after the latest run.
     * @param modelId ID of the model to update
     * @param modelPerformance Performance of the model
     */
    void updateModelAfterModelRun(String modelId, ModelPerformance modelPerformance) throws PersistenceException, NotFoundException;

    /**
     * Given a modelId, delete the model. Note that this doesn't delete all data we store for the model. It just deletes it from the Models table.
     * @param modelId ID of the model to be deleted
     * @throws PersistenceException
     */
    void deleteModel(String modelId) throws PersistenceException;
}
