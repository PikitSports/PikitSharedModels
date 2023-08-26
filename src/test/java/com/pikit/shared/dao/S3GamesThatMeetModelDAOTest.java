package com.pikit.shared.dao;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pikit.shared.client.S3Client;
import com.pikit.shared.dao.s3.S3GamesThatMeetModelDAO;
import com.pikit.shared.exceptions.NotFoundException;
import com.pikit.shared.exceptions.PersistenceException;
import com.pikit.shared.models.GameThatMeetsModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3GamesThatMeetModelDAOTest {
    private static final String BUCKET_NAME = "bucketName";
    private static final String MODEL_ID = "modelId";
    private static final String SEASON = "season";
    private static final String S3_KEY = "modelId/season.json";
    private static final String OTHER_S3_KEY = "modelId/2023.json";
    private static final String GAME = "game";

    @Mock
    private S3Client s3Client;

    private S3GamesThatMeetModelDAO s3GamesThatMeetModelDAO;

    @BeforeEach
    public void setup() {
        Assertions.setMaxStackTraceElementsDisplayed(100);
        s3GamesThatMeetModelDAO = new S3GamesThatMeetModelDAO(s3Client, BUCKET_NAME);
    }

    @Test
    public void addGamesThatMeetModel_successTest() throws PersistenceException, IOException {
        s3GamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, getListOfGamesThatMeetModel());
        verify(s3Client, times(2)).writeObjectToS3(eq(BUCKET_NAME), any(), any(), eq(false));
    }

    @Test
    public void addGamesThatMeetModel_IOExceptionThrown() throws IOException {
        doThrow(IOException.class).when(s3Client).writeObjectToS3(eq(BUCKET_NAME), any(), any(), eq(false));

        assertThatThrownBy(() -> s3GamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, getListOfGamesThatMeetModel()))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void addGamesThatMeetModel_S3ExceptionThrown() throws IOException {
        doThrow(SdkClientException.class).when(s3Client).writeObjectToS3(eq(BUCKET_NAME), any(), any(), eq(false));

        assertThatThrownBy(() -> s3GamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, getListOfGamesThatMeetModel()))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void addGamesThatMeetModelForSeason_successTest() throws PersistenceException, IOException {
        s3GamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, SEASON, getListOfGamesThatMeetModel().get(SEASON));

        verify(s3Client, times(1)).writeObjectToS3(eq(BUCKET_NAME), eq(S3_KEY), any(), eq(false));
    }

    @Test
    public void addGamesThatMeetModelForSeason_IOExceptionThrown() throws IOException {
        doThrow(IOException.class).when(s3Client).writeObjectToS3(eq(BUCKET_NAME), any(), any(), eq(false));

        assertThatThrownBy(() -> s3GamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, SEASON, getListOfGamesThatMeetModel().get(SEASON)))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void addGamesThatMeetModelForSeason_S3ExceptionThrown() throws IOException {
        doThrow(SdkClientException.class).when(s3Client).writeObjectToS3(eq(BUCKET_NAME), any(), any(), eq(false));

        assertThatThrownBy(() -> s3GamesThatMeetModelDAO.addGamesThatMeetModel(MODEL_ID, SEASON, getListOfGamesThatMeetModel().get(SEASON)))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void deleteGamesThatMeetModel_success() throws PersistenceException {
        s3GamesThatMeetModelDAO.deleteOldGamesThatMetModel(MODEL_ID, SEASON);
        verify(s3Client, times(1)).deleteObjectFromS3(BUCKET_NAME, S3_KEY);
    }

    @Test
    public void deleteGamesThatMeetModel_exceptionThrown() {
        doThrow(SdkClientException.class).when(s3Client).deleteObjectFromS3(BUCKET_NAME, S3_KEY);

        assertThatThrownBy(() -> s3GamesThatMeetModelDAO.deleteOldGamesThatMetModel(MODEL_ID, SEASON))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    public void getGamesThatMeetModel_success() throws IOException, PersistenceException, NotFoundException {
        List<GameThatMeetsModel> gamesThatMeetModel = new ArrayList<>();
        gamesThatMeetModel.add(GameThatMeetsModel.builder().gameId(GAME).build());

        when(s3Client.getTypeReferenceFromS3(eq(BUCKET_NAME), eq(S3_KEY), any(TypeReference.class), eq(false))).thenReturn(gamesThatMeetModel);
        List<GameThatMeetsModel> gamesThatMeetModelForSeason = s3GamesThatMeetModelDAO.getGamesThatMeetModelForSeason(MODEL_ID, SEASON);

        assertThat(gamesThatMeetModelForSeason).isNotNull();
        assertThat(gamesThatMeetModelForSeason.size()).isEqualTo(1);
        assertThat(gamesThatMeetModelForSeason.get(0).getGameId()).isEqualTo(GAME);
    }

    @Test
    public void getGamesThatMeetModel_noneExist() throws PersistenceException, IOException, NotFoundException {
        when(s3Client.getTypeReferenceFromS3(eq(BUCKET_NAME), eq(S3_KEY), any(TypeReference.class), eq(false))).thenReturn(Collections.emptyList());
        List<GameThatMeetsModel> gamesThatMeetModel = s3GamesThatMeetModelDAO.getGamesThatMeetModelForSeason(MODEL_ID, SEASON);
        assertThat(gamesThatMeetModel).isNotNull();
        assertThat(gamesThatMeetModel.size()).isEqualTo(0);
    }

    @Test
    public void getGamesThatMeetModel_s3KeyNotFoundException() throws IOException {
        AmazonS3Exception s3Exception = mock(AmazonS3Exception.class);
        when(s3Exception.getErrorCode()).thenReturn("NoSuchKey");
        doThrow(s3Exception).when(s3Client).getTypeReferenceFromS3(eq(BUCKET_NAME), eq(S3_KEY), any(TypeReference.class), eq(false));
        assertThatThrownBy(() -> s3GamesThatMeetModelDAO.getGamesThatMeetModelForSeason(MODEL_ID, SEASON))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getGamesThatMeetModel_exceptionThrown() throws IOException {
        doThrow(SdkClientException.class).when(s3Client).getTypeReferenceFromS3(eq(BUCKET_NAME), eq(S3_KEY), any(TypeReference.class), eq(false));

        assertThatThrownBy(() -> s3GamesThatMeetModelDAO.getGamesThatMeetModelForSeason(MODEL_ID, SEASON))
                .isInstanceOf(PersistenceException.class);
    }

    private TreeMap<String, List<GameThatMeetsModel>> getListOfGamesThatMeetModel() {
        TreeMap<String, List<GameThatMeetsModel>> gamesThatMeetModel = new TreeMap<>();
        List<GameThatMeetsModel> games = new ArrayList<>();
        games.add(GameThatMeetsModel.builder()
                .gameId(GAME)
                .build());
        gamesThatMeetModel.put(SEASON, games);
        gamesThatMeetModel.put("2023", games);
        return gamesThatMeetModel;
    }
}
