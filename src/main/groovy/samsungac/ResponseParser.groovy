package samsungac


class ResponseParser {

    static boolean isResponseWithToken(String response) {
        response =~ /Token="(.*)"/
    }

    static def parseTokenFromResponse(def response) {
        def matcher = response =~ /Token="(.*)"/
        matcher[0][1]
    }

    static boolean isFailedAuthenticationResponse(line) {
        line == '<?xml version="1.0" encoding="utf-8" ?><Response Status="Fail" Type="Authenticate" ErrorCode="301" />'
    }

    static boolean isFailedResponse(line) {
        line == '<?xml version="1.0" encoding="utf-8" ?><Response Status="Fail" Type="Authenticate" ErrorCode="301" />'
    }

    static boolean isCorrectCommandResponse(def line, def commandId) {
        line =~ /CommandID="$commandId"/
    }

    static boolean isSuccessfulLoginResponse(line) {
        line =~ /Response Type="AuthToken" Status="Okay"/
    }

    static boolean isFirstLine(line) {
        line == 'DRC-1.00'
    }

    static boolean isNotLoggedInResponse(line) {
        line == '<?xml version="1.0" encoding="utf-8" ?><Update Type="InvalidateAccount"/>'
    }

    static boolean isReadyForTokenResponse(line) {
        line == '<?xml version="1.0" encoding="utf-8" ?><Response Type="GetToken" Status="Ready"/>'
    }

    static boolean isDeviceControl(line) {
        line =~ /Response Type="DeviceControl"/
    }

    static boolean isDeviceState(line) {
        line =~ /Response Type="DeviceState" Status="Okay"/
    }

    static String getStatusValue(line) {
        (line =~ /Status="(\w*)"/)[0][1]
    }

    static Map parseStatusResponse(String response) {
        def records = new XmlSlurper().parseText(response)
        Map status = [:]
        records.DeviceState.Device.children().each {
            def value = (it.@Value as String).isInteger() ? (it.@Value as String) as Integer : it.@Value as String
            status.put(it.@ID as String, value)
        }
        println status
        status
    }

}
