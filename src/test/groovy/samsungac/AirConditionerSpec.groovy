package samsungac

import spock.lang.Specification

import javax.net.ssl.HandshakeCompletedListener
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket

class AirConditionerSpec extends Specification {

    def setup() {
        AirConditioner.metaClass.connect = {
            new MockSocket()
        }
    }

    def cleanup() {
        AirConditioner.metaClass = null
    }

    def "Connect and switch temp to 25"() {
        given:
        def aircon = new AirConditioner('192.168.1.140', '7825AD1243BA', '33965903-4482-M849-N716-373832354144')

        when:
        aircon.login()

        and:
        aircon.setTemperature(25)

        and:
        AirConditioner.metaClass.static.readLine = { SSLSocket socket -> return deviceStatus }

        then:
        aircon.status.AC_FUN_TEMPNOW == 25
    }

    def "should connect to air conditioner and get token"() {
        given:
        AirConditioner.metaClass.static.readLine = { SSLSocket socket -> return 'Token="ABCEDEF"' }
        def aircon = new AirConditioner('192.168.1.140', '7A3E4R567T54', null)

        when:
        aircon.login()

        then:
        noExceptionThrown()
        aircon.TOKEN_STRING == 'ABCEDEF'
    }

    class MockSocket extends SSLSocket {

        @Override
        String[] getSupportedCipherSuites() {
            return new String[0]
        }

        @Override
        String[] getEnabledCipherSuites() {
            return new String[0]
        }

        @Override
        void setEnabledCipherSuites(String[] strings) {

        }

        @Override
        String[] getSupportedProtocols() {
            return new String[0]
        }

        @Override
        String[] getEnabledProtocols() {
            return new String[0]
        }

        @Override
        void setEnabledProtocols(String[] strings) {

        }

        @Override
        SSLSession getSession() {
            return null
        }

        @Override
        void addHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {

        }

        @Override
        void removeHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {

        }

        @Override
        void startHandshake() throws IOException {

        }

        @Override
        void setUseClientMode(boolean b) {

        }

        @Override
        boolean getUseClientMode() {
            return false
        }

        @Override
        void setNeedClientAuth(boolean b) {

        }

        @Override
        boolean getNeedClientAuth() {
            return false
        }

        @Override
        void setWantClientAuth(boolean b) {

        }

        @Override
        boolean getWantClientAuth() {
            return false
        }

        @Override
        void setEnableSessionCreation(boolean b) {

        }

        @Override
        boolean getEnableSessionCreation() {
            return false
        }

        private def i
        private def o

        public MockSocket() {
            i = new MockInputStream()
            o = new ByteArrayOutputStream()
        }

        public InputStream getInputStream() { return i }

        public OutputStream getOutputStream() { return o }
    }

/**
 * only needed for workaround in groovy,
 *     new ByteArrayInputStream(myByteArray) doesn't work at mo... (28-Sep-2004)
 */
    class MockInputStream extends InputStream {
        int read() { return -1 }
    }

    def deviceStatus = '<?xml version="1.0" encoding="utf-8" ?><Response Type="DeviceState" Status="Okay"><DeviceState><Device DUID="7825AD1243BA" GroupID="AC" ModelID="AC" ><Attr ID="AC_FUN_ENABLE" Type="RW" Value="Enable"/><Attr ID="AC_FUN_POWER" Type="RW" Value="On"/><Attr ID="AC_FUN_SUPPORTED" Type="R" Value="0"/><Attr ID="AC_FUN_OPMODE" Type="RW" Value="Heat"/><Attr ID="AC_FUN_TEMPSET" Type="RW" Value="21"/><Attr ID="AC_FUN_COMODE" Type="RW" Value="Off"/><Attr ID="AC_FUN_ERROR" Type="RW" Value="00000000"/><Attr ID="AC_FUN_TEMPNOW" Type="R" Value="25"/><Attr ID="AC_FUN_SLEEP" Type="RW" Value="0"/><Attr ID="AC_FUN_WINDLEVEL" Type="RW" Value="Auto"/><Attr ID="AC_FUN_DIRECTION" Type="RW" Value="Fixed"/><Attr ID="AC_ADD_AUTOCLEAN" Type="RW" Value="Off"/><Attr ID="AC_ADD_APMODE_END" Type="W" Value="0"/><Attr ID="AC_ADD_STARTWPS" Type="RW" Value="Direct"/><Attr ID="AC_ADD_SPI" Type="RW" Value="Off"/><Attr ID="AC_SG_WIFI" Type="W" Value="Connected"/><Attr ID="AC_SG_INTERNET" Type="W" Value="Connected"/><Attr ID="AC_ADD2_VERSION" Type="RW" Value="0"/><Attr ID="AC_SG_MACHIGH" Type="W" Value="0"/><Attr ID="AC_SG_MACMID" Type="W" Value="0"/><Attr ID="AC_SG_MACLOW" Type="W" Value="0"/><Attr ID="AC_SG_VENDER01" Type="W" Value="0"/><Attr ID="AC_SG_VENDER02" Type="W" Value="0"/><Attr ID="AC_SG_VENDER03" Type="W" Value="0"/></Device></DeviceState></Response>'
}
