package com.touchgrass.app

import com.touchgrass.app.data.FocusPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EssentialPackagesTest {

    @Test
    fun isEssentialSystemPackage_protectsCoreServices() {
        val essentialList = listOf(
            "com.google.android.ext.services",
            "android.ext.services",
            "com.android.ext.services",
            "com.android.systemui",
            "android",
            "com.google.android.gms",
            "com.google.android.gsf",
            "com.android.permissioncontroller",
            "com.google.android.permissioncontroller",
            "com.android.packageinstaller",
            "com.google.android.packageinstaller",
            "com.google.android.inputmethod.latin",
            "com.samsung.android.honeyboard",
            "com.touchtype.swiftkey",
            "com.android.phone",
            "com.android.dialer",
            "com.google.android.dialer",
            "com.android.emergency",
            "com.google.android.emergency"
        )

        for (pkg in essentialList) {
            assertTrue("Expected $pkg to be marked as essential system package", FocusPreferences.isEssentialSystemPackage(pkg))
        }
    }

    @Test
    fun isEssentialSystemPackage_doesNotExemptDistractingApps() {
        val distractingApps = listOf(
            "com.instagram.android",
            "com.google.android.youtube",
            "com.zhiliaoapp.musically", // TikTok
            "com.twitter.android",
            "com.reddit.frontpage",
            "com.facebook.katana",
            "com.snapchat.android",
            "com.supercell.clashofclans"
        )

        for (pkg in distractingApps) {
            assertFalse("Expected $pkg to NOT be marked as essential", FocusPreferences.isEssentialSystemPackage(pkg))
        }
    }
}

