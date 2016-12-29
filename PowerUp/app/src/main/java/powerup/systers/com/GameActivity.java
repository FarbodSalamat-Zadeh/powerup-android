package powerup.systers.com;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import java.util.ArrayList;
import java.util.List;
import powerup.systers.com.datamodel.Answer;
import powerup.systers.com.datamodel.Question;
import powerup.systers.com.datamodel.Scenario;
import powerup.systers.com.datamodel.SessionHistory;
import powerup.systers.com.db.DatabaseHandler;

@SuppressLint("NewApi")
public class GameActivity extends Activity {

    public static Activity gameActivityInstance;

    private DatabaseHandler mDbHandler;
    private List<Answer> mAnswers;
    private Scenario mScene;
    private Scenario mPrevScene;
    private TextView mQuestionTextView;
    private TextView mScenarioNameTextView;
    private Button mReplay;
    private Button mGoToMap;
    private ArrayAdapter<String> mListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        setmDbHandler(new DatabaseHandler(this));
        getmDbHandler().open();

        gameActivityInstance = this;

        // Find the ListView resource.
        ListView mainListView = (ListView) findViewById(R.id.mainListView);
        mQuestionTextView = (TextView) findViewById(R.id.questionView);
        mScenarioNameTextView = (TextView) findViewById(R.id.scenarioNameEditText);
        mListAdapter = new ArrayAdapter<>(this, R.layout.simplerow, new ArrayList<String>());
        mAnswers = new ArrayList<>();
        mGoToMap = (Button) findViewById(R.id.continueButtonGoesToMap);
        mReplay = (Button) findViewById(R.id.redoButton);

        ImageView eyeImageView = (ImageView) findViewById(R.id.eyeImageView);
        ImageView faceImageView = (ImageView) findViewById(R.id.faceImageView);
        ImageView hairImageView = (ImageView) findViewById(R.id.hairImageView);
        ImageView clothImageView = (ImageView) findViewById(R.id.clothImageView);

        String eyeImageName = getResources().getString(R.string.eye);
        eyeImageName = eyeImageName + getmDbHandler().getAvatarEye();
        R.drawable ourRID = new R.drawable();
        java.lang.reflect.Field photoNameField;
        try {
            photoNameField = ourRID.getClass().getField(eyeImageName);
            eyeImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException e) {
            e.printStackTrace();
        }

        String faceImageName = getResources().getString(R.string.face);
        faceImageName = faceImageName + getmDbHandler().getAvatarFace();
        try {
            photoNameField = ourRID.getClass().getField(faceImageName);
            faceImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException e) {
            e.printStackTrace();
        }

        String clothImageName = getResources().getString(R.string.cloth);
        clothImageName = clothImageName + getmDbHandler().getAvatarCloth();
        try {
            photoNameField = ourRID.getClass().getField(clothImageName);
            clothImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException e) {
            e.printStackTrace();
        }

        String hairImageName = getResources().getString(R.string.hair);
        hairImageName = hairImageName + getmDbHandler().getAvatarHair();
        try {
            photoNameField = ourRID.getClass().getField(hairImageName);
            hairImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException e) {
            e.printStackTrace();
        }

