import android.app.Application
import androidx.work.Configuration

class MyApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(CustomWorkerFactory())
            .build()
}
