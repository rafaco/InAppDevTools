package es.rafaco.inappdevtools

class InAppDevToolsExtension {

    //User features fags
    String email

    // Build flags (affect plugin)
    Boolean enabled = true
    Boolean enabledOnRelease
    Boolean debug = false
    Boolean sourceInclusion
    Boolean sourceInspection

    // Runtime flags (used later on)
    Boolean overlayEnabled
    Boolean invocationByShake
    Boolean invocationByIcon
    Boolean invocationByNotification
    Boolean callDefaultCrashHandler
}
