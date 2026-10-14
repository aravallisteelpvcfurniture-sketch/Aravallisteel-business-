package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Carpenter
import com.example.ui.MainViewModel
import com.example.ui.theme.IconCarpenterCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarpentersScreen(
    viewModel: MainViewModel,
    carpenters: List<Carpenter>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    var showRegisterDialog by remember { mutableStateOf(false) }
    var selectedCarpenterForReferral by remember { mutableStateOf<Carpenter?>(null) }
    var selectedCarpenterForPayout by remember { mutableStateOf<Carpenter?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Aravalli Carpenter Club", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Partner Network, Rewards & Commissions", fontSize = 11.sp, color = Color.White.copy(0.85f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("carpenters_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IconCarpenterCyan,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showRegisterDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Register Carpenter") },
                containerColor = IconCarpenterCyan,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_register_carpenter")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE1F5FE).copy(alpha = 0.5f))
        ) {
            // Club Info Banner
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Aravalli Mitra Scheme",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF01579B)
                        )
                        Text(
                            text = "₹1,000 to ₹5,000 commission + 10 pts per ₹1,000 ordered",
                            fontSize = 11.sp,
                            color = Color(0xFF546E7A)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(carpenters, key = { it.id }) { carpenter ->
                    CarpenterItemCard(
                        carpenter = carpenter,
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${carpenter.phone}"))
                            context.startActivity(intent)
                        },
                        onAddReferral = { selectedCarpenterForReferral = carpenter },
                        onPayout = { selectedCarpenterForPayout = carpenter }
                    )
                }
            }
        }
    }

    if (showRegisterDialog) {
        RegisterCarpenterDialog(
            onDismiss = { showRegisterDialog = false },
            onSave = { name, phone, location ->
                viewModel.addCarpenter(name, phone, location)
                showRegisterDialog = false
            }
        )
    }

    selectedCarpenterForReferral?.let { carp ->
        AddReferralDialog(
            carpenter = carp,
            onDismiss = { selectedCarpenterForReferral = null },
            onConfirm = { commission, pts ->
                viewModel.recordCarpenterReferral(carp, commission, pts)
                selectedCarpenterForReferral = null
            }
        )
    }

    selectedCarpenterForPayout?.let { carp ->
        PayoutDialog(
            carpenter = carp,
            onDismiss = { selectedCarpenterForPayout = null },
            onConfirm = { amt ->
                viewModel.payoutCarpenter(carp, amt)
                selectedCarpenterForPayout = null
            }
        )
    }
}

@Composable
fun CarpenterItemCard(
    carpenter: Carpenter,
    onCall: () -> Unit,
    onAddReferral: () -> Unit,
    onPayout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("carpenter_card_${carpenter.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = carpenter.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "${carpenter.location} • ${carpenter.phone}", fontSize = 12.sp, color = Color(0xFF64748B))
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE0F7FA)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFF00838F), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${carpenter.pointsEarned} Pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00838F))
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Sites Referred", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text("${carpenter.totalReferrals} Orders", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column {
                    Text("Total Earned", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text("₹${String.format(java.util.Locale.getDefault(), "%,.0f", carpenter.totalCommissionEarned)}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Pending Payout", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(
                        "₹${String.format(java.util.Locale.getDefault(), "%,.0f", carpenter.pendingPayout)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (carpenter.pendingPayout > 0) Color(0xFFE65100) else Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 12.sp)
                }

                Button(
                    onClick = onAddReferral,
                    colors = ButtonDefaults.buttonColors(containerColor = IconCarpenterCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Site", fontSize = 12.sp)
                }

                if (carpenter.pendingPayout > 0) {
                    Button(
                        onClick = onPayout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Text("Pay UPI", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterCarpenterDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, location: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Carpenter Partner") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Carpenter / Fabricator Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Workshop Area / City") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) onSave(name, phone, location)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IconCarpenterCyan)
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddReferralDialog(
    carpenter: Carpenter,
    onDismiss: () -> Unit,
    onConfirm: (commission: Double, points: Int) -> Unit
) {
    var siteName by remember { mutableStateOf("") }
    var orderValueText by remember { mutableStateOf("100000") }
    val orderVal = orderValueText.toDoubleOrNull() ?: 0.0
    val commission = orderVal * 0.03 // 3% referral commission
    val points = (orderVal / 100).toInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Referral Site") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Carpenter: ${carpenter.name}")
                OutlinedTextField(
                    value = siteName,
                    onValueChange = { siteName = it },
                    label = { Text("Client / Site Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = orderValueText,
                    onValueChange = { orderValueText = it },
                    label = { Text("Order Value (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    color = Color(0xFFE0F7FA),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Commission (3%): ₹${String.format(java.util.Locale.getDefault(), "%,.0f", commission)}", fontWeight = FontWeight.Bold, color = Color(0xFF006064))
                        Text("Reward Points: +$points pts", fontSize = 12.sp, color = Color(0xFF00838F))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(commission, points) },
                colors = ButtonDefaults.buttonColors(containerColor = IconCarpenterCyan)
            ) {
                Text("Credit Commission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PayoutDialog(
    carpenter: Carpenter,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double) -> Unit
) {
    var amountText by remember { mutableStateOf("${carpenter.pendingPayout.toInt()}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Payout Carpenter Commission") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pay to: ${carpenter.name}")
                Text("Pending: ₹${String.format(java.util.Locale.getDefault(), "%,.0f", carpenter.pendingPayout)}", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Payout Amount (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) onConfirm(amt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Confirm Payout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
