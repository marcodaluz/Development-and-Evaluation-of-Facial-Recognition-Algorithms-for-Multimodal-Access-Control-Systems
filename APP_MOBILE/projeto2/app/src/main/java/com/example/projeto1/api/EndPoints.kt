package com.example.projeto1.api


import com.example.projeto1.api.Attendance
import com.example.projeto1.dataclasses.LoginRequest
import com.example.projeto1.dataclasses.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
interface EndPoints {
    @GET("/users/")
    fun getUsers(): Call<List<Attendance>>

    @GET("/attendance/{id}")
    fun getUserById(@Path("id") id: Int):  Call<List<Attendance>>

    @GET("/outs/{id}")
    fun getOutById(@Path("id") id: Int):  Call<List<Out>>

    @GET("/RoomAcess/{id}")
    fun getRoomAcessById(@Path("id") id: Int):  Call<List<RoomAcess>>

    @POST("/loginapp")
    fun loginUser(@Body loginRequest: LoginRequest): Call<User>

    @DELETE("/users/{id}")
    fun deleteUser(@Path("id") id: Int): Call<Void>


    @FormUrlEncoded
    @POST("/posts")
    fun postTest(@Field("title") title: String?): Call<OutputPost>
}
