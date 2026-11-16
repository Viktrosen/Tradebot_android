package ru.bolotov.tradebot.navigation.deeplink

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.bolotov.tradebot.navigation.data.AppLink

@RunWith(AndroidJUnit4::class)
class AppLinkParserTest {
    private val parser = AppLinkParser()
    private val id = "31fcf18a-350d-443d-9d69-871d4c25a4d9"

    @Test
    fun canonicalLinkRoundTrips() {
        val expected = AppLink.PositionDetails(positionId = id)
        val intent = Intent().setData(AppLinkEncoder().encode(destination = expected))
        assertEquals(expected, parser.parse(intent = intent))
    }

    @Test
    fun foregroundBackgroundAndOldNotificationsNormalizeToOneTarget() {
        val expected = AppLink.PositionDetails(positionId = id)
        assertEquals(expected, parser.parseNotificationData(data = mapOf("positionId" to id)))
        assertEquals(expected, parser.parse(intent = Intent().putExtra("positionId", id)))
        assertEquals(
            expected,
            parser.parse(
                intent =
                    Intent()
                        .setData(Uri.parse("tradebot://notification/legacy-message"))
                        .putExtra("positionId", id)
            ),
        )
    }

    @Test
    fun malformedLinksCannotFallBackToUnrelatedExtras() {
        for (uri in
            listOf(
                "https://positions/$id",
                "tradebot://other/$id",
                "tradebot://positions/not-a-uuid",
                "tradebot://positions/$id/extra",
            )) {
            assertNull(
                parser.parse(intent = Intent().setData(Uri.parse(uri)).putExtra("positionId", id))
            )
        }
        assertNull(parser.parse(intent = Intent().putExtra("positionId", "bad")))
        assertNull(parser.parse(intent = Intent()))
    }
}
