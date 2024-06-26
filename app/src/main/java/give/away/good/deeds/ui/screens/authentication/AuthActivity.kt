package give.away.good.deeds.ui.screens.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import give.away.good.deeds.R
import give.away.good.deeds.messaging.unsubscribeFromTopic
import give.away.good.deeds.ui.screens.authentication.module.authModule
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

/**
 * AuthActivity
 * @author Hitesh
 * @since 02.09.2023
 */
class AuthActivity : AppCompatActivity(R.layout.activity_auth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(authModule)

        unsubscribeFromTopic()
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(authModule)
    }

}