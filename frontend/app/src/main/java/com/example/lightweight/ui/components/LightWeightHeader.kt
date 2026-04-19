package com.example.lightweight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.lightweight.R
import com.example.lightweight.ui.theme.Background

@Composable
fun LightWeightHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(Background)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.lightweight_logo),
            contentDescription = "LightWeight Logo",
            modifier = Modifier.height(100.dp)
        )
    }
}