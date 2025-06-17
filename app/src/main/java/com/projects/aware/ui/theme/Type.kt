package com.projects.aware.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.projects.aware.R


val montserrat = FontFamily(
    Font(resId = R.font.montserrat_semibold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
    Font(resId = R.font.montserrat_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(resId = R.font.montserrat_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
)

val AD_Lam = FontFamily(
    Font(resId = R.font.adlam_display_regular, weight = FontWeight.Bold, style = FontStyle.Normal),
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AD_Lam,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    displayMedium = TextStyle(
        fontFamily = AD_Lam,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    displaySmall = TextStyle(
        fontFamily = AD_Lam,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    titleLarge = TextStyle(
        fontFamily = montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    titleSmall = TextStyle(
        fontFamily = montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
)
