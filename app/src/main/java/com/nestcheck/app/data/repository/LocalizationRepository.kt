package com.nestcheck.app.data.repository

import com.nestcheck.app.data.model.LanguageOption
import com.nestcheck.app.data.model.LocalizationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalizationRepository @Inject constructor() {

    val supportedLanguages = listOf(
        LanguageOption(code = "en", englishName = "English", nativeName = "English", flagEmoji = "🇺🇸"),
        LanguageOption(code = "es", englishName = "Spanish", nativeName = "Español", flagEmoji = "🇪🇸"),
        LanguageOption(code = "fr", englishName = "French", nativeName = "Français", flagEmoji = "🇫🇷"),
        LanguageOption(code = "de", englishName = "German", nativeName = "Deutsch", flagEmoji = "🇩🇪"),
        LanguageOption(code = "hi", englishName = "Hindi", nativeName = "हिन्दी", flagEmoji = "🇮🇳")
    )

    private val localizedStrings = mapOf(
        "en" to mapOf(
            "dashboard_title" to "Household Telemetry",
            "screen_time" to "Screen Time Analytics",
            "bedtime_curfew" to "Bedtime & Curfew",
            "device_lock" to "Remote Device Lock",
            "emergency_sos" to "Emergency SOS",
            "battery_status" to "Battery Level",
            "device_locked" to "Device Locked by Parent",
            "device_active" to "Online & Monitored",
            "family_sharing" to "Household Co-Parents",
            "language_settings" to "Language & Localization",
            "ai_assistant" to "AI Parenting Advisor",
            "save_changes" to "Save Preferences",
            "active_now" to "Active Now"
        ),
        "es" to mapOf(
            "dashboard_title" to "Telemetría del Hogar",
            "screen_time" to "Análisis del Tiempo de Pantalla",
            "bedtime_curfew" to "Toque de Queda y Hora de Dormir",
            "device_lock" to "Bloqueo Remoto del Dispositivo",
            "emergency_sos" to "SOS de Emergencia",
            "battery_status" to "Nivel de Batería",
            "device_locked" to "Dispositivo Bloqueado por el Padre",
            "device_active" to "En Línea y Monitoreado",
            "family_sharing" to "Co-Padres del Hogar",
            "language_settings" to "Idioma y Localización",
            "ai_assistant" to "Asesor de Crianza IA",
            "save_changes" to "Guardar Preferencias",
            "active_now" to "Activo Ahora"
        ),
        "fr" to mapOf(
            "dashboard_title" to "Télémétrie Familiale",
            "screen_time" to "Temps d'Écran",
            "bedtime_curfew" to "Couvre-feu & Sommeil",
            "device_lock" to "Verrouillage à Distance",
            "emergency_sos" to "SOS d'Urgence",
            "battery_status" to "Niveau de Batterie",
            "device_locked" to "Appareil Verrouillé par le Parent",
            "device_active" to "En Ligne & Surveillé",
            "family_sharing" to "Gestion Familiale Partagée",
            "language_settings" to "Langue & Région",
            "ai_assistant" to "Conseiller Parental IA",
            "save_changes" to "Enregistrer",
            "active_now" to "Actif Maintenant"
        ),
        "de" to mapOf(
            "dashboard_title" to "Haushalts-Telemetrie",
            "screen_time" to "Bildschirmzeit-Statistiken",
            "bedtime_curfew" to "Schlafenszeit & Sperre",
            "device_lock" to "Gerät Aus Der Ferne Sperren",
            "emergency_sos" to "Notfall-SOS",
            "battery_status" to "Akkustand",
            "device_locked" to "Gerät von Eltern Gesperrt",
            "device_active" to "Online & Überwacht",
            "family_sharing" to "Eltern & Aufsichtspersonen",
            "language_settings" to "Sprache & Lokalisierung",
            "ai_assistant" to "KI-Erziehungsberater",
            "save_changes" to "Einstellungen Speichern",
            "active_now" to "Jetzt Aktiv"
        ),
        "hi" to mapOf(
            "dashboard_title" to "पारिवारिक टेलीमेट्री डैशबोर्ड",
            "screen_time" to "स्क्रीन समय विश्लेषण",
            "bedtime_curfew" to "सोने का समय और कर्फ्यू",
            "device_lock" to "रिमोट डिवाइस लॉक",
            "emergency_sos" to "आपातकालीन एसओएस",
            "battery_status" to "बैटरी स्तर",
            "device_locked" to "अभिभावक द्वारा लॉक किया गया",
            "device_active" to "ऑनलाइन और सक्रिय",
            "family_sharing" to "पारिवारिक सह-अभिभावक",
            "language_settings" to "भाषा और क्षेत्रीय सेटिंग्स",
            "ai_assistant" to "एआई पेरेंटिंग सलाहकार",
            "save_changes" to "सेटिंग्स सहेजें",
            "active_now" to "अभी सक्रिय"
        )
    )

    private val _config = MutableStateFlow(
        LocalizationConfig(
            currentLanguageCode = "en",
            use24HourTime = true,
            useMetricSystem = true,
            dateFormatPattern = "DD/MM/YYYY"
        )
    )
    val config: StateFlow<LocalizationConfig> = _config.asStateFlow()

    fun setLanguage(languageCode: String) {
        _config.value = _config.value.copy(currentLanguageCode = languageCode)
    }

    fun toggle24HourTime(enabled: Boolean) {
        _config.value = _config.value.copy(use24HourTime = enabled)
    }

    fun toggleMetricSystem(enabled: Boolean) {
        _config.value = _config.value.copy(useMetricSystem = enabled)
    }

    fun getString(key: String): String {
        val lang = _config.value.currentLanguageCode
        return localizedStrings[lang]?.get(key)
            ?: localizedStrings["en"]?.get(key)
            ?: key
    }
}
