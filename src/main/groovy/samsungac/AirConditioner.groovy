package samsungac

import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class AirConditioner {

    private final String IP
    private final String MAC
    private String TOKEN_STRING
    private final Integer PORT = 2878
    private Map<String, Object> statusMap = [:]
    private SSLSocket socket

    def AirConditioner(String ipAddress, String macAddress, String token = null) {
        IP = ipAddress
        MAC = macAddress
        TOKEN_STRING = token
    }

    def login() {
        socket = connect()
        2.times { handleResponse() }
        getToken()
        loginWithToken()
    }

    private def loginWithToken() {
        if (TOKEN_STRING) writeLine("<Request Type=\"AuthToken\"><User Token=\"$TOKEN_STRING\" /></Request>")
        else throw new Exception("Must connect and retrieve a token before login in")

        if (handleResponse() == 'loginSuccess')
            getStatus()
    }

    private def getToken() {
        if (TOKEN_STRING) return
        def result = handleResponse()
        while (result == 'waiting' || TOKEN_STRING == null) {
            result = handleResponse()
            sleep 2000
        }
        println "Token has been acquired: $TOKEN_STRING"
    }

    private def handleResponse(def commandId = null) {
        def line = readLine(socket)

        if (!line || ResponseParser.isFirstLine(line)) {
            return
        }

        if (ResponseParser.isNotLoggedInResponse(line)) {
            if (TOKEN_STRING) return
            return writeLine('<Request Type="GetToken" />')
        }

        if (ResponseParser.isFailedAuthenticationResponse(line)) {
            throw new Exception('failed to connect')
        }

        if (commandId && !ResponseParser.isCorrectCommandResponse(line, commandId)) {
            throw new Exception("wrong response, expected $commandId, but got: $line")
        }

        if (ResponseParser.isResponseWithToken(line)) {
            TOKEN_STRING = ResponseParser.parseTokenFromResponse(line)
            return 'authenticated'
        }
        if (ResponseParser.isReadyForTokenResponse(line)) {
            println 'Switch off and on the air conditioner within 30 seconds'
            return 'waiting'
        }

        if (ResponseParser.isSuccessfulLoginResponse(line)) {
            return 'loginSuccess'
        }

        if (ResponseParser.isDeviceState(line)) {
            statusMap.clear()
            statusMap.putAll(ResponseParser.parseStatusResponse(line))
            return 'deviceState'
        }

        if (ResponseParser.isDeviceControl(line)) {
            return ResponseParser.getStatusValue(line)
        }

        throw new Exception("Response not handled: $line")
    }

    private def writeLine(String line) {
        connect()
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        writer.write(line)
        writer.newLine()
        writer.flush()
        println "WRITE: $line"
    }

    static def readLine(SSLSocket socket) {
        BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            def result = r.readLine()
            println "READ: $result"
            return result
        } catch (SocketTimeoutException e) {
            println e
        }
    }

    private def connect() {
        if (socket && socket.isConnected())
            return
        SSLContext ctx = SSLContext.getInstance("TLS");
        X509TrustManager trustManager = [
                checkClientTrusted: { Object[] params -> null },
                checkServerTrusted: { Object[] params -> null },
                getAcceptedIssuers: { Object[] params -> null } ] as X509TrustManager
        try {
            ctx.init(null, [trustManager] as TrustManager[], null);
            socket = ctx.socketFactory.createSocket(IP, PORT) as SSLSocket
            socket.setSoTimeout(7000)
            socket.startHandshake()
        } catch (Exception e) {
            throw new Exception("Cannot connect to $IP:$PORT", e)
        }
        socket
    }

    private def sendCommand(def command, def value) {
        connect()
        def id = "cmd${Math.round(Math.random() * 10000)}"
        writeLine("<Request Type=\"DeviceControl\"><Control CommandID=\"$id\" DUID=\"${MAC}\"><Attr ID=\"$command\" Value=\"$value\" /></Control></Request>")
        id
    }

    private def handleCommandRequestResponse(def command, def value) {
        handleResponse(sendCommand(command, value))
    }

    def on() {
        handleCommandRequestResponse(Command.AC_FUN_POWER, 'On')
    }

    def off() {
        handleCommandRequestResponse(Command.AC_FUN_POWER, 'Off')
    }

    def setMode(OperationMode mode) {
        handleCommandRequestResponse(Command.AC_FUN_OPMODE, mode.toString())
    }

    def setTemperature(Integer temp) {
        handleCommandRequestResponse(Command.AC_FUN_TEMPSET, temp.toString())
    }

    def getTemperatureNow() {
        handleCommandRequestResponse(Command.AC_FUN_TEMPNOW, '')
    }

    def setConvenientMode(ConvenientMode mode) {
        handleCommandRequestResponse(Command.AC_FUN_COMODE, mode.toString())
    }

    def getStatus() {
        writeLine("<Request Type=\"DeviceState\" DUID=\"${MAC}\"></Request>")
        handleResponse()
        statusMap
    }
}