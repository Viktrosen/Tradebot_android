package ru.bolotov.tradebot.notifications

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.bolotov.tradebot.navigation.data.AppLink
import ru.bolotov.tradebot.navigation.deeplink.AppLinkEncoder

@RunWith(AndroidJUnit4::class)
class NotificationPendingIntentFactoryTest {
    @Test
    fun notificationsHaveIndependentImmutableActivityIntents() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val factory = NotificationPendingIntentFactory(encoder = AppLinkEncoder())
        val target = AppLink.PositionDetails(positionId = "31fcf18a-350d-443d-9d69-871d4c25a4d9")
        val first = factory.create(context = context, destination = target, notificationId = 771)
        val second = factory.create(context = context, destination = target, notificationId = 772)
        try {
            assertNotEquals(first, second)
            assertTrue(first.isActivity)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) assertTrue(first.isImmutable)
        } finally {
            first.cancel()
            second.cancel()
        }
    }
}
