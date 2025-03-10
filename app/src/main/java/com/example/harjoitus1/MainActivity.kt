package com.example.harjoitus1

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import android.content.res.Configuration
import android.graphics.drawable.shapes.OvalShape
import android.renderscript.Sampler
import android.util.Log
import android.widget.Space
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.example.harjoitus1.ui.theme.Harjoitus1Theme
import kotlinx.serialization.Serializable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.flow.asFlow
import java.io.File
import java.io.FileOutputStream


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called!")
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            Harjoitus1Theme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()) {
                MainApp(viewModel = viewModel)
                //Conversation(SampleData.conversationSample)
                }
            }
        }
    }
}

data class Message(val author: String, val body: String)

@Serializable
object MessageCard

@Serializable
object ProfileData

@Serializable
object Conversation2

@Composable
fun MessageCardScreen(
    msg: Message,
    viewModel: MainViewModel,
    onNavigateToProfileData: () -> Unit,
    ) {

    val user by viewModel.userStateFlow.collectAsState()

    val context = LocalContext.current

    val profilePicUri = remember(user?.profilePicUri) {
        user?.profilePicUri?.let { Uri.parse(it) }
    }

    var username by remember { mutableStateOf(user?.userName ?: "Cirno") }

    // Add padding around message
    Row(modifier = Modifier.padding(all = 8.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(profilePicUri)
                .crossfade(true)
                .build(),
            fallback = painterResource(R.drawable.cirn),
            contentDescription = "Contact profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                // Set image size to 50 dp
                .size(50.dp)
                // Clip image to be shaped as a circle
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        // Add a horizontal space between the image and the column
        Spacer(modifier = Modifier.width(8.dp))

        // We keep track if the message is expanded or not in this variable
        var isExpanded by remember { mutableStateOf(false) }

        // surfaceColor will be updated gradually from one color to another
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )

        // We toggle the isExpanded variable when we click on this Column
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded} ) {
            Text(
                text = username,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            // Add a vertical space between the author and message
            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                // surfaceColor will be changing gradually from primary to surface
                color = surfaceColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    // If the message is expanded, we display all its content
                    // otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ProfileDataScreen(
    onSaveUser: (String, Uri?) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel
) {
    fun getUriFromFile(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it)}
    }

    val user by viewModel.userStateFlow.collectAsState()

    val context = LocalContext.current

    val profilePicUri = remember(user?.profilePicUri) {
        user?.profilePicUri?.let { Uri.parse(it) }
    }

    var username by remember { mutableStateOf(user?.userName ?: "Cirno") }
    var isEditing by remember { mutableStateOf(false) }

    //add picking media
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            viewModel.saveUser(username, uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }


    // Main Column to hold top bar and profile
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar with button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), //top bar height
            color = MaterialTheme.colorScheme.primary
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = {
                        Log.d("ProfileDataScreen", "Saving user: name=$username, imageUri=$profilePicUri")
                    onSaveUser(username, profilePicUri)
                    onNavigateBack() },
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.surface)
                ) {
                    Text("Back")
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(profilePicUri)
                    .crossfade(true)
                    .build(),
                fallback = painterResource(R.drawable.cirn),
                contentDescription = "Contact profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) }

                /*
                painter = painterResource(R.drawable.cirn),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) }
                 */

            )
            Spacer(modifier = Modifier.height(16.dp))

            // Button layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.align(Alignment.CenterHorizontally)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Profile Name
                    if (isEditing) {
                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    } else {
                        Text(
                            text = username,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    TextButton(
                        onClick = { isEditing = !isEditing },
                        //shape = MaterialTheme.shapes.small,
                        //colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                        //contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(if (isEditing) "Save" else "Edit")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Phone: 040 XXXXXXX",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ConversationScreen(
    messages: List<Message>,
    viewModel: MainViewModel,
    onNavigateToProfileData: () -> Unit
) {
    // Main column to hold top bar and conversation
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar with button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), //top bar height
            color = MaterialTheme.colorScheme.primary
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Button inside top bar
                Button(
                    onClick = { onNavigateToProfileData() },
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.surface)
                ) {
                    Text("View Profile")
                }
            }
        }
        // LazyColumn with messages
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp) // Padding to avoid content being hidden behind the top bar
        ) {
            items(messages) { message ->
                MessageCardScreen(message, viewModel = viewModel, onNavigateToProfileData = { })
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel) {
    // Init navController
    val navController = rememberNavController()
    //val user by viewModel.userStateFlow.collectAsState()
    NavHost(navController, startDestination = "conversation2") {
        composable("conversation2") {
            //val currentDestination: Conversation2 = backStackEntry.toRoute()
            ConversationScreen(
                SampleData.conversationSample,
                viewModel = viewModel,
                onNavigateToProfileData = {
                    navController.navigate("profileData")
                }
            )
        }
    /*    composable("messageCard") {
            MessageCardScreen(
                msg = Message("Cirno", "Hey, take a look at Jetpack Compose"),
                onNavigateToProfileData = {
                    navController.navigate(
                        "profileData"
                    )
                }
            )
        }*/
        composable("profileData") {
            ProfileDataScreen(
                onSaveUser = { name, imageUri -> viewModel.saveUser(name, imageUri) },
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}

/*
@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun MessageCardPreview() {
    Harjoitus1Theme {
        Surface {
            MessageCardScreen(
                msg = Message("Cirno", "Hey, take a look at Jetpack Compose"), {  })
        }
    }
}


@Preview
@Composable
fun ProfileDataPreview() {
    Harjoitus1Theme {
        ProfileDataScreen(
            msg = Message("Cirno", "Hello there"), {})
    }
}



@Preview
@Composable
fun ConversationPreview() {
    Harjoitus1Theme {
        ConversationScreen(SampleData.conversationSample, {})
    }
}
*/