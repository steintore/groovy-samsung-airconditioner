package samsungac

public enum Command {

    AC_FUN_OPMODE(OperationMode),
    AC_FUN_TEMPSET,
    AC_FUN_WINDLEVEL(WindLevel),
    AC_ADD_SPI(OnOff),
    AC_ADD_AUTOCLEAN(AutoClean),
    AC_FUN_TEMPNOW,
    AC_FUN_POWER(OnOff),
    AC_FUN_COMODE(ConvenientMode),
    AC_FUN_DIRECTION(Direction)

    private final def value

    Command(def value = null) {
        this.value = value
    }
}

enum AutoClean {
    On
}

enum ConvenientMode {
    Off, Quiet, Sleep, Smart, SoftCool, TurboMode, WindMode1, WindMode2, WindMode3
}

enum Direction {
    SwingUD, Rotation, Fixed
}

enum OperationMode {
    Auto, Cool, Dry, Wind, Heat
}

enum OnOff {
    On, Off
}

enum WindLevel {
    Auto, Low, Mid, High, Turbo
}