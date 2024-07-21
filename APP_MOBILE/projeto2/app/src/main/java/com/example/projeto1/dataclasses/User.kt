package com.example.projeto1.dataclasses
import java.io.Serializable


data class LoginRequest(val username: String, val password: String)



data class User(
    val userid: Int,
    val username: String,
    val cargo: String,
    val departamento: String,
    val HoraEntrada: String,
    val HoraSaida: String
) : Serializable
