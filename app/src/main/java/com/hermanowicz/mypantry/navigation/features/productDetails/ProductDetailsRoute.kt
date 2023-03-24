package com.hermanowicz.mypantry.navigation.features.productDetails

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.hermanowicz.mypantry.navigation.features.productDetails.ui.ProductDetailsScreen

@Composable
fun ProductDetailsRoute(
    navController: NavHostController,
    openDrawer: () -> Unit
) {
    ProductDetailsScreen(openDrawer)
}
