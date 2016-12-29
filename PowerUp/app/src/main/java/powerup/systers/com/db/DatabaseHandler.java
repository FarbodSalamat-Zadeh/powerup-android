package powerup.systers.com.db;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import java.util.List;
import powerup.systers.com.datamodel.Answer;
import powerup.systers.com.datamodel.Question;
import powerup.systers.com.datamodel.Scenario;
import powerup.systers.com.datamodel.SessionHistory;

public class DatabaseHandler extends AbstractDbAdapter {

    public DatabaseHandler(Context ctx) {
        super(ctx);
        ctx.getAssets();
    }

    public boolean gameOver() {
        String selectQuery = "SELECT  * FROM " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.ScenarioEntry.COLUMN_COMPLETED + " = 0";
        Cursor cursor = sDb.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            return false;
        }
        cursor.close();
        return true;
    }

    public void getAllAnswer(List<Answer> answers, int qId) {
        String selectQuery = "SELECT  * FROM " + PowerUpContract.AnswerEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AnswerEntry.COLUMN_QUESTION_ID + " = " + qId;
        Cursor cursor = sDb.rawQuery(selectQuery, null);
        answers.clear();
        if (cursor.moveToFirst()) {
            do {
                Answer ans = new Answer();
                ans.setAnswerId(cursor.getInt(0));
                ans.setQuestionId(cursor.getInt(1));
                ans.setAnswerDescription(cursor.getString(2));
                ans.setNextQuestionId(cursor.getInt(3));
                ans.setPoints(cursor.getInt(4));
                answers.add(ans);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public Question getCurrentQuestion() {
        String selectQuery = "SELECT  * FROM " + PowerUpContract.QuestionEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.QuestionEntry.COLUMN_QUESTION_ID + " = "
                + SessionHistory.currQId;
        Cursor cursor = sDb.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            Question question = new Question();
            question.setQuestionId(cursor.getInt(0));
            question.setScenarioId(cursor.getInt(1));
            question.setQuestionDescription(cursor.getString(2));
            return question;
        }
        cursor.close();
        return null;
    }

    public Scenario getScenario() {
        String selectQuery = "SELECT  * FROM " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.ScenarioEntry.COLUMN_ID + " = "
                + SessionHistory.currSessionId;
        Cursor cursor = sDb.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            Scenario scene = new Scenario();
            scene.setId(cursor.getInt(0));
            scene.setScenarioName(cursor.getString(1));
            scene.setTimestamp(cursor.getString(2));
            scene.setAsker(cursor.getString(3));
            scene.setAvatar(cursor.getInt(4));
            scene.setFirstQuestionId(cursor.getInt(5));
            scene.setCompleted(cursor.getInt(6));
            scene.setNextScenarioId(cursor.getInt(7));
            scene.setReplayed(cursor.getInt(8));
            return scene;
        }
        cursor.close();
        return null;
    }

    public Scenario getScenarioFromID(int id) {
        String selectQuery = "SELECT  * FROM " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.ScenarioEntry.COLUMN_ID + " = "
                + id;
        Cursor cursor = sDb.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            Scenario scene = new Scenario();
            scene.setId(cursor.getInt(0));
            scene.setScenarioName(cursor.getString(1));
            scene.setTimestamp(cursor.getString(2));
            scene.setAsker(cursor.getString(3));
            scene.setAvatar(cursor.getInt(4));
            scene.setFirstQuestionId(cursor.getInt(5));
            scene.setCompleted(cursor.getInt(6));
            scene.setNextScenarioId(cursor.getInt(7));
            scene.setReplayed(cursor.getInt(8));
            return scene;
        }
        cursor.close();
        return null;
    }

    public boolean setSessionId(CharSequence ScenarioName) {
        String selectQuery = "SELECT  * FROM " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.ScenarioEntry.COLUMN_SCENARIO_NAME + " = "
                + "\"" + ScenarioName + "\"";
        Cursor cursor = sDb.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            // If the scene is already completed
            if (cursor.getInt(6) == 1) {
                return false;
            }
            SessionHistory.currSessionId = cursor.getInt(0);
            SessionHistory.currQId = cursor.getInt(5);
            return true;
        }
        cursor.close();
        // Scenario not Found
        return false;
    }

    public void setCompletedScenario(int id) {
        String updateQuery = "UPDATE  " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " SET " + PowerUpContract.ScenarioEntry.COLUMN_COMPLETED + "=1" +
                " WHERE " + PowerUpContract.ScenarioEntry.COLUMN_ID + " = " + id;
        sDb.execSQL(updateQuery);
        String selectQuery = "SELECT  * FROM " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.ScenarioEntry.COLUMN_ID + " = "
                + id;
        Cursor cursor = sDb.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            Log.i("Completed : ", String.valueOf(cursor.getInt(6)));
        }
        cursor.close();
    }

    public void setCompletedScenario(CharSequence ScenarioName) {
        String updateQuery = "UPDATE  " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " SET " + PowerUpContract.ScenarioEntry.COLUMN_COMPLETED + "=1" +
                " WHERE " + PowerUpContract.ScenarioEntry.COLUMN_SCENARIO_NAME + " = " +
                "\"" + ScenarioName + "\"";
        sDb.execSQL(updateQuery);
    }

    public void setReplayedScenario(CharSequence ScenarioName) {
        String updateQuery = "UPDATE  " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " SET " + PowerUpContract.ScenarioEntry.COLUMN_REPLAYED + "=1" +
                " WHERE " + PowerUpContract.ScenarioEntry.COLUMN_SCENARIO_NAME + " = " +
                "\"" + ScenarioName + "\"";
        sDb.execSQL(updateQuery);
    }

    public int getAvatarFace() {
        String query = "Select * from " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(1);
        }
        cursor.close();
        return 1;
    }

    public void setAvatarFace(int face) {
        String query = "UPDATE " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " SET " + PowerUpContract.AvatarEntry.COLUMN_FACE + " = " + face +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getAvatarEye() {
        String query = "Select * from " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(4);
        }
        cursor.close();
        return 1;
    }

    public void setAvatarEye(int eye) {
        String query = "UPDATE " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " SET " + PowerUpContract.AvatarEntry.COLUMN_EYES + " = " + eye +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getAvatarCloth() {
        String query = "Select * from " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(2);
        }
        cursor.close();
        return 1;
    }

    public void setAvatarCloth(int cloth) {
        String query = "UPDATE " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " SET " + PowerUpContract.AvatarEntry.COLUMN_CLOTHES + " = " + cloth +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getAvatarHair() {
        String query = "Select * from " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(3);
        }
        cursor.close();
        return 1;
    }

    public void setAvatarHair(int hair) {
        String query = "UPDATE " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " SET " + PowerUpContract.AvatarEntry.COLUMN_HAIR + " = " + hair +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getAvatarBag() {
        String query = "Select * from " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(5);
        }
        cursor.close();
        return 0;
    }

    public void setAvatarBag(int bag) {
        String query = "UPDATE " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " SET " + PowerUpContract.AvatarEntry.COLUMN_BAG + " = " + bag +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getAvatarGlasses() {
        String query = "Select * from " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(6);
        }
        cursor.close();
        return 0;
    }

    public void setAvatarGlasses(int glasses) {
        String query = "UPDATE " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " SET " + PowerUpContract.AvatarEntry.COLUMN_GLASSES + " = " + glasses +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getAvatarHat() {
        String query = "Select * from " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(7);
        }
        cursor.close();
        return 0;
    }

    public void setAvatarHat(int hat) {
        String query = "UPDATE " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " SET " + PowerUpContract.AvatarEntry.COLUMN_HAT + " = " + hat +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getAvatarNecklace() {
        String query = "Select * from " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(8);
        }
        cursor.close();
        return 0;
    }

    public void setAvatarNecklace(int necklace) {
        String query = "UPDATE " + PowerUpContract.AvatarEntry.TABLE_NAME +
                " SET " + PowerUpContract.AvatarEntry.COLUMN_NECKLACE + " = " + necklace +
                " WHERE " + PowerUpContract.AvatarEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getHealing() {
        String query = "Select * from " + PowerUpContract.PointEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.PointEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(3);
        }
        cursor.close();
        return 1;
    }

    public void setHealing(int healing) {
        String query = "UPDATE " + PowerUpContract.PointEntry.TABLE_NAME +
                " SET " + PowerUpContract.PointEntry.COLUMN_HEALING + " = " + healing +
                " WHERE " + PowerUpContract.PointEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
        Log.i("heal", getHealing() + " ");
    }

    public int getStrength() {
        String query = "Select * from " + PowerUpContract.PointEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.PointEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(1);
        }
        cursor.close();
        return 1;
    }

    public void setStrength(int strength) {
        String query = "UPDATE " + PowerUpContract.PointEntry.TABLE_NAME +
                " SET " + PowerUpContract.PointEntry.COLUMN_STRENGTH + " = " + strength +
                " WHERE " + PowerUpContract.PointEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getTelepathy() {
        String query = "Select * from " + PowerUpContract.PointEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.PointEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(4);
        }
        cursor.close();
        return 1;
    }

    public void setTelepathy(int telepathy) {
        String query = "UPDATE " + PowerUpContract.PointEntry.TABLE_NAME +
                " SET " + PowerUpContract.PointEntry.COLUMN_TELEPATHY + " = " + telepathy +
                " WHERE " + PowerUpContract.PointEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getInvisibility() {
        String query = "Select * from " + PowerUpContract.PointEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.PointEntry.COLUMN_ID + " = 1";
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(2);
        }
        cursor.close();
        return 1;
    }

    public void setInvisibility(int invisibility) {
        String query = "UPDATE " + PowerUpContract.PointEntry.TABLE_NAME +
                " SET " + PowerUpContract.PointEntry.COLUMN_INVISIBILITY + " = " + invisibility +
                " WHERE " + PowerUpContract.PointEntry.COLUMN_ID + " = 1";
        sDb.execSQL(query);
    }

    public int getPointsClothes(int id) {
        String query = "Select * from " + PowerUpContract.ClothesEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.ClothesEntry.COLUMN_ID + " = " + id;
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(2);
        }
        cursor.close();
        return 1;
    }

    public int getPointsHair(int id) {
        String query = "Select * from " + PowerUpContract.HairEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.HairEntry.COLUMN_ID + " = " + id;
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(2);
        }
        cursor.close();
        return 1;
    }

    public int getPointsAccessories(int id) {
        String query = "Select * from " + PowerUpContract.AccessoryEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AccessoryEntry.COLUMN_ID + " = " + id;
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(2);
        }
        cursor.close();
        return 1;
    }

    public int getPurchasedClothes(int id) {
        String query = "Select * from " + PowerUpContract.ClothesEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.ClothesEntry.COLUMN_ID + " = " + id;
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(3);
        }
        cursor.close();
        return 1;
    }

    public void setPurchasedClothes(int id) {
        String query = "UPDATE " + PowerUpContract.ClothesEntry.TABLE_NAME +
                " SET " + PowerUpContract.ClothesEntry.COLUMN_PURCHASED + " = 1"  +
                " WHERE " + PowerUpContract.ClothesEntry.COLUMN_ID + " = " + id;
        sDb.execSQL(query);
    }

    public int getPurchasedHair(int id) {
        String query = "Select * from " + PowerUpContract.HairEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.HairEntry.COLUMN_ID + " = " + id;
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(3);
        }
        cursor.close();
        return 1;
    }

    public void setPurchasedHair(int id) {
        String query = "UPDATE " + PowerUpContract.HairEntry.TABLE_NAME +
                " SET " + PowerUpContract.HairEntry.COLUMN_PURCHASED + " = 1"  +
                " WHERE " + PowerUpContract.HairEntry.COLUMN_ID + " = " + id;
        sDb.execSQL(query);
    }

    public int getPurchasedAccessories(int id) {
        String query = "Select * from " + PowerUpContract.AccessoryEntry.TABLE_NAME +
                " WHERE " + PowerUpContract.AccessoryEntry.COLUMN_ID + " = " + id;
        Cursor cursor = sDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(3);
        }
        cursor.close();
        return 1;
    }

    public void setPurchasedAccessories(int id) {
        String query = "UPDATE " + PowerUpContract.AccessoryEntry.TABLE_NAME +
                " SET " + PowerUpContract.AccessoryEntry.COLUMN_PURCHASED + " = 1"  +
                " WHERE " + PowerUpContract.AccessoryEntry.COLUMN_ID + " = " + id;
        sDb.execSQL(query);
    }

    public void updateComplete()
    {
        String query = "UPDATE " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " SET " + PowerUpContract.ScenarioEntry.COLUMN_COMPLETED + " = 0";
        sDb.execSQL(query);
    }

    public void updateReplayed()
    {
        String query = "UPDATE " + PowerUpContract.ScenarioEntry.TABLE_NAME +
                " SET " + PowerUpContract.ScenarioEntry.COLUMN_REPLAYED + " = 0";
        sDb.execSQL(query);
    }
    public void resetPurchase()
    {
        String query = "UPDATE " + PowerUpContract.ClothesEntry.TABLE_NAME +
                " SET " + PowerUpContract.ClothesEntry.COLUMN_PURCHASED + " = 0";
        sDb.execSQL(query);

        String query2 = "UPDATE " + PowerUpContract.HairEntry.TABLE_NAME +
                " SET " + PowerUpContract.HairEntry.COLUMN_PURCHASED + " = 0";
        sDb.execSQL(query2);

        String query3 = "UPDATE " + PowerUpContract.AccessoryEntry.TABLE_NAME +
                " SET " + PowerUpContract.AccessoryEntry.COLUMN_PURCHASED + " = 0";
        sDb.execSQL(query3);
    }
}
