package give.away.good.deeds.ui.screens.main.post.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import give.away.good.deeds.ui.screens.app_common.PagerIndicator
import give.away.good.deeds.ui.screens.main.post.media.ImageFullScreenView


@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PostImageCarousel(
    imageList: List<String>,
    modifier: Modifier = Modifier,
    showFullImage: Boolean = false
) {
    val pageCount = imageList.size
    val pagerState = rememberPagerState { pageCount }
    Box(
        modifier = modifier
    ) {

        val showDialog = remember { mutableStateOf("") }
        if (showDialog.value.isNotBlank())
            Dialog(
                onDismissRequest = {
                    showDialog.value = ""
                },
                content = {
                    ImageFullScreenView(
                        imageUrl = showDialog.value,
                    )
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                ),
            )

        HorizontalPager(
            state = pagerState
        ) { pageIndex ->
            var modifier1 = Modifier.fillMaxSize()
            if (showFullImage) {
                modifier1 = modifier1.clickable {
                    showDialog.value = imageList[pageIndex]
                }
            }

            Box(
                modifier = modifier1,
                contentAlignment = Alignment.TopCenter
            ) {
                AsyncImage(
                    model = imageList[pageIndex],
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                )
            }
        }

        if (pageCount > 1) {
            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {

                PagerIndicator(
                    pagerState = pagerState,
                    indicatorCount = pageCount,
                    activeColor = MaterialTheme.colorScheme.primary,
                    inActiveColor = Color.White
                )
            }
        }
    }
}