package com.daviapps.liturgiadiaria.data.model

import com.google.gson.annotations.SerializedName

data class LiturgiaResponse(
    @SerializedName("data") val data: String = "",
    @SerializedName("liturgia") val liturgia: String = "",
    @SerializedName("cor") val cor: String = "",
    @SerializedName("oracoes") val oracoes: Oracoes = Oracoes(),
    @SerializedName("leituras") val leituras: Leituras = Leituras(),
    @SerializedName("antifonas") val antifonas: Antifonas = Antifonas()
)

data class Oracoes(
    @SerializedName("coleta") val coleta: String = "",
    @SerializedName("oferendas") val oferendas: String = "",
    @SerializedName("comunhao") val comunhao: String = ""
)

data class Leituras(
    @SerializedName("primeiraLeitura") val primeiraLeitura: List<Leitura> = emptyList(),
    @SerializedName("salmo") val salmo: List<Salmo> = emptyList(),
    @SerializedName("segundaLeitura") val segundaLeitura: List<Leitura> = emptyList(),
    @SerializedName("evangelho") val evangelho: List<Leitura> = emptyList()
)

data class Leitura(
    @SerializedName("referencia") val referencia: String = "",
    @SerializedName("titulo") val titulo: String = "",
    @SerializedName("texto") val texto: String = ""
)

data class Salmo(
    @SerializedName("referencia") val referencia: String = "",
    @SerializedName("refrao") val refrao: String = "",
    @SerializedName("texto") val texto: String = ""
)

data class Antifonas(
    @SerializedName("entrada") val entrada: String = "",
    @SerializedName("comunhao") val comunhao: String = ""
)
