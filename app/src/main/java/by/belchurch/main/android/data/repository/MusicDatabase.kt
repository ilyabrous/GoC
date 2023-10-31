package by.belchurch.main.android.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import by.belchurch.main.android.data.local.models.Song
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicDatabase @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection("songs")

    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch(e: Exception) {
            emptyList()
        }
    }
}