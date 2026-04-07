package com.example.a519lablearnandroid

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class MyGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceContent()
        }
    }

    @Composable
    private fun GlanceContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            Text(
                text = "Hello from Glance!",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color(0xFF6200EE))
                )
            )
            Row(modifier = GlanceModifier.padding(top = 4.dp)) {
                Text(text = "Mission 10", style = TextStyle(color = ColorProvider(Color.Gray)))
                Spacer(modifier = GlanceModifier.width(4.dp))
                Text(text = "✅ Functional", style = TextStyle(color = ColorProvider(Color(0xFF388E3C))))
            }
        }
    }
}
