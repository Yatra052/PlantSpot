package com.example.plantspot.api

import com.example.plantspot.model.CheckStatus
import com.example.plantspot.model.Notification
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {

    @GET("apis/pg-sandbox/pg/v1/status/{merchantId}/{transactionId}")
      suspend fun checkStatus(
          @HeaderMap headers : Map<String, String>,
        @Path("merchantId") merchantId: String,
          @Path("transactionId") transactionId: String
      ): Response<CheckStatus>




      @Headers(
          "Content-Type: application/json",
          "Authorization: key=AAAAIyniEG4:APA91bHzZhf_xgv7llucreNgUf1P0WFdQ_SB9TNGN8FEJeWkW1C4KC9UGmi1xiJ2S0q8aFO9iADVLePhgZ-gw6SC7OPf6l1wZVLD1geffTqP4jujh41zFqVgLAbA1Z8wMoEtTEIuK4cT"

      )

      @POST("fcm/send")

      fun sendNotification(@Body notification: Notification):Call<Notification>



}