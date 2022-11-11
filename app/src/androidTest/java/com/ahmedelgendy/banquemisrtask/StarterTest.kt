package com.ahmedelgendy.banquemisrtask


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ahmedelgendy.banquemisrtask.activities.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class StarterTest {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Before
    fun intentsInit() {
        // initialize Espresso Intents capturing
        Intents.init()
    }

    @After
    fun intentsTeardown() {
        // release Espresso Intents capturing
        Intents.release()
    }


    @Test
    fun checkIfTextFiledOnlyAcceptsNumbers() {
        // Type text and then press the button.
        onView(withId(R.id.fromTextField))
            .perform(typeText("test text"))


        // Check that the text was changed.
        onView(withId(R.id.fromTextField)).check(matches(withText("1.00")))


        onView(withId(R.id.toTextField))
            .perform(typeText("test text"))


        // Check that the text was changed.
        onView(withId(R.id.toTextField)).check(matches(withText("1.00")))

    }

    @Test
    fun testNavigation() {

        onView(withId(R.id.detailsBtn))
            .perform(click())

        TestScope().launch {
            delay(1000)
            onView(withId(R.id.historyRootView))
                .check(matches(isDisplayed()))
        }

    }

}