package com.example.ui.screens.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalSafaColors
import com.example.ui.theme.SafaNavyDark
import com.example.ui.theme.SafaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatCalculatorScreen(
    viewModel: MoreViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val zakatState by viewModel.zakatState.collectAsState()
    val safaColors = LocalSafaColors.current

    var cashInput by remember { mutableStateOf(if (zakatState.cashOnHand > 0) zakatState.cashOnHand.toString() else "") }
    var goldInput by remember { mutableStateOf(if (zakatState.goldValue > 0) zakatState.goldValue.toString() else "") }
    var silverInput by remember { mutableStateOf(if (zakatState.silverValue > 0) zakatState.silverValue.toString() else "") }
    var investmentsInput by remember { mutableStateOf(if (zakatState.investments > 0) zakatState.investments.toString() else "") }
    var liabilitiesInput by remember { mutableStateOf(if (zakatState.liabilities > 0) zakatState.liabilities.toString() else "") }

    fun recalculate() {
        viewModel.updateZakatInputs(
            cash = cashInput.toDoubleOrNull() ?: 0.0,
            gold = goldInput.toDoubleOrNull() ?: 0.0,
            silver = silverInput.toDoubleOrNull() ?: 0.0,
            investments = investmentsInput.toDoubleOrNull() ?: 0.0,
            liabilities = liabilitiesInput.toDoubleOrNull() ?: 0.0
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Zakat Calculator",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = safaColors.goldPrimary,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = safaColors.goldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = SafaSpacing.screenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(SafaSpacing.md)
        ) {
            // Result Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadiusLarge),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.2.dp, safaColors.goldPrimary.copy(alpha = 0.6f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = if (safaColors.isLuxuryNavy) {
                                        listOf(safaColors.navyElevated, safaColors.navySurface, safaColors.navyBackground)
                                    } else {
                                        listOf(safaColors.navyElevated, safaColors.navySurface)
                                    }
                                )
                            )
                            .padding(SafaSpacing.cardContentPadding)
                    ) {
                        Column {
                            Text(
                                text = "TOTAL ZAKAT DUE (2.5%)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne else safaColors.textGold,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$${String.format("%.2f", zakatState.zakatPayable)}",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = safaColors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (zakatState.isEligible)
                                    "Your net wealth exceeds the Nisab threshold ($6,800)."
                                else
                                    "Your net wealth is currently below the Nisab threshold.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (safaColors.isLuxuryNavy) safaColors.goldChampagne.copy(alpha = 0.9f) else safaColors.textSecondary
                            )
                        }
                    }
                }
            }

            // Input Fields
            item {
                Text(
                    text = "Zakatable Assets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textPrimary
                )
            }

            item {
                ZakatInputField(
                    label = "Cash on Hand & Bank Accounts ($)",
                    value = cashInput,
                    onValueChange = {
                        cashInput = it
                        recalculate()
                    }
                )
            }

            item {
                ZakatInputField(
                    label = "Gold Jewelry & Bullion Value ($)",
                    value = goldInput,
                    onValueChange = {
                        goldInput = it
                        recalculate()
                    }
                )
            }

            item {
                ZakatInputField(
                    label = "Silver & Precious Metals ($)",
                    value = silverInput,
                    onValueChange = {
                        silverInput = it
                        recalculate()
                    }
                )
            }

            item {
                ZakatInputField(
                    label = "Shares, Crypto & Business Merchandise ($)",
                    value = investmentsInput,
                    onValueChange = {
                        investmentsInput = it
                        recalculate()
                    }
                )
            }

            item {
                Text(
                    text = "Deductible Liabilities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = safaColors.textPrimary
                )
            }

            item {
                ZakatInputField(
                    label = "Immediate Debts & Outstanding Bills ($)",
                    value = liabilitiesInput,
                    onValueChange = {
                        liabilitiesInput = it
                        recalculate()
                    }
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(SafaSpacing.cardRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, safaColors.goldBorder.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(SafaSpacing.cardContentPadding)) {
                        Text(
                            text = "About Nisab & Zakat Rules",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = safaColors.goldPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nisab is the minimum threshold of surplus wealth a Muslim must own for one lunar year before Zakat is due (approx. 85g gold ~ $6,800 or 595g silver). 2.5% is payable annually on the net qualifying wealth.",
                            style = MaterialTheme.typography.bodySmall,
                            color = safaColors.textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ZakatInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val safaColors = LocalSafaColors.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = safaColors.textSecondary) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = RoundedCornerShape(SafaSpacing.cardRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = safaColors.goldPrimary,
            unfocusedBorderColor = safaColors.navyBorder.copy(alpha = 0.3f),
            focusedTextColor = safaColors.textPrimary,
            unfocusedTextColor = safaColors.textPrimary
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
