package give.away.good.deeds.ui.screens.main.post.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import give.away.good.deeds.R
import give.away.good.deeds.network.model.PostInfo
import give.away.good.deeds.ui.screens.app_common.ErrorStateView
import give.away.good.deeds.ui.screens.app_common.LottieAnimationView
import give.away.good.deeds.ui.screens.app_common.NoInternetStateView
import give.away.good.deeds.ui.screens.app_common.ProfileAvatar
import give.away.good.deeds.ui.screens.app_common.SimpleAlertDialog
import give.away.good.deeds.ui.screens.main.post.list.PostImageCarousel
import give.away.good.deeds.ui.screens.main.setting.location.LoadingView
import give.away.good.deeds.ui.screens.state.AppState
import give.away.good.deeds.ui.screens.state.ErrorCause
import give.away.good.deeds.ui.theme.AppThemeButtonShape
import give.away.good.deeds.utils.TimeAgo
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    navController: NavController? = null,
    viewModel: PostDetailViewModel = koinViewModel()
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Post Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController?.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back Arrow"
                    )
                }
            },
        )
    }) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            LaunchedEffect(Unit, block = {
                viewModel.getPost(postId)
            })

            val uiState = viewModel.uiState.collectAsState()
            when (val state = uiState.value) {
                is AppState.Result<PostInfo> -> {
                    val postInfo = state.data
                    if (postInfo == null) {
                        LaunchedEffect(Unit) {
                            navController?.popBackStack()
                        }
                    } else if (postInfo.chatGroupId != null) {
                        LaunchedEffect(Unit) {
                            navController?.navigate("chat/" + postInfo.chatGroupId)
                            postInfo.chatGroupId = null
                        }
                    } else {
                        PostDetailActionView(
                            postInfo = postInfo,
                        )
                    }
                }

                is AppState.Loading -> {
                    LoadingView()
                }

                is AppState.Error -> {
                    when (state.cause) {
                        ErrorCause.NO_INTERNET -> {
                            NoInternetStateView {
                                viewModel.getPost(postId)
                            }
                        }

                        ErrorCause.UNKNOWN -> {
                            ErrorStateView(
                                title = "Couldn't Load Post!",
                                message = state.message,
                            ) {
                                viewModel.getPost(postId)
                            }
                        }

                        else -> {
                            LottieAnimationView(
                                resId = R.raw.animation_no_result
                            )
                        }
                    }
                }

                is AppState.Ideal -> {
                    // do nothing
                }
            }
        }
    }
}

@Composable
fun PostDetailActionView(
    postInfo: PostInfo,
    viewModel: PostDetailViewModel = koinViewModel()
) {
    val post = postInfo.post
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                PostImageCard(postInfo)
            }

            item {
                PostDetailView(postInfo)
            }

            val location = post.location
            if (location != null) {
                item {
                    GoogleMapView(
                        defaultLatLng = post.location,
                        address = post.address ?: "",
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(96.dp))
            }
        }

        val showCloseDialog = remember { mutableStateOf(false) }
        if (showCloseDialog.value)
            SimpleAlertDialog(
                title = "Close Give Away?",
                message = "Are you sure you want to close this give away?",
                confirmAction = "Yes",
                dismissAction = "No",
                onDismiss = {
                    showCloseDialog.value = false
                },
                onConfirm = {
                    viewModel.setPostStatus(post, 0)
                }
            )

        val showRequestDialog = remember { mutableStateOf(false) }
        if (showRequestDialog.value)
            SimpleAlertDialog(
                title = "Request \"${post.title}\"",
                message = "Are you sure you want to request this give away?",
                confirmAction = "Yes",
                dismissAction = "No",
                onDismiss = {
                    showRequestDialog.value = false
                },
                onConfirm = {
                    viewModel.sendRequest(postInfo)
                }
            )

        if (viewModel.isMyPost(post)) {
            if (!post.isClosed()) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    shape = AppThemeButtonShape,
                    onClick = {
                        showCloseDialog.value = true
                    },
                ) {
                    Text(
                        text = "Close".uppercase(),
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        } else {
            val alreadyRequested = viewModel.isAlreadyRequested(post)
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                shape = AppThemeButtonShape,
                onClick = {
                    showRequestDialog.value = true
                },
            ) {
                val text = if (alreadyRequested) {
                    "Send Message"
                } else {
                    "Request Item"
                }
                Text(
                    text = text.uppercase(),
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
    }
}

@Composable
fun PostImageCard(
    postInfo: PostInfo
) {
    Card {
        PostImageCarousel(
            imageList = postInfo.post.images,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            showFullImage = true
        )
    }
}

@Composable
fun PostDetailView(
    postInfo: PostInfo,
) {
    val post = postInfo.post
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {

                ProfileAvatar(
                    profileUrl = postInfo.user?.profilePic ?: "",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp)),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        postInfo.user?.getName() ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Added " + TimeAgo.timeAgo(post.createdDateTime.time),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                post.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                post.description,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun GoogleMapView(
    defaultLatLng: LatLng,
    address: String?,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 14f)
    }

    Card(
    ) {
        Column {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Marker(
                    state = MarkerState(position = defaultLatLng),
                    snippet = address ?: "Post location",
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = ""
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        address ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }
    }

}