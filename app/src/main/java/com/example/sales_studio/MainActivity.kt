package com.example.sales_studio

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.sales_studio.ui.theme.Sales_StudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sales_StudioTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KidsTVLauncher(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun KidsTVLauncher(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val approvedApps = remember {
        listOf(
            "com.youtube.kids",
            "com.netflix.mediaclient",
            "com.pbskids.video"
        )
    }

    val packageManager = context.packageManager
    val allInstalledApps = remember {
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }
    val approvedInstalledApps = remember {
        allInstalledApps
            .filter { it.packageName in approvedApps }
            .map {
                Triple(
                    packageManager.getApplicationLabel(it).toString(),
                    it.packageName,
                    packageManager.getApplicationIcon(it).toBitmap().asImageBitmap()
                )
            }
    }
    val appsToDisplay = if (approvedInstalledApps.isEmpty()) {
        allInstalledApps.map {
            Triple(
                packageManager.getApplicationLabel(it).toString(),
                it.packageName,
                packageManager.getApplicationIcon(it).toBitmap().asImageBitmap()
            )
        }
    } else {
        approvedInstalledApps
    }

    MaterialTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color(0xFF2196F3)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kids TV Launcher",
                    fontSize = 28.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .semantics { contentDescription = "Kids TV Launcher Title" }
                )

                Text(
                    text = "Found ${approvedInstalledApps.size} approved apps",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (appsToDisplay.isEmpty()) {
                    Text(
                        text = "No apps installed on device",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(appsToDisplay) { app ->
                            AppItem(
                                appName = app.first,
                                packageName = app.second,
                                appIcon = app.third
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppItem(appName: String, packageName: String, appIcon: androidx.compose.ui.graphics.ImageBitmap) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                try {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                    if (launchIntent != null) {
                        context.startActivity(launchIntent)
                    } else {
                        Toast.makeText(context, "Unable to launch $appName", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error launching $appName", Toast.LENGTH_SHORT).show()
                }
            }
            .semantics { contentDescription = "Launch $appName" },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = appIcon,
                contentDescription = "$appName icon",
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = appName,
                fontSize = 18.sp,
                color = Color.Black,
                maxLines = 2
            )
        }
    }
}