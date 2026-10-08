package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.data.network.GeminiService
import com.example.data.repository.EventosRepository
import com.example.ui.components.EventosAppHeader
import com.example.ui.screens.*
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PrimaryIndigoLight

enum class NavDestination(val label: String, val icon: ImageVector) {
  HOME("Home", Icons.Default.Dashboard),
  CONTROL_ROOM("Command", Icons.Default.Sensors),
  MARKETPLACE("Gigs", Icons.Default.WorkOutline),
  WORKSPACE("Workspace", Icons.Default.Assignment),
  VENDORS("Vendors", Icons.Default.Storefront),
  FINANCE("Finance", Icons.Default.AccountBalanceWallet),
  PASSPORT("Passport", Icons.Default.Badge),
  PROFILE("Profile", Icons.Default.Person),
  AI_SUITE("AI Suite", Icons.Default.AutoAwesome)
}

@Composable
fun EventosMainApp(
  repository: EventosRepository,
  geminiService: GeminiService,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentUser by repository.currentUser.collectAsStateWithLifecycle()
  val events by repository.events.collectAsStateWithLifecycle()
  val opportunities by repository.opportunities.collectAsStateWithLifecycle()
  val vendors by repository.vendors.collectAsStateWithLifecycle()
  val quotations by repository.quotations.collectAsStateWithLifecycle()
  val attendance by repository.attendance.collectAsStateWithLifecycle()
  val incidents by repository.incidents.collectAsStateWithLifecycle()
  val tasks by repository.tasks.collectAsStateWithLifecycle()
  val budgetSummary by repository.budgetSummary.collectAsStateWithLifecycle()
  val payouts by repository.payouts.collectAsStateWithLifecycle()
  val passportStamps by repository.passportStamps.collectAsStateWithLifecycle()
  val isFirestoreLive by repository.isFirestoreLive.collectAsStateWithLifecycle()
  val isSyncing by repository.isSyncing.collectAsStateWithLifecycle()
  val reviews by repository.reviews.collectAsStateWithLifecycle()
  val bookings by repository.bookings.collectAsStateWithLifecycle()

  var currentNav by remember { mutableStateOf(NavDestination.HOME) }
  var showRoleSwitchDialog by remember { mutableStateOf(false) }

  // Handle system back button to always return to HOME first if in secondary screen
  BackHandler(enabled = currentNav != NavDestination.HOME) {
    currentNav = NavDestination.HOME
  }

  Scaffold(
    topBar = {
      EventosAppHeader(
        currentRole = currentUser.role,
        onRoleClick = { showRoleSwitchDialog = true },
        onAIAssistantClick = { currentNav = NavDestination.AI_SUITE },
        onProfileClick = { currentNav = NavDestination.PROFILE }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = DarkSurfaceElevated,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("eventos_bottom_nav")
      ) {
        val navItems = listOf(
          NavDestination.HOME,
          NavDestination.CONTROL_ROOM,
          NavDestination.MARKETPLACE,
          NavDestination.WORKSPACE,
          NavDestination.VENDORS,
          NavDestination.FINANCE,
          NavDestination.PASSPORT,
          NavDestination.PROFILE,
          NavDestination.AI_SUITE
        )
        navItems.forEach { destination ->
          NavigationBarItem(
            selected = currentNav == destination,
            onClick = { currentNav = destination },
            icon = {
              Icon(
                imageVector = destination.icon,
                contentDescription = destination.label,
                modifier = Modifier.size(20.dp)
              )
            },
            label = {
              Text(
                text = destination.label,
                fontSize = 10.sp,
                fontWeight = if (currentNav == destination) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = PrimaryIndigoLight,
              selectedTextColor = PrimaryIndigoLight,
              indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            modifier = Modifier.testTag("nav_${destination.name.lowercase()}")
          )
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentNav) {
        NavDestination.HOME -> {
          HomeScreen(
            user = currentUser,
            events = events,
            opportunities = opportunities,
            isFirestoreLive = isFirestoreLive,
            isSyncing = isSyncing,
            onRefreshFirestore = { repository.refreshOpportunitiesFromFirestore() },
            onPostNewGig = { newOpp -> repository.postNewGigToFirestore(newOpp) },
            onBookGig = { opp -> repository.bookGig(opp) },
            onNavigateToControlRoom = { currentNav = NavDestination.CONTROL_ROOM },
            onNavigateToMarketplace = { currentNav = NavDestination.MARKETPLACE },
            onNavigateToWorkspace = { currentNav = NavDestination.WORKSPACE },
            onNavigateToPassport = { currentNav = NavDestination.PASSPORT },
            onNavigateToAI = { currentNav = NavDestination.AI_SUITE },
            onNavigateToProfile = { currentNav = NavDestination.PROFILE },
            onApplyOpportunity = { oppId ->
              repository.applyForOpportunity(oppId)
              Toast.makeText(context, "Application submitted for gig! Awaiting supervisor shortlist.", Toast.LENGTH_SHORT).show()
            },
            onLeaveReview = { gigId, eventTitle, role, rating, text, orgName, orgOrg, punctual, tech, team ->
              repository.submitReview(
                gigId = gigId,
                eventTitle = eventTitle,
                professionalId = currentUser.id,
                professionalName = currentUser.name,
                organizerId = "org_client_bengaluru",
                organizerName = orgName,
                organizerOrganization = orgOrg,
                roleExecuted = role,
                rating = rating,
                reviewText = text,
                punctualScore = punctual,
                technicalCompetenceScore = tech,
                teamworkScore = team
              )
            }
          )
        }
        NavDestination.CONTROL_ROOM -> {
          val liveEvent = events.firstOrNull { it.status == com.example.data.model.EventStatus.LIVE } ?: events.first()
          ControlRoomScreen(
            event = liveEvent,
            attendanceList = attendance,
            incidents = incidents,
            onCheckInWorker = { name, method ->
              repository.checkInWorker(name, method)
              Toast.makeText(context, "Checked in $name via $method!", Toast.LENGTH_SHORT).show()
            },
            onLogIncident = { title, cat, sev, action ->
              repository.logIncident(title, cat, sev, action)
              Toast.makeText(context, "Incident recorded & dispatched to desk!", Toast.LENGTH_SHORT).show()
            },
            onResolveIncident = { id, notes ->
              repository.resolveIncident(id, notes)
              Toast.makeText(context, "Incident marked resolved!", Toast.LENGTH_SHORT).show()
            },
            onNavigateToStaffReplacement = {
              currentNav = NavDestination.MARKETPLACE
              Toast.makeText(context, "Staff replacement engine: 6 standby candidates available.", Toast.LENGTH_LONG).show()
            }
          )
        }
        NavDestination.MARKETPLACE -> {
          MarketplaceScreen(
            opportunities = opportunities,
            onApply = { oppId ->
              repository.applyForOpportunity(oppId)
              Toast.makeText(context, "1-Click Application recorded in Firestore!", Toast.LENGTH_SHORT).show()
            },
            onBookGig = { opp ->
              repository.bookGig(opp)
              Toast.makeText(context, "Gig booked! Request saved to Firestore 'bookings' collection.", Toast.LENGTH_SHORT).show()
            }
          )
        }
        NavDestination.WORKSPACE -> {
          val currentEvent = events.first()
          EventWorkspaceScreen(
            event = currentEvent,
            tasks = tasks,
            onToggleTask = { taskId -> repository.toggleTask(taskId) },
            onCloneEvent = {
              Toast.makeText(context, "Cloned event template with WBS, vendors and safety checklists!", Toast.LENGTH_LONG).show()
            }
          )
        }
        NavDestination.VENDORS -> {
          VendorProcurementScreen(
            vendors = vendors,
            quotations = quotations,
            onDraftRFQWithAI = {
              currentNav = NavDestination.AI_SUITE
            }
          )
        }
        NavDestination.FINANCE -> {
          FinanceScreen(
            budget = budgetSummary,
            payouts = payouts,
            onDisbursePayout = { payoutId ->
              repository.disbursePayout(payoutId)
            },
            onExplainFinanceWithAI = {
              currentNav = NavDestination.AI_SUITE
            }
          )
        }
        NavDestination.PASSPORT -> {
          PassportScreen(
            user = currentUser,
            stamps = passportStamps,
            onUpgradeCareerWithAI = {
              currentNav = NavDestination.AI_SUITE
            }
          )
        }
        NavDestination.PROFILE -> {
          ProfileScreen(
            user = currentUser,
            reviews = reviews,
            bookings = bookings,
            onSaveProfile = { bio, skills, portfolio, title, zone, rate ->
              repository.updateUserProfile(
                bio = bio,
                skills = skills,
                portfolio = portfolio,
                title = title,
                primaryZone = zone,
                hourlyRateINR = rate
              )
            },
            onAddPortfolioItem = { item ->
              repository.addPortfolioItem(item)
            },
            onRemovePortfolioItem = { itemId ->
              repository.removePortfolioItem(itemId)
            },
            onLeaveReview = { gigId, eventTitle, role, rating, text, orgName, orgOrg, punctual, tech, team ->
              repository.submitReview(
                gigId = gigId,
                eventTitle = eventTitle,
                professionalId = currentUser.id,
                professionalName = currentUser.name,
                organizerId = "org_client_bengaluru",
                organizerName = orgName,
                organizerOrganization = orgOrg,
                roleExecuted = role,
                rating = rating,
                reviewText = text,
                punctualScore = punctual,
                technicalCompetenceScore = tech,
                teamworkScore = team
              )
            }
          )
        }
        NavDestination.AI_SUITE -> {
          AIAgentsScreen(geminiService = geminiService)
        }
      }
    }
  }

  // Role Switcher Dialog
  if (showRoleSwitchDialog) {
    AlertDialog(
      onDismissRequest = { showRoleSwitchDialog = false },
      title = { Text("Switch Operating Role", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Experience Eventos Omega from different persona lenses across the Bangalore ecosystem:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          UserRole.values().forEach { r ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (currentUser.role == r) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  repository.switchUserRole(r)
                  showRoleSwitchDialog = false
                  Toast.makeText(context, "Switched role to ${r.label}", Toast.LENGTH_SHORT).show()
                }
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(text = r.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text(text = "Persona Badge: ${r.badge}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (currentUser.role == r) {
                  Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryIndigoLight)
                }
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showRoleSwitchDialog = false }) {
          Text("Done")
        }
      }
    )
  }
}
