package com.bluesky.rabbit_habit;

import android.content.Context;
import android.util.Log;

import com.bluesky.rabbit_habit.data.Habit;
import com.bluesky.rabbit_habit.data.source.local.HabitDao;
import com.bluesky.rabbit_habit.data.source.local.RHDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public static final String TAG = "ExampleInstrumentedTest";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.bluesky.rabbit_habit", appContext.getPackageName());
    }

    @Test
    public void insertFakeDatabase() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        HabitDao dao = RHDatabase.getInstance(appContext).habitDao();
        Habit habit1 = new Habit(R.drawable.ic_menu_camera, "habit1", "habit first...");
        Habit habit2 = new Habit(R.drawable.ic_menu_gallery, "habit2", "habit second...");
        Habit habit3 = new Habit(R.drawable.ic_menu_manage, "habit3", "habit third...");
        Habit habit4 = new Habit(R.drawable.ic_menu_share, "habit4", "habit forth...");
        dao.insertHabit(habit1);
        dao.insertHabit(habit2);
        dao.insertHabit(habit3);
        dao.insertHabit(habit4);
        List<Habit> habits = dao.getHabits();
        Log.d(TAG, habits.toString());
    }
}
