package com.example.data.model

data class AiAgent(
  val id: String,
  val name: String,
  val role: String,
  val tagline: String,
  val description: String,
  val systemPrompt: String,
  val glowColorHex: Long, // Color hex e.g. 0xFF00F5FF
  val starterPrompts: List<String>,
  val tags: List<String>,
  val temperature: Float = 0.7f,
  val imageResId: Int? = null,
  val iconName: String = "smart_toy",
  val isCustom: Boolean = false
)
