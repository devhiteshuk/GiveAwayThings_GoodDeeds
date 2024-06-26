package give.away.good.deeds.ui.screens.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import give.away.good.deeds.ui.screens.main.messages.detail.MessageDetailScreen
import give.away.good.deeds.ui.screens.main.post.detail.PostDetailScreen
import give.away.good.deeds.ui.screens.main.post.list.PostListScreen
import give.away.good.deeds.ui.theme.AppTheme
import give.away.good.deeds.utils.contentView

/**
 * Home Journey class
 * @author Hitesh
 * @since 02.09.2023
 */
class HomeJourney : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = contentView(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)) {
        AppTheme {
            HomeJourneyScreen()
        }
    }
}


@Composable
fun HomeJourneyScreen(
    navController: NavHostController = rememberNavController()
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                PostListScreen(
                    onPostClick = { post ->
                        navController.navigate("post_detail/${post.id}")
                    }
                )
            }

            composable("post_detail/{postId}") { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId")
                PostDetailScreen(
                    postId = postId ?: "",
                    navController = navController,
                )
            }

            composable("chat/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")
                MessageDetailScreen(
                    groupId = groupId ?: "",
                    navController = navController,
                )
            }
        }
    }
}
