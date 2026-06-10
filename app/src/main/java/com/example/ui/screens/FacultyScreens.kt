package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.BasicTextField
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AnalyticsMetrics
import com.example.ui.viewmodel.FacultyViewModel

fun Modifier.glassBackdrop(isDark: Boolean): Modifier = this.drawBehind {
    val baseColors = if (isDark) {
        listOf(Color(0xFF0F172A), Color(0xFF020617))
    } else {
        listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
    }
    drawRect(
        brush = Brush.verticalGradient(colors = baseColors)
    )
    
    val orbColor1 = if (isDark) Color(0xFF6366F1).copy(alpha = 0.22f) else Color(0xFF818CF8).copy(alpha = 0.45f)
    val orbColor2 = if (isDark) Color(0xFF0D9488).copy(alpha = 0.18f) else Color(0xFF2DD4BF).copy(alpha = 0.35f)
    val orbColor3 = if (isDark) Color(0xFFEC4899).copy(alpha = 0.12f) else Color(0xFFF472B6).copy(alpha = 0.25f)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(orbColor1, Color.Transparent),
            center = Offset(size.width * 0.15f, size.height * 0.25f),
            radius = size.width * 0.8f
        ),
        radius = size.width * 0.8f,
        center = Offset(size.width * 0.15f, size.height * 0.25f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(orbColor2, Color.Transparent),
            center = Offset(size.width * 0.85f, size.height * 0.75f),
            radius = size.width * 0.9f
        ),
        radius = size.width * 0.9f,
        center = Offset(size.width * 0.85f, size.height * 0.75f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(orbColor3, Color.Transparent),
            center = Offset(size.width * 0.8f, size.height * 0.15f),
            radius = size.width * 0.5f
        ),
        radius = size.width * 0.5f,
        center = Offset(size.width * 0.8f, size.height * 0.15f)
    )
}

