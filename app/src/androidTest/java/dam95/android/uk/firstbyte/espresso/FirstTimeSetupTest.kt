package dam95.android.uk.firstbyte.espresso


import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.internal.inject.TargetContext
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.gui.mainactivity.HomeActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author David Mckee
 * Only tests first time setup.
 * I have a better chance winning against a lion in a gladiator arena, than getting espresso to do what I want.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class FirstTimeSetupTest {

    private val FIRST_TIME_SETUP = "FIRST_TIME_SETUP"
    private val NIGHT_MODE = "NIGHT_MODE"
    private lateinit var instrumentContext: Context
    private lateinit var preferences: SharedPreferences
    lateinit var homeActivityTestRule: ActivityTestRule<HomeActivity>

    @Rule
    fun initialise(): ActivityTestRule<HomeActivity> {
        //Make sure the next test launches first time setup
        instrumentContext = InstrumentationRegistry.getInstrumentation().targetContext
        preferences = PreferenceManager.getDefaultSharedPreferences(instrumentContext)
        preferences.edit().putBoolean(FIRST_TIME_SETUP, true).apply()
        preferences.edit().putBoolean(NIGHT_MODE, false).apply()
        homeActivityTestRule = ActivityTestRule(HomeActivity::class.java)
        return homeActivityTestRule
    }

    @After
    fun reset(){
        preferences.edit().putBoolean(FIRST_TIME_SETUP, true).apply()
        preferences.edit().putBoolean(NIGHT_MODE, false).apply()
    }


    @Test
    fun firstTimeSetupTest() {
        //Click on the spinner
        onView(withId(R.id.selectRecommendedBuildSpinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)))).atPosition(2).perform(click())
        //Check that the selected value is workstation.
        onView(withId(R.id.selectRecommendedBuildSpinner)).check(matches(withSpinnerText("Workstation")))

        //Enter into the app
        val appCompatButton = onView(
            allOf(
                withId(R.id.enterAppBtn),
                childAtPosition(
                    allOf(
                        withId(R.id.firstTimeSetupActivity),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        //Check price of entry level recommended PC to make sure it's the same price as the entry level workstation pc
        val entryLevelPCPrice = onView(
            allOf(
                withId(R.id.recommendedTierPriceLeft), withText("Price: £654.39"),
                withParent(
                    allOf(
                        withId(R.id.recommendedPCLayoutLeft),
                        withParent(withId(R.id.recycler_list))
                    )
                ),
                isDisplayed()
            )
        )
        entryLevelPCPrice.check(matches(withText("Price: £654.39")))

        //Check price of budget recommended PC to make sure it's the same price as the budget level workstation pc
        val budgetPCPrice = onView(
            allOf(
                withId(R.id.recommendedTierPriceLeft), withText("Price: £1094.19"),
                withParent(
                    allOf(
                        withId(R.id.recommendedPCLayoutLeft),
                        withParent(withId(R.id.recycler_list))
                    )
                ),
                isDisplayed()
            )
        )
        budgetPCPrice.check(matches(withText("Price: £1094.19")))

        //Check price of high end recommended PC to make sure it's the same price as the high end workstation pc
        val highEndPCPrice = onView(
            allOf(
                withId(R.id.recommendedTierPriceRight), withText("Price: £2319.79"),
                withParent(
                    allOf(
                        withId(R.id.recommendedPCLayoutRight),
                        withParent(withId(R.id.recycler_list))
                    )
                ),
                isDisplayed()
            )
        )
        highEndPCPrice.check(matches(withText("Price: £2319.79")))

        //Swipe to the bottom the of home screen
        onView(withId(R.id.recycler_list)).perform(swipeUp())

        //Check price of enthusiast level recommended PC to make sure it's the same price as the enthusiast level workstation pc
        val enthusiastPCPrice = onView(
            allOf(
                withId(R.id.recommendedTierPriceRight), withText("Price: £4881.05"),
                withParent(
                    allOf(
                        withId(R.id.recommendedPCLayoutRight),
                        withParent(withId(R.id.recycler_list))
                    )
                ),
                isDisplayed()
            )
        )
        enthusiastPCPrice.check(matches(withText("Price: £4881.05")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
