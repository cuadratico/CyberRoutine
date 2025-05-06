package com.cyberoutine.recy_task

data class data_task(var position: Int, var task: String, val habilidad: String, var money: Int, val iv: String)

val task_list = mutableListOf<data_task>()