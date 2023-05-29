package com.shenmali.stumateapp.data.source.remotestorage

import android.net.Uri
import com.shenmali.stumateapp.data.model.ImagePath

interface RemoteStorageRepository {
    suspend fun getDownloadUrl(imagePath: ImagePath): String
    suspend fun uploadImage(uri: Uri, fileName: String): ImagePath
}