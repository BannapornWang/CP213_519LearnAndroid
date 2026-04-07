package com.example.a519lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a519lablearnandroid.ui.theme._519LabLearnAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ─── Data ───────────────────────────────────────────────────────────────────

data class Contact(val name: String) {
    val initial: Char get() = name.first().uppercaseChar()
}

// ─── Mock Data Generator ─────────────────────────────────────────────────────

private val PAGE_SIZE = 20

private val ALL_MOCK_NAMES = listOf(
    "Alice Anderson", "Aaron Adams", "Amy Allen",
    "Bob Brown", "Ben Baker", "Barbara Bell",
    "Charlie Clark", "Carol Chen", "Chris Cooper",
    "Diana Davis", "David Dixon", "Dorothy Dean",
    "Eve Evans", "Ethan Ellis", "Emma Edwards",
    "Frank Foster", "Fiona Ford", "Felix Flynn",
    "Grace Green", "George Grant", "Gina Gray",
    "Henry Hall", "Hannah Harris", "Hugo Hill",
    "Iris Ingram", "Ivan Irwin", "Ivy Ireland",
    "Jack Jackson", "Jane James", "Josh Jordan",
    "Karen King", "Kevin Klein", "Kate Knox",
    "Liam Lewis", "Laura Lee", "Leo Lang",
    "Mia Miller", "Mark Moore", "Maya Morgan",
    "Nina Nelson", "Noah Nash", "Nora Norris",
    "Oscar Owen", "Olivia Olsen", "Omar Otto",
    "Paul Parker", "Pam Price", "Pete Perry",
    "Quinn Quick", "Quincy Quest",
    "Rachel Reed", "Ryan Ross", "Rose Ray",
    "Sam Scott", "Sara Shaw", "Steve Stone",
    "Tina Turner", "Tom Taylor", "Tracy Todd",
    "Uma Urban", "Uma Upton",
    "Victor Vance", "Violet Vale", "Vera Voss",
    "Wendy Ward", "Will Webb", "Wade West",
    "Xena Xavier",
    "Yara Young", "Yvonne York",
    "Zoe Zhang", "Zack Zhou", "Zelda Zane"
).sorted()

// ─── ViewModel ───────────────────────────────────────────────────────────────

class ContactViewModel : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentPage = 0
    private var hasMore = true

    init {
        loadMore()
    }

    fun loadMore() {
        if (_isLoading.value || !hasMore) return
        viewModelScope.launch {
            _isLoading.value = true
            delay(2_000) // จำลอง network delay
            val from = currentPage * PAGE_SIZE
            val to = minOf(from + PAGE_SIZE, ALL_MOCK_NAMES.size)
            if (from >= ALL_MOCK_NAMES.size) {
                hasMore = false
            } else {
                val newContacts = ALL_MOCK_NAMES.subList(from, to).map { Contact(it) }
                _contacts.value = _contacts.value + newContacts
                currentPage++
                if (to >= ALL_MOCK_NAMES.size) hasMore = false
            }
            _isLoading.value = false
        }
    }
}

// ─── Activity ────────────────────────────────────────────────────────────────

class Part2Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _519LabLearnAndroidTheme {
                ContactListScreen()
            }
        }
    }
}

// ─── Screen ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(vm: ContactViewModel = viewModel()) {
    val contacts by vm.contacts.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val listState = rememberLazyListState()

    // จัดกลุ่มตามตัวอักษรแรก
    val grouped: Map<Char, List<Contact>> = remember(contacts) {
        contacts.groupBy { it.initial }
    }

    // Pagination trigger: เมื่อ scroll ถึง item สุดท้าย
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible >= totalItems - 1 && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) vm.loadMore()
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Contacts", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            grouped.forEach { (initial, names) ->
                // Sticky Header ตัวอักษร A, B, C ...
                stickyHeader(key = initial) {
                    StickyHeaderItem(initial)
                }
                itemsIndexed(names, key = { _, c -> c.name }) { _, contact ->
                    ContactItem(contact)
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                }
            }

            // Loading indicator ที่ด้านล่างสุด
            if (isLoading) {
                item(key = "loader") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

// ─── Composables ─────────────────────────────────────────────────────────────

@Composable
fun StickyHeaderItem(initial: Char) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = initial.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ContactItem(contact: Contact) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar วงกลม
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = androidx.compose.foundation.shape.CircleShape
                        .let { androidx.compose.foundation.shape.RoundedCornerShape(50) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.initial.toString(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = contact.name,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContactListScreenPreview() {
    _519LabLearnAndroidTheme {
        ContactListScreen()
    }
}
