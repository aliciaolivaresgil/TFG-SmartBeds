package com.example.smartbeds.generalActivities;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.smartbeds.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class navigationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void navigationTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.input_nombre),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("admin"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.input_nombre), withText("admin"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(pressImeActionButton());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.input_contrasena),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("admin"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.input_contrasena), withText("admin"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText4.perform(pressImeActionButton());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.boton_iniciarSesion), withText("Iniciar Sesión"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.widget.CardView")),
                                        0),
                                5),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withText("Administración"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.admin_toolbar),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("Administración")));

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.image_menu),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.admin_toolbar),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.text_username), withText("admin"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_header_container),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("admin")));

        pressBack();

        ViewInteraction appCompatButton2 = onView(
                allOf(withText("Gestión de Usuarios"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                1)));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction textView3 = onView(
                allOf(withText("Gestión de usuarios"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.users_management_toolbar),
                                        0),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("Gestión de usuarios")));

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.image_menu),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.users_management_toolbar),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navigation_view),
                                        0)),
                        3),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withText("Gestión de camas"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.beds_management_toolbar),
                                        0),
                                1),
                        isDisplayed()));
        textView4.check(matches(withText("Gestión de camas")));

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.image_menu),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.beds_management_toolbar),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView3.perform(click());

        ViewInteraction navigationMenuItemView2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navigation_view),
                                        0)),
                        4),
                        isDisplayed()));
        navigationMenuItemView2.perform(click());

        DataInteraction cardView = onData(anything())
                .inAdapterView(allOf(withId(R.id.beds_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                2)))
                .atPosition(0);
        cardView.perform(scrollTo(), click());

        ViewInteraction appCompatImageView4 = onView(
                allOf(withId(R.id.image_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bed_charts_toolbar),
                                        0),
                                0)));
        appCompatImageView4.perform(scrollTo(), click());

        ViewInteraction appCompatImageView5 = onView(
                allOf(withId(R.id.image_menu),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.beds_toolbar),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView5.perform(click());

        ViewInteraction navigationMenuItemView3 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navigation_view),
                                        0)),
                        5),
                        isDisplayed()));
        navigationMenuItemView3.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withText("Acerca de SmartBeds"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.user_add_toolbar),
                                        0),
                                1),
                        isDisplayed()));
        textView5.check(matches(withText("Acerca de SmartBeds")));

        ViewInteraction appCompatImageView6 = onView(
                allOf(withId(R.id.image_back),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.user_add_toolbar),
                                        0),
                                0)));
        appCompatImageView6.perform(scrollTo(), click());

        pressBack();

        pressBack();

        pressBack();

        pressBack();

        pressBack();

        pressBack();

        ViewInteraction appCompatImageView7 = onView(
                allOf(withId(R.id.image_menu),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.admin_toolbar),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView7.perform(click());

        ViewInteraction navigationMenuItemView4 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navigation_view),
                                        0)),
                        3),
                        isDisplayed()));
        navigationMenuItemView4.perform(click());

        ViewInteraction textView6 = onView(
                allOf(withText("Inicia sesión"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView6.check(matches(withText("Inicia sesión")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
