import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.submissionandroid.dicodingevents.DailyReminderWorker
import com.submissionandroid.dicodingevents.repository.EventsRepository
import com.submissionandroid.dicodingevents.retrofit.ApiConfig

class CustomWorkerFactory : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val apiService = ApiConfig.getApiService()
        val repository = EventsRepository(apiService)

        return when (workerClassName) {
            DailyReminderWorker::class.java.name -> DailyReminderWorker(appContext, workerParameters, repository)
            else -> null
        }
    }
}