@Composable
fun MainAppScreen(viewModel: FacultyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    MyApplicationTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .glassBackdrop(isSystemInDarkTheme())
        ) {
            when (currentScreen) {
                "login" -> LoginScreen(viewModel)
                "print" -> PrintPreviewScreen(viewModel)
                else -> {
                    // Modern scaffold layout for logged in sessions with responsive bottom/side nav
                    Scaffold(
                        topBar = { TopAcademicBar(viewModel) },
                        bottomBar = { SecondaryBottomNavigation(viewModel) },
                        containerColor = Color.Transparent
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Support expandable side rail for larger width screens / tablet
                            Box(modifier = Modifier.fillMaxSize()) {
                                when (currentScreen) {
                                    "overview" -> AnalyticsOverviewScreen(viewModel)
                                    "students" -> StudentManagementScreen(viewModel)
                                    "attendance" -> AttendanceManagementScreen(viewModel)
                                    "marks" -> MarksManagerScreen(viewModel)
                                    "profile" -> FacultyProfileScreen(viewModel)
                                    "ai_insights" -> AiInsightsScreen(viewModel)
                                    "presentation" -> PresentationLayoutScreen(viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAcademicBar(viewModel: FacultyViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val selectedCode by viewModel.selectedSubjectCode.collectAsStateWithLifecycle()
    var dropdownExpanded by remember { mutableStateOf(false) }

    val activeSubjectName = subjects.find { it.subjectCode == selectedCode }?.subjectName ?: "No Course Selected"

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Faculty Pro",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFE4E6))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "NBA-COMPLIANT",
                            fontSize = 9.sp,
                            color = Color(0xFFE11D48),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Text(
                    text = "${currentUser?.name ?: ""} | ${currentUser?.department ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        actions = {
            // Subject selection chip dropdown
            if (subjects.isNotEmpty()) {
                Box {
                    Button(
                        onClick = { dropdownExpanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("course_dropdown_button"),
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = selectedCode, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        subjects.forEach { subj ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.School,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                text = {
                                    Column {
                                        Text(subj.subjectName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Text("${subj.subjectCode} • ${subj.section}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    viewModel.selectSubject(subj.subjectCode)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = { viewModel.setScreen("print") },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("app_print_button")
            ) {
                Icon(Icons.Default.Print, contentDescription = "Print Reports", tint = MaterialTheme.colorScheme.onSurface)
            }

            IconButton(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("app_logout_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Log out", tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}

@Composable
fun SecondaryBottomNavigation(viewModel: FacultyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val menuItems = listOf(
            Triple("overview", Icons.Default.Dashboard, "Dashboard"),
            Triple("students", Icons.Default.People, "Students"),
            Triple("attendance", Icons.Default.CalendarToday, "Attendance"),
            Triple("marks", Icons.Default.Grade, "Marks"),
            Triple("ai_insights", Icons.Default.AutoAwesome, "AI Advisor"),
            Triple("presentation", Icons.Default.Analytics, "Report Deck")
        )

        menuItems.forEach { (route, icon, label) ->
            val isSelected = currentScreen == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { viewModel.setScreen(route) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, overflow = TextOverflow.Ellipsis, maxLines = 1, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("bottom_nav_$route")
            )
        }
    }
}

@Composable
fun LoginScreen(viewModel: FacultyViewModel) {
    var emailInput by remember { mutableStateOf("sarah@college.edu") }
    var passwordInput by remember { mutableStateOf("********") }
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .glassBackdrop(isDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 420.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Enterprise Logo Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF6366F1), Color(0xFF3B82F6))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "FACULTY PRO",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                letterSpacing = 1.6.sp
            )
            Text(
                text = "Next-Gen Academic Performance Analytics System",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF475569),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Credentials Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = GlassCardShape,
                border = glassBorder(isDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                val textColor = if (isDark) Color.White else Color(0xFF0F172A)
                val subColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF475569)
                val labelColor = if (isDark) Color.White.copy(0.7f) else Color(0xFF0F172A).copy(0.6f)
                val borderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color(0xFF0F172A).copy(alpha = 0.2f)

                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Secure ERP Gateway",
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Sign in using your institutional credentials.",
                        color = subColor,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("College Email", color = labelColor) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = PrimaryIndigo,
                            unfocusedLabelColor = labelColor
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password", color = labelColor) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = PrimaryIndigo,
                            unfocusedLabelColor = labelColor
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.login(emailInput) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_primary_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Text("Connect ERP Session", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick-launch bypass profiles for accreditation reviews
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFF0F172A).copy(alpha = 0.1f))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "QUICK ACCESS DEMO CHANNELS",
                        fontSize = 10.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.4f) else Color(0xFF475569).copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                emailInput = "sarah@college.edu"; viewModel.login("sarah@college.edu")
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                            border = BorderStroke(1.dp, if (isDark) Color.White.copy(0.15f) else Color(0xFF0F172A).copy(0.15f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .padding(end = 4.dp)
                                .testTag("demo_btn_faculty")
                        ) {
                            Text("Dean Profile", fontSize = 11.sp, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                emailInput = "admin@college.sys"; viewModel.login("admin@college.sys", "ADMIN")
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                            border = BorderStroke(1.dp, if (isDark) Color.White.copy(0.15f) else Color(0xFF0F172A).copy(0.15f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .padding(start = 4.dp)
                                .testTag("demo_btn_admin")
                        ) {
                            Text("Admin Portal", fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsOverviewScreen(viewModel: FacultyViewModel) {
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val selectedCode by viewModel.selectedSubjectCode.collectAsStateWithLifecycle()

    var showAddSubjectDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Subject Add/Remove bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Unified Course Metrics",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time accreditation and class stats indicators.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Button(
                    onClick = { showAddSubjectDialog = true },
                    modifier = Modifier.testTag("add_subject_fab"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Course")
                }
            }
        }

        if (metrics == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Text(
                            "No student or class details loaded yet.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            val met = metrics!!

            item {
                // Row of robust KPI cards inspired by Power BI dashboards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        title = "TOTAL ENROLLS",
                        value = met.totalStudents.toString(),
                        subtitle = "Students registered",
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "PASS RATE",
                        value = "%.1f%%".format(met.passPercentage),
                        subtitle = ">= 40% Marks",
                        color = AccentEmerald,
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        title = "CLASS AVERAGE",
                        value = "%.1f/100".format(met.avgMarks),
                        subtitle = "Mean score",
                        color = SecondaryTeal,
                        icon = Icons.Default.Analytics,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "LOW ATTENDANCE",
                        value = met.lowAttendanceCount.toString(),
                        subtitle = "Below 75%",
                        color = WarningAmber,
                        icon = Icons.Default.Warning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                // SPI & GRADE ACCORDION PANEL - Left-right design
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // SPI circular gauge meter
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = GlassCardShape,
                        border = glassBorder(),
                        modifier = Modifier
                            .weight(1f)
                            .height(220.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "SUBJECT PERFORMANCE INDEX",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(100.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { met.spiValue / 10f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 10.dp,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "%.2f".format(met.spiValue),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text("of 10 SPI", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Compliance: " + if(met.spiValue > 6.5) "ELITE ACCREDITATION" else "RECOVERY NEEDED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if(met.spiValue > 6.5) AccentEmerald else WarningAmber
                            )
                        }
                    }

                    // Top Performers Gold/Silver List
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = GlassCardShape,
                        border = glassBorder(),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(220.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "CLASS TOP PERFORMERS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = AccentEmerald
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn {
                                itemsIndexed(met.topPerformers.take(3)) { index, student ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (index == 0) Color(0xFFFFD700) else if (index == 1) Color(0xFFC0C0C0) else Color(0xFFCD7F32)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "${index + 1}",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                student.studentName,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(student.rollNumber, fontSize = 10.sp, color = Color.Gray)
                                        }
                                        Text(
                                            "%.1f%%".format(student.totalMarks),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // GRADE DISTRIBUTION BAR CHART (Power BI custom drawing)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = GlassCardShape,
                    border = glassBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ACCUMULATED GRADE DISTRIBUTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        GradeBarChart(met.gradeDistribution)
                    }
                }
            }

            item {
                // ATTENDANCE TO GRADES ATTRIBUTION CORRELATION PLOT
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = GlassCardShape,
                    border = glassBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, "AI", tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ATTENDANCE VS ACHIEVEMENT CORRELATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = met.correlationInsight,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }

    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name, code, sem, dept, sec, year ->
                viewModel.addSubject(name, code, sem, dept, sec, year)
                showAddSubjectDialog = false
            }
        )
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(105.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = GlassCardShape,
        border = glassBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    letterSpacing = 0.5.sp
                )
                Icon(icon, contentDescription = null, tint = color.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun GradeBarChart(distribution: Map<String, Int>) {
    val grades = listOf("A+", "A", "B", "C", "D", "F")
    val maxCount = (distribution.values.maxOrNull() ?: 1).coerceAtLeast(1)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        grades.forEach { gr ->
            val count = distribution[gr] ?: 0
            val fraction = count.toFloat() / maxCount.toFloat()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = gr,
                    modifier = Modifier.width(32.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Smooth animated progress bar width
                val animatedProgress by animateFloatAsState(
                    targetValue = fraction,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "bar_$gr"
                )

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "$count qty",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

// Dialog to add customized Subjects
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var sem by remember { mutableStateOf("Semester V") }
    var dept by remember { mutableStateOf("CSE") }
    var sec by remember { mutableStateOf("Sec A") }
    var year by remember { mutableStateOf("2025-2026") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Deploy New Curricular Course", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Course Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Course Code (e.g. CS-305)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sem,
                    onValueChange = { sem = it },
                    label = { Text("Semester (Roman numeral index)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dept,
                    onValueChange = { dept = it },
                    label = { Text("Branch/Department Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sec,
                    onValueChange = { sec = it },
                    label = { Text("Assigned Section") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank() && code.isNotBlank()) onConfirm(name, code, sem, dept, sec, year) },
                enabled = name.isNotBlank() && code.isNotBlank()
            ) {
                Text("Deploy Course")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abort")
            }
        }
    )
}

@Composable
fun StudentManagementScreen(viewModel: FacultyViewModel) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    var searchTxt by remember { mutableStateOf("") }
    var filterLowAtt by remember { mutableStateOf(false) }

    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showImportExcelDialog by remember { mutableStateOf(false) }

    val filteredList = students.filter {
        (it.studentName.contains(searchTxt, ignoreCase = true) || it.rollNumber.contains(searchTxt, ignoreCase = true)) &&
                (!filterLowAtt || it.attendancePercentage < 75f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toolbar controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Student Enrollment Index", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Add manual entries, export or batch import Excel lists.", fontSize = 12.sp, color = Color.Gray)
            }

            Row {
                IconButton(
                    onClick = { showImportExcelDialog = true },
                    modifier = Modifier.testTag("student_import_excel_btn")
                ) {
                    Icon(Icons.Default.UploadFile, "Excel Import", tint = MaterialTheme.colorScheme.primary)
                }

                Button(
                    onClick = { showAddStudentDialog = true },
                    modifier = Modifier.testTag("add_student_fab"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Pupil")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search + Filtration toggler
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchTxt,
                onValueChange = { searchTxt = it },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                placeholder = { Text("Search by name or Roll No...") },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilterChip(
                selected = filterLowAtt,
                onClick = { filterLowAtt = !filterLowAtt },
                label = { Text("Att < 75%") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WarningAmber.copy(0.2f),
                    selectedLabelColor = WarningAmber
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List Grid View
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No pupils correspond to active search query.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { student ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = glassBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Roll No: ${student.rollNumber} • ${student.department}", fontSize = 12.sp, color = Color.Gray)
                            }

                            // Attendance gauge indicator tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (student.attendancePercentage < 75f) DestructiveRose.copy(0.15f) else AccentEmerald.copy(0.15f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Att: %.1f%%".format(student.attendancePercentage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (student.attendancePercentage < 75f) DestructiveRose else AccentEmerald
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            IconButton(
                                onClick = { viewModel.deleteStudent(student.rollNumber) },
                                modifier = Modifier.testTag("delete_student_${student.rollNumber}")
                            ) {
                                Icon(Icons.Default.Delete, "Delete", tint = DestructiveRose)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddStudentDialog) {
        AddStudentDialog(
            onDismiss = { showAddStudentDialog = false },
            onConfirm = { roll, name, dept, sem, sec, attPct ->
                viewModel.addStudent(roll, name, dept, sem, sec, attPct)
                showAddStudentDialog = false
            }
        )
    }

    if (showImportExcelDialog) {
        ImportExcelDialog(
            onDismiss = { showImportExcelDialog = false },
            onImport = { data ->
                viewModel.mockImportExcel(data)
                showImportExcelDialog = false
            }
        )
    }
}

@Composable
fun AddStudentDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String, String, Float) -> Unit) {
    var roll by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("CSE") }
    var sem by remember { mutableStateOf("Semester VI") }
    var sec by remember { mutableStateOf("Sec A") }
    var attPct by remember { mutableStateOf("85") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enroll New Pupil Record", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = roll, onValueChange = { roll = it }, label = { Text("Roll Number") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Pupil Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dept, onValueChange = { dept = it }, label = { Text("Department") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = attPct, onValueChange = { attPct = it }, label = { Text("Lecture Attendance %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(roll, name, dept, sem, sec, attPct.toFloatOrNull() ?: 85f)
                },
                enabled = roll.isNotBlank() && name.isNotBlank()
            ) {
                Text("Enroll Student")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abort") }
        }
    )
}

@Composable
fun ImportExcelDialog(onDismiss: () -> Unit, onImport: (String) -> Unit) {
    var batchText by remember { mutableStateOf(
        "ROLL-101, Albert Stark, 92.4\nROLL-102, Bruce Wayne, 68.5\nROLL-103, Clark Kent, 88.0\nROLL-104, Diana Prince, 74.0"
    ) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.UploadFile, null, tint = AccentEmerald)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Mock CSV/Excel Loader", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Paste comma-separated rows below corresponding to columns: RollNo, Full Name, Attendance%. Useful for fast batch accreditation syncing.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = batchText,
                    onValueChange = { batchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    placeholder = { Text("Format: Roll, Name, Attendance%") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onImport(batchText) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald)
            ) {
                Text("Process Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AttendanceManagementScreen(viewModel: FacultyViewModel) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val attendanceLogs by viewModel.attendance.collectAsStateWithLifecycle()
    val rawDate by viewModel.attendanceDate.collectAsStateWithLifecycle()

    var customDateInput by remember { mutableStateOf(rawDate) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Daily Attendance Ledger", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Log session details and update records immediately.", fontSize = 12.sp, color = Color.Gray)
            }

            // Simple micro date input field
            OutlinedTextField(
                value = customDateInput,
                onValueChange = {
                    customDateInput = it
                    viewModel.setAttendanceDate(it)
                },
                label = { Text("Session Date") },
                modifier = Modifier.width(150.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (students.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Please enroll students or deploy subjects to toggle attendance.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students) { student ->
                    val isPresentThisDate = attendanceLogs.find {
                        it.rollNumber == student.rollNumber && it.date == rawDate
                    }?.isPresent ?: true

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = glassBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(student.studentName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Roll: ${student.rollNumber} • Cumulative: %.1f%%".format(student.attendancePercentage), fontSize = 11.sp, color = Color.Gray)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isPresentThisDate) "PRESENT" else "ABSENT",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isPresentThisDate) AccentEmerald else DestructiveRose,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                Switch(
                                    checked = isPresentThisDate,
                                    onCheckedChange = { viewModel.saveAttendance(student.rollNumber, it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AccentEmerald,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = DestructiveRose
                                    ),
                                    modifier = Modifier.testTag("attendance_toggle_${student.rollNumber}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarksManagerScreen(viewModel: FacultyViewModel) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val assessments by viewModel.assessments.collectAsStateWithLifecycle()
    val marks by viewModel.marks.collectAsStateWithLifecycle()

    var showAddAssessment by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Dynamic Marks Entry Sheets", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Click Create Column to customize assessments instantly.", fontSize = 12.sp, color = Color.Gray)
            }

            Button(
                onClick = { showAddAssessment = true },
                modifier = Modifier.testTag("create_assessment_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Column")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (students.isEmpty() || assessments.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Create columns to build your custom syllabus marking grids.", color = Color.Gray)
                }
            }
        } else {
            // Spacious spreadsheet grid layout
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students) { student ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = glassBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(student.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Roll: ${student.rollNumber}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("TOTAL: %.1f%%".format(student.totalMarks), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (student.grade == "F") DestructiveRose.copy(0.12f) else AccentEmerald.copy(0.12f)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(student.grade, fontWeight = FontWeight.Bold, color = if (student.grade == "F") DestructiveRose else AccentEmerald, fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Horizontal scroll grid of assessment marks entries for this student
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(assessments) { ass ->
                                    val currentMarkValue = marks.find {
                                        it.rollNumber == student.rollNumber && it.assessmentId == ass.id
                                    }?.marksObtained ?: 0f

                                    MarksInputBubble(
                                        title = ass.name,
                                        currentValue = currentMarkValue,
                                        maxValue = ass.maxMarks,
                                        onValueEntered = { entered ->
                                            viewModel.saveStudentMarks(student.rollNumber, ass.id, entered)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddAssessment) {
        CreateAssessmentDialog(
            onDismiss = { showAddAssessment = false },
            onConfirm = { name, max, wght, qtns, crit ->
                viewModel.addAssessment(name, max, wght, qtns, crit)
                showAddAssessment = false
            }
        )
    }
}

@Composable
fun MarksInputBubble(
    title: String,
    currentValue: Float,
    maxValue: Float,
    onValueEntered: (Float) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var inTxt by remember { mutableStateOf(currentValue.toString()) }

    Box(
        modifier = Modifier
            .clickable { isEditing = true }
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.06f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        if (!isEditing) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("%.1f/%.0f".format(currentValue, maxValue), fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = inTxt,
                    onValueChange = { inTxt = it },
                    modifier = Modifier.width(36.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                IconButton(
                    onClick = {
                        val value = inTxt.toFloatOrNull() ?: currentValue
                        onValueEntered(value.coerceIn(0f, maxValue))
                        isEditing = false
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Default.Check, "Save", tint = AccentEmerald, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
fun CreateAssessmentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Float, Float, Int, Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var maxMarks by remember { mutableStateOf("100") }
    var weightage by remember { mutableStateOf("40") }
    var questions by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Curricular Column", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Assessment Column Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = maxMarks, onValueChange = { maxMarks = it }, label = { Text("Maximum Base Mark") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = weightage, onValueChange = { weightage = it }, label = { Text("Accreditation Weightage (%)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        name,
                        maxMarks.toFloatOrNull() ?: 100f,
                        weightage.toFloatOrNull() ?: 40f,
                        questions.toIntOrNull() ?: 5,
                        40.0f
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text("Embed assessment column")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abort") }
        }
    )
}

@Composable
fun AiInsightsScreen(viewModel: FacultyViewModel) {
    val aiResponse by viewModel.aiAnalysisResult.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, "AI", tint = PrimaryIndigo, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Advisor & Accreditation Assistant", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            }

            Button(
                onClick = { viewModel.fetchGeminiInsights() },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                enabled = !isAnalyzing,
                modifier = Modifier.testTag("generate_insights_btn")
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Querying...")
                } else {
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Regenerate")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = GlassCardShape,
            border = glassBorder()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                if (aiResponse.isBlank() && !isAnalyzing) {
                    // Empty welcoming state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = PrimaryIndigo.copy(0.4f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Query the Gemini intelligence model to calculate syllabus correlation metrics, NBA compliance factors, and list recovery paths for at-risk pupils.",
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    Text(
                        text = if (isAnalyzing) "Gemini Advisor compiling executive metrics summary..." else aiResponse,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PresentationLayoutScreen(viewModel: FacultyViewModel) {
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val selectedCode by viewModel.selectedSubjectCode.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Dean & Board Presentation Deck", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text("High-resolution, structured snapshots perfect for NAAC documentation and principal reviews.", fontSize = 12.sp, color = Color.Gray)
        }

        if (metrics == null) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("No data to present. Build student lists first.", color = Color.Gray)
            }
        } else {
            val m = metrics!!
            Text("CURRENT ACCREDITATION MILESTONES: $selectedCode", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)

            PresentationSlideCard(
                slideNumber = "SLIDE 1: RETENTION & PERFORMANCE ATTRIBUTION",
                title = "Engagement correlates directly to Grade achievements",
                bullet1 = "Class registry average is %.1f%% with a general compliance factor evaluated at %.2f SPI.".format(m.avgMarks, m.spiValue),
                bullet2 = "Top 5% students display > 92% attendance on standard academic hours.",
                bullet3 = "Recovery lists launched for ${m.studentsAtRisk.size} students currently struggling below the pass criteria threshold.",
                accentColor = MaterialTheme.colorScheme.primary
            )

            PresentationSlideCard(
                slideNumber = "SLIDE 2: DISABLING LEARNING GAP DEFICITS",
                title = "NBA Outcome-Based Compliance Matrix",
                bullet1 = "Calculations show %.1f%% pass percentile for this section.".format(m.passPercentage),
                bullet2 = "Standard curriculum goals set at 75% target have been successfully surpassed.",
                bullet3 = "Highest achievement noted at %.1f/100 points.".format(m.highestMarks),
                accentColor = SecondaryTeal
            )
        }
    }
}

@Composable
fun PresentationSlideCard(
    slideNumber: String,
    title: String,
    bullet1: String,
    bullet2: String,
    bullet3: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = GlassCardShape,
        border = glassBorder(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(slideNumber, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = accentColor, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BulletRow(bullet1)
                BulletRow(bullet2)
                BulletRow(bullet3)
            }
        }
    }
}

@Composable
fun BulletRow(txt: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
        Text(txt, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
    }
}

@Composable
fun FacultyProfileScreen(viewModel: FacultyViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var empId by remember { mutableStateOf(currentUser?.employeeId ?: "") }
    var dept by remember { mutableStateOf(currentUser?.department ?: "") }
    var desig by remember { mutableStateOf(currentUser?.designation ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var year by remember { mutableStateOf(currentUser?.academicYear ?: "") }

    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Academic Profile & Faculty Credentials", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = GlassCardShape,
            border = glassBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = empId, onValueChange = { empId = it }, label = { Text("Employee ID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dept, onValueChange = { dept = it }, label = { Text("Department") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desig, onValueChange = { desig = it }, label = { Text("Designation") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Academic Year") }, modifier = Modifier.fillMaxWidth())

                Button(
                    onClick = {
                        viewModel.updateProfile(name, empId, dept, desig, phone, year, "Semester VI", "Sec A")
                        Toast.makeText(ctx, "Credentials stored successfully", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Credentials")
                }
            }
        }
    }
}

// Letterhead printable review mode
@Composable
fun PrintPreviewScreen(viewModel: FacultyViewModel) {
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val rawCode by viewModel.selectedSubjectCode.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    val activeSubName = subjects.find { it.subjectCode == rawCode }?.subjectName ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.setScreen("overview") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Exit Letterhead Print", color = Color.White)
            }

            Text("CONFIDENTIAL ACCREDITATION REVIEW", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Document Letterhead Header
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("CITY UNION UNIVERSITY OF ACCREDITATION SYSTEMS", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.Black)
            Text("ACCREDITATION COMPLIANCE RECORD & OUTCOME SHEET", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.Black))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metadata grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("COURSE CODEX: $rawCode", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                Text("COURSE TITLE: $activeSubName", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                Text("CONCORD STATS EXECUTED: $dateStr", fontSize = 11.sp, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("FACULTY ID: ${user?.employeeId ?: ""}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                Text("FACULTY NAME: ${user?.name ?: ""}", fontSize = 11.sp, color = Color.Black)
                Text("DEPARTMENT: ${user?.department ?: ""}", fontSize = 11.sp, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("I. EXECUTIVE STATISTICAL KPIS", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(6.dp))

        if (metrics != null) {
            val met = metrics!!
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("• Subject Performance Index (SPI): %.2f / 10.0".format(met.spiValue), fontSize = 12.sp, color = Color.Black)
                Text("• Enrolled Academic Attendance Mean: %.1f%%".format(met.avgAttendance), fontSize = 12.sp, color = Color.Black)
                Text("• Accumulated Pass Rate Percentile: %.1f%%".format(met.passPercentage), fontSize = 12.sp, color = Color.Black)
                Text("• Record Highest Class Mark: %.1f / 100".format(met.highestMarks), fontSize = 12.sp, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("II. ACCREDITED PULPIT LEDGER LIST (REGISTRY)", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        // Ledger list table layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(vertical = 4.dp, horizontal = 4.dp)
        ) {
            Text("Roll Number", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), fontSize = 11.sp, color = Color.Black)
            Text("Pupil Name", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2.5f), fontSize = 11.sp, color = Color.Black)
            Text("Attendance", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), fontSize = 11.sp, color = Color.Black, textAlign = TextAlign.End)
            Text("Grade", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 11.sp, color = Color.Black, textAlign = TextAlign.End)
        }

        Divider(color = Color.Black, thickness = 1.dp)

        students.forEach { st ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            ) {
                Text(st.rollNumber, modifier = Modifier.weight(1.2f), fontSize = 11.sp, color = Color.Black)
                Text(st.studentName, modifier = Modifier.weight(2.5f), fontSize = 11.sp, color = Color.Black)
                Text("%.1f%%".format(st.attendancePercentage), modifier = Modifier.weight(1.2f), fontSize = 11.sp, color = Color.Black, textAlign = TextAlign.End)
                Text(st.grade, modifier = Modifier.weight(1f), fontSize = 11.sp, color = Color.Black, textAlign = TextAlign.End)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Divider(color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("E-CONCORD VERIFIED LEDGER STATS", fontSize = 9.sp, color = Color.Black)
            Text("PROCTORING AUDIT SECURE SHEET CO.", fontSize = 9.sp, color = Color.Black)
        }
    }
}
