package powerup.systers.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import powerup.systers.com.datamodel.SessionHistory;
import powerup.systers.com.db.DatabaseHandler;

public class ScenarioOverActivity extends AppCompatActivity {

    public static Activity scenarioOverActivityInstance;
    public static int scenarioActivityDone;

    private DatabaseHandler mDbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario_over);

        mDbHandler = new DatabaseHandler(this);
        mDbHandler.open();

        scenarioOverActivityInstance = this;
        scenarioActivityDone = 1;

        ImageButton continueButton = (ImageButton) findViewById(R.id.continueButton);
        Button homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ScenarioOverActivity.this, StartActivity.class));
            }
        });
        TextView scenarioTextView = (TextView) findViewById(R.id.scenarioTextView);
        TextView karmaPoints = (TextView) findViewById(R.id.karmaPoints);
        ImageView eyeImageView = (ImageView) findViewById(R.id.eyeView);
        ImageView faceImageView = (ImageView) findViewById(R.id.faceView);
        ImageView hairImageView = (ImageView) findViewById(R.id.hairView);
        ImageView clothImageView = (ImageView) findViewById(R.id.clothView);

        String eyeImageName = getResources().getString(R.string.eye);
        eyeImageName = eyeImageName + mDbHandler.getAvatarEye();
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
        faceImageName = faceImageName + mDbHandler.getAvatarFace();
        try {
            photoNameField = ourRID.getClass().getField(faceImageName);
            faceImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException e) {
            e.printStackTrace();
        }

        String clothImageName = getResources().getString(R.string.cloth);
        clothImageName = clothImageName + mDbHandler.getAvatarCloth();
        try {
            photoNameField = ourRID.getClass().getField(clothImageName);
            clothImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException e) {
            e.printStackTrace();
        }

        String hairImageName = getResources().getString(R.string.hair);
        hairImageName = hairImageName + mDbHandler.getAvatarHair();
        try {
            photoNameField = ourRID.getClass().getField(hairImageName);
            hairImageView.setImageResource(photoNameField.getInt(ourRID));
        } catch (NoSuchFieldException | IllegalAccessException
                | IllegalArgumentException e) {
            e.printStackTrace();
        }

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

        scenarioTextView.setText("Current Scene: " + getIntent().getExtras().getString(String.valueOf(R.string.scene)));
        karmaPoints.setText(String.valueOf(SessionHistory.totalPoints));
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.gameActivityInstance.finish();
                startActivity(new Intent(ScenarioOverActivity.this, GameActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GameActivity.gameActivityInstance.finish();
    }
}
