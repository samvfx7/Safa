package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.IslamicApp
import com.example.navigation.Screen
import com.example.ui.components.StartupQuoteDialog
import com.example.ui.screens.bookmarks.BookmarksScreen
import com.example.ui.screens.duas.DuasScreen
import com.example.ui.screens.duas.DuasViewModel
import com.example.ui.screens.more.FastingTrackerScreen
import com.example.ui.screens.more.HadithScreen
import com.example.ui.screens.more.IslamicLearningScreen
import com.example.ui.screens.more.MasjidFinderScreen
import com.example.ui.screens.more.MoreScreen
import com.example.ui.screens.more.MoreViewModel
import com.example.ui.screens.more.PrayerStreakScreen
import com.example.ui.screens.more.SettingsScreen
import com.example.ui.screens.more.TasbihScreen
import com.example.ui.screens.more.ZakatCalculatorScreen
import com.example.ui.screens.prayer.FajrAlarmScreen
import com.example.ui.screens.prayer.PrayerTimesScreen
import com.example.ui.screens.prayer.PrayerViewModel
import com.example.ui.screens.qibla.QiblaScreen
import com.example.ui.screens.quran.QuranScreen
import com.example.ui.screens.quran.QuranViewModel
import com.example.ui.screens.quran.SurahDetailScreen
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaSpacing

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.PrayerTimes.route,
        title = "Prayer",
        selectedIcon = Icons.Filled.AccessTime,
        unselectedIcon = Icons.Outlined.AccessTime
    ),
    BottomNavItem(
        route = Screen.Quran.route,
        title = "Quran",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook
    ),
    BottomNavItem(
        route = Screen.Qibla.route,
        title = "Qibla",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    ),
    BottomNavItem(
        route = Screen.Duas.route,
        title = "Duas",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    BottomNavItem(
        route = Screen.More.route,
        title = "More",
        selectedIcon = Icons.Filled.GridView,
        unselectedIcon = Icons.Outlined.GridView
    )
)

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom navigation on full-screen flows like Fajr Alarm
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    val context = LocalContext.current
    val app = context.applicationContext as IslamicApp

    val prayerViewModel: PrayerViewModel = viewModel()
    val quranViewModel: QuranViewModel = viewModel()
    val duasViewModel: DuasViewModel = viewModel()
    val moreViewModel: MoreViewModel = viewModel()

    val safaColors = LocalSafaColors.current

    val tabRoutes = remember {
        listOf(
            Screen.PrayerTimes.route,
            Screen.Quran.route,
            Screen.Qibla.route,
            Screen.Duas.route,
            Screen.More.route
        )
    }

    val smoothEnterEasing = remember { CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f) }
    val smoothExitEasing = remember { CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f) }
    val emphasizedEasing = remember { CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.PrayerTimes.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                val initialRoute = initialState.destination.route
                val targetRoute = targetState.destination.route

                val initialIndex = tabRoutes.indexOf(initialRoute)
                val targetIndex = tabRoutes.indexOf(targetRoute)

                if (initialIndex != -1 && targetIndex != -1) {
                    // Smooth Tab Lateral Slide & Parallax
                    if (targetIndex > initialIndex) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> (fullWidth * 0.32f).toInt() },
                            animationSpec = tween(340, easing = emphasizedEasing)
                        ) + fadeIn(tween(280, easing = emphasizedEasing)) + scaleIn(initialScale = 0.97f, animationSpec = tween(340, easing = emphasizedEasing))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -(fullWidth * 0.32f).toInt() },
                            animationSpec = tween(340, easing = emphasizedEasing)
                        ) + fadeIn(tween(280, easing = emphasizedEasing)) + scaleIn(initialScale = 0.97f, animationSpec = tween(340, easing = emphasizedEasing))
                    }
                } else {
                    // Deep Destination Forward Navigation
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> (fullWidth * 0.8f).toInt() },
                        animationSpec = tween(380, easing = smoothEnterEasing)
                    ) + fadeIn(tween(300, easing = emphasizedEasing)) + scaleIn(initialScale = 0.96f, animationSpec = tween(380, easing = smoothEnterEasing))
                }
            },
            exitTransition = {
                val initialRoute = initialState.destination.route
                val targetRoute = targetState.destination.route

                val initialIndex = tabRoutes.indexOf(initialRoute)
                val targetIndex = tabRoutes.indexOf(targetRoute)

                if (initialIndex != -1 && targetIndex != -1) {
                    if (targetIndex > initialIndex) {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -(fullWidth * 0.32f).toInt() },
                            animationSpec = tween(300, easing = emphasizedEasing)
                        ) + fadeOut(tween(220, easing = emphasizedEasing)) + scaleOut(targetScale = 0.97f, animationSpec = tween(300, easing = emphasizedEasing))
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> (fullWidth * 0.32f).toInt() },
                            animationSpec = tween(300, easing = emphasizedEasing)
                        ) + fadeOut(tween(220, easing = emphasizedEasing)) + scaleOut(targetScale = 0.97f, animationSpec = tween(300, easing = emphasizedEasing))
                    }
                } else {
                    // Outgoing Screen Parallax Shift
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -(fullWidth * 0.24f).toInt() },
                        animationSpec = tween(320, easing = smoothExitEasing)
                    ) + fadeOut(tween(240, easing = emphasizedEasing)) + scaleOut(targetScale = 0.96f, animationSpec = tween(320, easing = smoothExitEasing))
                }
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -(fullWidth * 0.24f).toInt() },
                    animationSpec = tween(360, easing = smoothEnterEasing)
                ) + fadeIn(tween(280, easing = emphasizedEasing)) + scaleIn(initialScale = 0.96f, animationSpec = tween(360, easing = smoothEnterEasing))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> (fullWidth * 0.8f).toInt() },
                    animationSpec = tween(340, easing = smoothExitEasing)
                ) + fadeOut(tween(240, easing = emphasizedEasing)) + scaleOut(targetScale = 0.96f, animationSpec = tween(340, easing = smoothExitEasing))
            }
        ) {
            // Tab 1: Prayer Times
            composable(Screen.PrayerTimes.route) {
                PrayerTimesScreen(
                    viewModel = prayerViewModel,
                    onNavigateToAlarm = { navController.navigate(Screen.FajrAlarm.route) }
                )
            }

            // Tab 2: Quran
            composable(Screen.Quran.route) {
                QuranScreen(
                    viewModel = quranViewModel,
                    onNavigateToSurah = { surahNumber ->
                        navController.navigate(Screen.SurahDetail.createRoute(surahNumber))
                    },
                    onNavigateToBookmarks = { navController.navigate(Screen.Bookmarks.route) }
                )
            }

            // Surah Detail
            composable(
                route = Screen.SurahDetail.route,
                arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
            ) { backStackEntry ->
                val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
                SurahDetailScreen(
                    surahNumber = surahNumber,
                    viewModel = quranViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Tab 3: Qibla Compass
            composable(Screen.Qibla.route) {
                QiblaScreen()
            }

            // Tab 4: Duas
            composable(Screen.Duas.route) {
                DuasScreen(viewModel = duasViewModel)
            }

            // Tab 5: More Submenu Hub
            composable(Screen.More.route) {
                MoreScreen(
                    viewModel = moreViewModel,
                    onNavigateToRoute = { route -> navController.navigate(route) }
                )
            }

            // Sub-features
            composable(Screen.Tasbih.route) {
                TasbihScreen(
                    viewModel = moreViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Hadith.route) {
                HadithScreen(
                    viewModel = moreViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.IslamicLearning.route) {
                IslamicLearningScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.PrayerStreak.route) {
                PrayerStreakScreen(
                    viewModel = moreViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ZakatCalculator.route) {
                ZakatCalculatorScreen(
                    viewModel = moreViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.FastingTracker.route) {
                FastingTrackerScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.MasjidFinder.route) {
                MasjidFinderScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    settingsRepository = app.settingsRepository,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToSurah = { surahNumber ->
                        navController.navigate(Screen.SurahDetail.createRoute(surahNumber))
                    }
                )
            }

            // Fajr Mat Alarm Experience with Vertical Modal Slide
            composable(
                route = Screen.FajrAlarm.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeIn(tween(250))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeOut(tween(200))
                },
                popEnterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeIn(tween(250))
                },
                popExitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
                    ) + fadeOut(tween(200))
                }
            ) {
                FajrAlarmScreen(
                    onDismiss = { navController.popBackStack() },
                    onPrayerCompleted = {
                        prayerViewModel.togglePrayerCompleted("fajr", true)
                        navController.popBackStack()
                    }
                )
            }
        }

        // True Floating Bottom Navigation Bar
        AnimatedVisibility(
            visible = showBottomBar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            enter = slideInVertically(
                initialOffsetY = { it * 2 },
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f)
            ) + fadeIn(tween(260, easing = emphasizedEasing)) + scaleIn(initialScale = 0.92f, animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f)),
            exit = slideOutVertically(
                targetOffsetY = { it * 2 },
                animationSpec = spring(dampingRatio = 0.9f, stiffness = 350f)
            ) + fadeOut(tween(200, easing = emphasizedEasing)) + scaleOut(targetScale = 0.92f, animationSpec = tween(200))
        ) {
            FloatingPillBottomBar(
                currentRoute = currentRoute,
                items = bottomNavItems,
                onItemClick = { item ->
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
        
        // Show a random quote on app startup
        StartupQuoteDialog()
    }
}

@Composable
fun FloatingPillBottomBar(
    currentRoute: String?,
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val safaColors = LocalSafaColors.current

    Surface(
        shape = RoundedCornerShape(32.dp),
        color = if (safaColors.isLuxuryNavy) {
            Color(0xF50D1730) // Frosted luxury midnight navy
        } else {
            Color(0xF8FBF8F2) // Frosted warm sand / alabaster
        },
        tonalElevation = 10.dp,
        shadowElevation = 16.dp,
        border = BorderStroke(
            1.dp,
            if (safaColors.isLuxuryNavy) {
                safaColors.goldPrimary.copy(alpha = 0.35f)
            } else {
                safaColors.navyBorder.copy(alpha = 0.35f)
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = safaColors.goldPrimary.copy(alpha = 0.25f),
                ambientColor = Color.Black.copy(alpha = 0.35f)
            )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidth = maxWidth
            val count = items.size
            val itemWidth = totalWidth / count
            val selectedIndex = items.indexOfFirst { it.route == currentRoute }.let {
                if (it >= 0) it else 0
            }

            // Smoothly sliding active pill highlight indicator with fluid spring
            val animatedOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = spring(
                    dampingRatio = 0.76f,
                    stiffness = 320f
                ),
                label = "slidingIndicatorOffset"
            )

            Box(
                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (safaColors.isLuxuryNavy) {
                                listOf(
                                    safaColors.goldGlow.copy(alpha = 0.35f),
                                    safaColors.goldGlow.copy(alpha = 0.18f)
                                )
                            } else {
                                listOf(
                                    safaColors.goldGlow.copy(alpha = 0.48f),
                                    safaColors.goldGlow.copy(alpha = 0.25f)
                                )
                            }
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .border(
                        1.dp,
                        safaColors.goldPrimary.copy(alpha = 0.38f),
                        RoundedCornerShape(22.dp)
                    )
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    FloatingPillNavItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onItemClick(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingPillNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safaColors = LocalSafaColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Fluid spring animations for touch press, selection scale, and vertical displacement
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.91f
            isSelected -> 1.07f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "nav_scale_${item.title}"
    )

    val iconOffsetY by animateDpAsState(
        targetValue = if (isSelected) (-2).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "nav_icon_offset_${item.title}"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "nav_icon_scale_${item.title}"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) safaColors.goldPrimary else safaColors.textSecondary,
        animationSpec = tween(durationMillis = 260, easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)),
        label = "nav_icon_color_${item.title}"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) safaColors.goldPrimary else safaColors.textSecondary,
        animationSpec = tween(durationMillis = 260, easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)),
        label = "nav_text_color_${item.title}"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = safaColors.goldPrimary.copy(alpha = 0.25f)
                ),
                onClick = onClick
            )
            .testTag("nav_item_${item.title.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        // Content: Icon + Label + Micro Active Indicator Dot
        Column(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .padding(vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.title,
                tint = iconColor,
                modifier = Modifier
                    .size(21.dp)
                    .graphicsLayer {
                        translationY = iconOffsetY.toPx()
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                letterSpacing = if (isSelected) 0.3.sp else 0.sp,
                maxLines = 1
            )

            // Micro Active Glow Dot with bouncy entrance
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(tween(200)) + scaleIn(spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow)),
                exit = fadeOut(tween(140)) + scaleOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .size(3.5.dp)
                        .background(safaColors.goldPrimary, CircleShape)
                )
            }
        }
    }
}

