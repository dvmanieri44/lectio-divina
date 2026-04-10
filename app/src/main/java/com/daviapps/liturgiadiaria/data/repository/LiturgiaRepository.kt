package com.daviapps.liturgiadiaria.data.repository

import com.daviapps.liturgiadiaria.data.api.RetrofitClient
import com.daviapps.liturgiadiaria.data.model.LiturgiaResponse

class LiturgiaRepository {
    suspend fun getLiturgiaHoje(): Result<LiturgiaResponse> = runCatching {
        RetrofitClient.api.getLiturgiaHoje()
    }
}