        // Update scene
        updateScenario();
        if (mScene.getReplayed() == 1) {
            mGoToMap.setAlpha((float) 0.0);
            mReplay.setAlpha((float) 0.0);
        }

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter(mListAdapter);
        mainListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        if (mAnswers.get(position).getNextQuestionId() > 0) {
                            // Next Question
                            SessionHistory.currQId = mAnswers.get(position)
                                    .getNextQuestionId();
                            updatePoints(position);
                            updateQA();

                        } else {
                            SessionHistory.currSessionId = mScene
                                    .getNextScenarioId();
                            if (SessionHistory.currSessionId == -1) {
                                // Check to make sure all scenes are completed
                                SessionHistory.currSessionId = 1;
                            }
                            updatePoints(position);
                            getmDbHandler().setCompletedScenario(
                                    mScene.getId());
                            SessionHistory.currScenePoints = 0;
                            updateScenario();
                        }
                    }
                });

        IconRoundCornerProgressBar powerBarHealing =
                (IconRoundCornerProgressBar) findViewById(R.id.powerbarHealing);
        powerBarHealing.setIconImageResource(R.drawable.icon_healing);
        powerBarHealing.setIconBackgroundColor(R.color.powerup_purple_light);
        powerBarHealing.setProgress(mDbHandler.getHealing());

        IconRoundCornerProgressBar powerBarInvisibility =
                (IconRoundCornerProgressBar) findViewById(R.id.powerbarInvisibility);
        powerBarInvisibility.setIconImageResource(R.drawable.icon_invisibility);
        powerBarInvisibility.setProgress(mDbHandler.getInvisibility());

        IconRoundCornerProgressBar powerBarStrength =
                (IconRoundCornerProgressBar) findViewById(R.id.powerbarStrength);
        powerBarStrength.setIconImageResource(R.drawable.icon_strength);
        powerBarStrength.setProgress(mDbHandler.getStrength());

        IconRoundCornerProgressBar powerBarTelepathy =
                (IconRoundCornerProgressBar) findViewById(R.id.powerbarTelepathy);
        powerBarTelepathy.setIconImageResource(R.drawable.icon_telepathy);
        powerBarTelepathy.setProgress(mDbHandler.getTelepathy());
    }

    private void updatePoints(int position) {
        // Update the scene points
        SessionHistory.currScenePoints += mAnswers.get(position).getPoints();

        // Update total points
        SessionHistory.totalPoints += mAnswers.get(position).getPoints();
    }

    private void updateScenario() {
        if (ScenarioOverActivity.scenarioActivityDone == 1) {
            ScenarioOverActivity.scenarioOverActivityInstance.finish();
        }

        if (mScene != null) {
            mPrevScene = getmDbHandler().getScenarioFromID(mScene.getId());
        }

        mScene = getmDbHandler().getScenario();

        // Replay a scenario
        if (mScene.getReplayed() == 0) {
            // mGoToMap Mechanics
            mGoToMap.setAlpha((float) 1.0);
            mGoToMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // In case the user move back to map in between a running scenario.
                    SessionHistory.totalPoints -= SessionHistory.currScenePoints;
                    mGoToMap.setClickable(false);

                    Intent myIntent = new Intent(GameActivity.this, MapActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(myIntent, 0);

                    getmDbHandler().setReplayedScenario(mScene.getScenarioName());
                    mGoToMap.setAlpha((float) 0.0);
                    mReplay.setAlpha((float) 0.0);
                }
            });

            // Replay Mechanics
            mReplay.setAlpha((float) 1.0);
            mReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // In case the user moves back to map in between a running scenario.
                    SessionHistory.totalPoints -= SessionHistory.currScenePoints;
                    mReplay.setClickable(false);

                    Intent myIntent = new Intent(GameActivity.this, GameActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(myIntent, 0);

                    getmDbHandler().setReplayedScenario(mScene.getScenarioName());
                    mGoToMap.setAlpha((float) 0.0);
                    mReplay.setAlpha((float) 0.0);
                }
            });
        }

        // If completed check if it is last mScene
        if (mPrevScene != null && mPrevScene.getCompleted() == 1) {
            if (mScene.getNextScenarioId() == -1) {
                Intent myIntent = new Intent(GameActivity.this, GameOverActivity.class);
                finish();
                startActivityForResult(myIntent, 0);
            } else {
                SessionHistory.currSessionId = mScene.getNextScenarioId();
                Intent intent = new Intent(GameActivity.this, ScenarioOverActivity.class);
                intent.putExtra(String.valueOf(R.string.scene), mPrevScene.getScenarioName());
                startActivity(intent);
            }
        }

        SessionHistory.currQId = mScene.getFirstQuestionId();
        mScenarioNameTextView.setText(mScene.getScenarioName());
        updateQA();
    }

    private void updateQA() {
        mListAdapter.clear();
        getmDbHandler().getAllAnswer(mAnswers, SessionHistory.currQId);
        for (Answer ans : mAnswers) {
            mListAdapter.add(ans.getAnswerDescription());
        }
        Question questions = getmDbHandler().getCurrentQuestion();
        mQuestionTextView.setText(questions.getQuestionDescription());
    }

    public DatabaseHandler getmDbHandler() {
        return mDbHandler;
    }

    public void setmDbHandler(DatabaseHandler mDbHandler) {
        this.mDbHandler = mDbHandler;
    }
}
