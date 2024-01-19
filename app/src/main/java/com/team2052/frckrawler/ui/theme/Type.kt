package com.team2052.frckrawler.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.team2052.frckrawler.R

private val light = Font(R.font.roboto_light, FontWeight.W300)
private val regular = Font(R.font.roboto_regular, FontWeight.W400)
private val medium = Font(R.font.roboto_medium, FontWeight.W500)
private val bold = Font(R.font.roboto_bold, FontWeight.W700)

private val frcFontFamily = FontFamily(fonts = listOf(light, regular, medium, bold))

val frcTypography = Typography(
  h1 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W300,
    fontSize = 96.sp
  ),
  h2 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W400,
    fontSize = 60.sp
  ),
  h3 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W700,
    fontSize = 48.sp
  ),
  h4 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W700,
    fontSize = 34.sp
  ),
  h5 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W700,
    fontSize = 24.sp
  ),
  h6 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W400,
    fontSize = 20.sp
  ),
  subtitle1 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W500,
    fontSize = 16.sp
  ),
  subtitle2 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W700,
    fontSize = 14.sp
  ),
  body1 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W700,
    fontSize = 16.sp
  ),
  body2 = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W400,
    fontSize = 14.sp
  ),
  button = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W700,
    fontSize = 14.sp
  ),
  caption = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W500,
    fontSize = 12.sp
  ),
  overline = TextStyle(
    fontFamily = frcFontFamily,
    fontWeight = FontWeight.W400,
    fontSize = 12.sp
  )
)
