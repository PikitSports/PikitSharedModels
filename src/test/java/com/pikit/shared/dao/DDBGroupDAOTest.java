package com.pikit.shared.dao;

import com.pikit.shared.dao.ddb.DDBGroupDAO;
import com.pikit.shared.dao.ddb.model.DDBGroup;
import com.pikit.shared.dynamodb.LocalDynamoDB;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

public class DDBGroupDAOTest {

    private static final String USER = "USER";
    private static final String MODEL_ID = "MODEL_ID";
    private static final String GROUP_NAME = "GROUP_NAME";
    private LocalDynamoDB localDynamoDB = new LocalDynamoDB();
    private DynamoDbTable<DDBGroup> groupsTable;
    private DynamoDbIndex<DDBGroup> userGroupsIndex;
    private DDBGroupDAO groupDAO;

    @BeforeEach
    public void setup() {
        localDynamoDB.start();

        TableSchema<DDBGroup> groupTableSchema = TableSchema.fromBean(DDBGroup.class);

        DynamoDbClient localDynamoClient = localDynamoDB.createClient();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(localDynamoClient)
                .build();

        groupsTable = spy(enhancedClient.table("Groups", groupTableSchema));

        EnhancedGlobalSecondaryIndex userIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("userGroupsIndex")
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();

        groupsTable.createTable(CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(userIndex)
                .build());

        userGroupsIndex = spy(groupsTable.index("userGroupsIndex"));

        groupDAO = new DDBGroupDAO(groupsTable, userGroupsIndex);
    }

    @Test
    public void createGroup_successTest() throws PersistenceException {
        String groupId = groupDAO.createGroup(USER, MODEL_ID, GROUP_NAME);
        DDBGroup group = groupsTable.getItem(Key.builder().partitionValue(groupId).build());
        assertThat(group).isNotNull();
        assertThat(group.getUserCreatedBy()).isEqualTo(USER);
        assertThat(group.getModelIds()).contains(MODEL_ID);
        assertThat(group.getGroupName()).isEqualTo(GROUP_NAME);
    }

    @Test
    public void createGroup_exceptionThrown() {
        doThrow(DynamoDbException.class).when(groupsTable).putItem(any(PutItemEnhancedRequest.class));
        assertThatThrownBy(() -> groupDAO.createGroup(null, null, null))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void addModelToGroup_successTest() throws PersistenceException, NotFoundException {
        String modelToAdd = "model_to_add";
        String groupId = groupDAO.createGroup(USER, MODEL_ID, GROUP_NAME);

        groupDAO.addModelToGroup(groupId, modelToAdd);

        DDBGroup group = groupsTable.getItem(Key.builder()
                .partitionValue(groupId)
                .build());

        assertThat(group.getModelIds()).contains(modelToAdd);
    }

    @Test
    public void addModelToGroup_notFound() {
        assertThatThrownBy(() -> groupDAO.addModelToGroup("unknownGroup", "unknownModel"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void addModelToGroup_exceptionThrown() throws PersistenceException {
        String modelToAdd = "model_to_add";
        String groupId = groupDAO.createGroup(USER, MODEL_ID, GROUP_NAME);

        doThrow(DynamoDbException.class).when(groupsTable).updateItem(any(UpdateItemEnhancedRequest.class));

        assertThatThrownBy(() -> groupDAO.addModelToGroup(groupId, modelToAdd))
                .isInstanceOf(PersistenceException.class);

        DDBGroup group = groupsTable.getItem(Key.builder()
                .partitionValue(groupId)
                .build());

        assertThat(group.getModelIds()).contains(MODEL_ID);
        assertThat(group.getModelIds()).doesNotContain(modelToAdd);
    }

    @Test
    public void getGroup_successTest() throws PersistenceException {
        String groupId = groupDAO.createGroup(USER, MODEL_ID, GROUP_NAME);

        Optional<DDBGroup> groupOptional = groupDAO.getGroupFromId(groupId);
        assertThat(groupOptional.isPresent()).isTrue();
        assertThat(groupOptional.get().getGroupName()).isEqualTo(GROUP_NAME);
    }

    @Test
    public void getGroup_notExists() throws PersistenceException {
        Optional<DDBGroup> groupOptional = groupDAO.getGroupFromId("unknownGroup");
        assertThat(groupOptional.isPresent()).isFalse();
    }

    @Test
    public void getGroup_exceptionThrown() throws PersistenceException {
        String groupId = groupDAO.createGroup(USER, MODEL_ID, GROUP_NAME);

        doThrow(DynamoDbException.class).when(groupsTable).getItem(GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(groupId)
                        .build())
                .consistentRead(true)
                .build());

        assertThatThrownBy(() -> groupDAO.getGroupFromId(groupId)).isInstanceOf(PersistenceException.class);
    }

    @Test
    public void getGroupsForUser_successTest() throws PersistenceException {
        String groupId1 = groupDAO.createGroup(USER, MODEL_ID, GROUP_NAME);

        String groupId2 = groupDAO.createGroup(USER, MODEL_ID, GROUP_NAME);

        //Create 3rd group not created by user just to ensure accuracy.
        groupDAO.createGroup("notUser", "newModelId", "newGroupName");

        List<DDBGroup> groupsForUser = groupDAO.getGroupsForUser(USER);
        assertThat(groupsForUser.size()).isEqualTo(2);
        assertThat(groupsForUser.get(0).getGroupId()).isEqualTo(groupId1);
        assertThat(groupsForUser.get(1).getGroupId()).isEqualTo(groupId2);
    }

    @Test
    public void getGroupsForUser_noModels() throws PersistenceException {
        List<DDBGroup> groupsForUser = groupDAO.getGroupsForUser(USER);
        assertThat(groupsForUser).isEmpty();
    }

    @Test
    public void getGroupsForUser_exceptionThrown() {
        doThrow(DynamoDbException.class).when(userGroupsIndex).query(any(QueryEnhancedRequest.class));

        assertThatThrownBy(() -> groupDAO.getGroupsForUser(USER))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void deleteGroup_successTest() throws PersistenceException {
        String groupId = groupDAO.createGroup(USER, MODEL_ID, GROUP_NAME);

        DDBGroup currentGroup = groupsTable.getItem(Key.builder()
                .partitionValue(groupId)
                .build());

        assertThat(currentGroup).isNotNull();

        groupDAO.deleteGroup(groupId);

        DDBGroup newGroup = groupsTable.getItem(Key.builder()
                .partitionValue(groupId)
                .build());

        assertThat(newGroup).isNull();
    }

    @Test
    public void deleteGroup_notExists() throws PersistenceException {
        //Doesn't throw exception
        groupDAO.deleteGroup("unkownGroup");
    }

    @Test
    public void deleteGroup_exceptionThrown() throws PersistenceException {
        String groupId = groupDAO.createGroup(USER, MODEL_ID, GROUP_NAME);

        DDBGroup currentGroup = groupsTable.getItem(Key.builder()
                .partitionValue(groupId)
                .build());

        assertThat(currentGroup).isNotNull();

        doThrow(DynamoDbException.class).when(groupsTable).deleteItem(any(DeleteItemEnhancedRequest.class));

        assertThatThrownBy(() -> groupDAO.deleteGroup(groupId))
                .isInstanceOf(PersistenceException.class);

        DDBGroup newGroup = groupsTable.getItem(Key.builder()
                .partitionValue(groupId)
                .build());

        assertThat(newGroup).isNotNull();
    }
}
