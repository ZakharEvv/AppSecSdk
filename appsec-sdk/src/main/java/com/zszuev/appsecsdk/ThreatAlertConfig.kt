package com.zszuev.appsecsdk

data class ThreatAlertConfig(
    val title: String,
    val message: String,
    val buttonText: String = "Понятно",
)